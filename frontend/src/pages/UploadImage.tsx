import { UploadOutlined } from "@ant-design/icons";
import { Upload, Button, message } from "antd";
import type { RcFile, UploadFile } from "antd/es/upload/interface";
import { useState } from "react";
import { uploadFile } from "../services/FileService";

function UploadImage() {
  const [fileList, setFileList] = useState<UploadFile<RcFile>[]>([]);
  const [loading, setLoading] = useState(false);

  const beforeUpload = (file: RcFile) => {
    const isValidType = file.type === "image/png" || file.type === "image/jpeg";

    if (!isValidType) {
      message.error("Only PNG/JPG files are allowed!");
      return Upload.LIST_IGNORE;
    }

    return false;
  };

  const handleUpload = async () => {
    const fileObj = fileList[0]?.originFileObj;
    if (!fileObj) return;

    setLoading(true);
    try {
      await uploadFile(fileObj);
      message.success("Upload successful!");

      setFileList([]);
    } catch (err) {
      message.error("Upload failed");
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <Upload
        maxCount={1}
        fileList={fileList}
        beforeUpload={beforeUpload}
        onChange={({ fileList }) => setFileList(fileList)}
        accept=".png,.jpg,.jpeg"
      >
        <Button icon={<UploadOutlined />}>Select File</Button>
      </Upload>
      <br />
      {fileList.length > 0 && (
        <Button onClick={handleUpload} loading={loading}>
          Upload to Server
        </Button>
      )}
    </div>
  );
}

export default UploadImage;
