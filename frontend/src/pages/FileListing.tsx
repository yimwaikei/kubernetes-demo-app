import { useEffect, useState } from "react";
import { Button, message, Table } from "antd";
import type { FileDto, FileDtoList } from "../models/file";
import { downloadFile, getListOfProcessedFiles } from "../services/FileService";

function FileListing() {
  const [fileList, setFileList] = useState<FileDtoList>();
  const [loading, setLoading] = useState(true);

  const handleDownload = async (record: FileDto) => {
    if (!record) return;

    setLoading(true);
    try {
      const res: FileDto = await downloadFile(record.filePath);

      const link = document.createElement("a");
      link.href = res.filePath;
      link.download = res.filePath;
      link.target = "_blank";
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);

      message.success("Download successful!");
    } catch (err) {
      message.error("Download failed");
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const columns = [
    {
      title: "Index",
      key: "index",
      render: (_: any, __: FileDto, rowIndex: number) => rowIndex + 1,
      width: 150,
    },
    {
      title: 'File Name',
      dataIndex: 'fileName',
      key: 'fileName',
    },
    {
      title: 'Actions',
      key: 'actions',
      render: (record: FileDto) => (
        <span>
          <Button
            type="primary"
            onClick={() => handleDownload(record)}
            style={{ marginRight: 8 }}
          >
            Download
          </Button>
        </span>
      ),
    }
  ];

  useEffect(() => {
    const fetchFiles = async () => {
      try {
        setLoading(true);
        const files = await getListOfProcessedFiles();
        setFileList(files);
      } catch (err) {
        console.error(err);
      } finally {
        setLoading(false);
      }
    };
    fetchFiles();
  }, []);

  if (loading) return <p>Loading...</p>;

  return (
    <div>
      <h1>List of Processed Files</h1>
      <Table
        dataSource={fileList?.files}
        columns={columns}
        rowKey={(_, index) => index ?? 1}
        loading={loading}
        tableLayout="fixed"
        pagination={false}
      />
    </div>
  );
}

export default FileListing
