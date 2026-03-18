import { UploadOutlined } from "@ant-design/icons";
import { Upload, Button, message, Input, Typography, Space } from "antd";
import type { RcFile, UploadFile } from "antd/es/upload/interface";
import { useState } from "react";
import { uploadFile } from "../services/FileService";
import type { UploadFileResponse } from "../models/file";
import { createJob } from "../services/JobService";
import type { CreateJobResponse } from "../models/job";

function UploadImage() {
  const [fileList, setFileList] = useState<UploadFile<RcFile>[]>([]);
  const [loading, setLoading] = useState(false);
  const [filePath, setFilePath] = useState<string>();

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
      const res: UploadFileResponse = await uploadFile(fileObj);
      message.success("Upload successful!");

      setFileList([])
      setFilePath(res.filePath);
    } catch (err) {
      message.error("Upload failed");
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleRunJob = async () => {
    if (!filePath) return;

    setLoading(true);
    try {
      const res: CreateJobResponse = await createJob(filePath);
      message.success(`Job ${res.id} created successfully!`);
    } catch (err) {
      message.error("Fail to create job");
      console.error(err);
    } finally {
      setLoading(false);
    }
  }

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
      {filePath && (
        <div>
          <Space orientation="vertical" size="middle" style={{ width: "100%" }}>
            <Typography.Title level={5}>File Path:</Typography.Title>
            <Input value={filePath} />
            <Button onClick={handleRunJob}>Run Job</Button>
          </Space>
        </div>
      )}
    </div>
  );
}

export default UploadImage;
