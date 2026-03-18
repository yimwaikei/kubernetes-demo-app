import { useEffect, useState } from "react";
import { fetchJobsByName } from "../services/JobService";
import type { JobList } from "../models/job";
import { Table } from "antd";

function JobListing() {
  const [jobList, setJobList] = useState<JobList>();
  const [loading, setLoading] = useState(true);
  const [pageNumber, setPageNumber] = useState(1);
  const [pageSize, setPageSize] = useState(10);

  const columns = [
    {
      title: 'ID',
      dataIndex: 'id',
      key: 'id',
    },
    {
      title: 'File Path',
      dataIndex: 'filePath',
      key: 'filePath',
    },
    {
      title: 'Status',
      dataIndex: 'status',
      key: 'status',
    },
    {
      title: 'Created At',
      dataIndex: 'createdAt',
      key: 'createdAt',
    },
    {
      title: 'Start At',
      dataIndex: 'startAt',
      key: 'startAt',
    },
    {
      title: 'End At',
      dataIndex: 'endAt',
      key: 'endAt',
    },
    {
      title: 'Error',
      dataIndex: 'error',
      key: 'error',
    },
  ];

  useEffect(() => {
    setLoading(true);
    fetchJobsByName("TRANSFORM_IMAGE", pageNumber, pageSize)
      .then(setJobList)
      .finally(() => setLoading(false));
  }, [pageNumber, pageSize]);

  if (loading) return <p>Loading...</p>;

  return (
    <div>
      <h1>Job History</h1>
      <Table
        dataSource={jobList?.content}
        columns={columns}
        rowKey="id"
        loading={loading}
        pagination={{
          current: pageNumber,
          pageSize: pageSize,
          total: jobList?.page.totalElements,
          onChange: (page, size) => {
            setPageNumber(page);
            setPageSize(size);
          },
        }}
      />
    </div>
  );
}

export default JobListing
