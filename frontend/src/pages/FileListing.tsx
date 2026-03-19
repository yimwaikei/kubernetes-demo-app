import { useEffect, useState } from "react";
import { Table } from "antd";
import type { FileDto, FileDtoList } from "../models/file";
import { getListOfProcessedFiles } from "../services/FileService";

function FileListing() {
  const [fileList, setFileList] = useState<FileDtoList>();
  const [loading, setLoading] = useState(true);

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
