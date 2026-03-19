import { useEffect, useState } from "react";
import { getJobsByName, rerunJob } from "../services/JobService";
import type { Job, JobList } from "../models/job";
import { Button, message, Table } from "antd";

function JobListing() {
  const [jobList, setJobList] = useState<JobList>();
  const [loading, setLoading] = useState(true);
  const [pageNumber, setPageNumber] = useState(1);
  const [pageSize, setPageSize] = useState(10);

  const handleRerun = async (record: Job) => {
    if (!record) return;

    setLoading(true);
    try {
      await rerunJob(record.id);

      // reset page to default values to refresh table to show latest job
      setPageNumber(1)
      setPageSize(10)
      message.success("Triggered job rerun");
    } catch (err) {
      message.error("Rerun failed");
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const columns = [
    {
      title: "Index",
      key: "index",
      render: (_: Job, __: Job, rowIndex: number) => rowIndex + 1,
      width: 100,
    },
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
      width: 250,
    },
    {
      title: 'Actions',
      key: 'actions',
      render: (record: Job) => (
        <span>
          {record.status == 'Failed' &&
            <Button
              type="primary"
              onClick={() => handleRerun(record)}
              style={{ marginRight: 8 }}
            >
              Rerun
            </Button>
          }
        </span>
      ),
    }
  ];

  useEffect(() => {
    const fetchJobs = async () => {
      try {
        setLoading(true);
        const jobs = await getJobsByName("TRANSFORM_IMAGE", pageNumber, pageSize);
        setJobList(jobs);
      } catch (err) {
        console.error(err);
      } finally {
        setLoading(false);
      }
    };
    fetchJobs();
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
        tableLayout="fixed"
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
