import io
import os
import sys
import psycopg2
import cv2
import numpy as np
from minio import Minio

class JobStatus:
    CREATED = 0
    RUNNING = 1
    COMPLETED = 2
    FAILED = 3

def get_db_connection():
    return psycopg2.connect(
        host=os.getenv("DB_HOST"),
        port=os.getenv("DB_PORT", "5432"),
        dbname=os.getenv("DB_NAME"),
        user=os.getenv("DB_USER"),
        password=os.getenv("DB_PASSWORD")
    )

def update_status(record_id, new_status):
    try:
        conn = get_db_connection()
        cur = conn.cursor()
        cur.execute("UPDATE jobs SET status = %s WHERE id = %s;", (new_status, record_id))
        conn.commit()
        cur.close()
        conn.close()
        print(f"Record ID {record_id} status updated to {new_status}")
    except Exception as e:
        print(f"Failed to update status for record {record_id}: {e}")

def fetch_record(record_id):
    conn = get_db_connection()
    cur = conn.cursor()
    cur.execute("SELECT * FROM jobs WHERE id = %s;", (record_id,))
    row = cur.fetchone()
    if not row:
        cur.close()
        conn.close()
        return None
    col_names = [desc[0] for desc in cur.description]
    record = dict(zip(col_names, row))
    cur.close()
    conn.close()
    return record

def get_file_path(record):
    metadata = record.get("metadata")
    if metadata:
        return metadata.get("filePath")
    return None

def get_minio_client():
    endpoint = f"{os.getenv('MINIO_HOST')}:{os.getenv('MINIO_PORT', '9000')}"
    return Minio(
        endpoint,
        access_key=os.getenv("MINIO_USER"),
        secret_key=os.getenv("MINIO_PASSWORD"),
        secure=False  # Set to True if using HTTPS
    )

def parse_minio_path(file_path):
    parts = file_path.split("/", 1)
    if len(parts) != 2:
        raise ValueError(f"Invalid MinIO path: {file_path}")

    bucket = parts[0]
    object_name = parts[1]
    return bucket, object_name

def download_from_minio(bucket, object_name):
    client = get_minio_client()

    response = client.get_object(bucket, object_name)

    try:
        data = response.read()
        return data
    finally:
        response.close()
        response.release_conn()

def transform_file(file_bytes):
    np_arr = np.frombuffer(file_bytes, np.uint8)

    img = cv2.imdecode(np_arr, cv2.IMREAD_COLOR)

    if img is None:
        raise ValueError("Failed to decode image")

    gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)

    success, encoded_img = cv2.imencode(".png", gray)

    if not success:
        raise ValueError("Failed to encode grayscale image")

    return encoded_img.tobytes()

def upload_to_minio(bucket, object_name, data_bytes):
    client = get_minio_client()

    data_stream = io.BytesIO(data_bytes)

    client.put_object(
        bucket,
        object_name,
        data_stream,
        length=len(data_bytes),
        content_type="application/octet-stream"
    )

    print(f"Uploaded transformed file to {bucket}/{object_name}")

def main():
    record_id = os.getenv("RECORD_ID")
    if len(sys.argv) > 1:
        record_id = sys.argv[1]

    if not record_id:
        print("RECORD_ID not provided")
        sys.exit(1)

    update_status(record_id, JobStatus.RUNNING)

    try:
        record = fetch_record(record_id)
        if not record:
            print(f"No record found with id={record_id}")
            update_status(record_id, JobStatus.FAILED)
            sys.exit(0)

        file_path = get_file_path(record)
        if not file_path:
            print(f"No filePath in metadata for record {record_id}")
            update_status(record_id, JobStatus.FAILED)
            sys.exit(0)


        bucket, object_name = parse_minio_path(file_path)
        file_bytes = download_from_minio(bucket, object_name)
        transformed_bytes = transform_file(file_bytes)
        filename, ext = os.path.splitext(object_name)
        new_object = f"{filename}-grayscale{ext}"
        upload_to_minio(bucket, new_object, transformed_bytes)

        update_status(record_id, JobStatus.COMPLETED)

    except Exception as e:
        print(f"Error during job execution: {e}")
        update_status(record_id, JobStatus.FAILED)
        sys.exit(1)

if __name__ == "__main__":
    main()
