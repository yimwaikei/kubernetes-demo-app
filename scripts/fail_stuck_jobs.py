import os
import psycopg2

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

def fail_stuck_jobs():
    try:
        timeout_minutes = int(os.getenv("JOB_TIMEOUT", 15))
        timeout_interval = f"INTERVAL '{timeout_minutes} minutes'"

        conn = get_db_connection()
        cur = conn.cursor()

        cur.execute(f"""
            SELECT id
            FROM jobs
            WHERE status = %s
              AND start_at <= NOW() - {timeout_interval};
        """, (JobStatus.RUNNING,))

        rows = cur.fetchall()
        if not rows:
            print("No running jobs to fail.")
            cur.close()
            conn.close()
            return

        stuck_job_ids = [row[0] for row in rows]
        print(f"Failing running jobs: {stuck_job_ids}")

        cur.execute("""
            UPDATE jobs
            SET status = %s,
                end_at = NOW(),
                error = CASE
                            WHEN error IS NULL OR error = '' THEN 'Job timed out'
                            ELSE error || E'\nJob timed out'
                        END
            WHERE id = ANY(%s::uuid[]);
        """, (JobStatus.FAILED, stuck_job_ids))

        conn.commit()
        cur.close()
        conn.close()

    except Exception as e:
        print(f"Failed to fail stuck jobs: {e}")

if __name__ == "__main__":
    fail_stuck_jobs()
