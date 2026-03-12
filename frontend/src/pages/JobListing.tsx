import { useEffect, useState } from "react";
import { fetchJobsByName } from "../services/JobService";
import type { Job } from "../models/job";

function JobListing() {
  const [jobs, setJobs] = useState<Job[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchJobsByName("test")
      .then(setJobs)
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <p>Loading...</p>;

  return (
    <div>
      <h2>Jobs</h2>
      {jobs.map(job => (
        <p key={job.id}>{job.id}</p>
      ))}
    </div>
  );
}

export default JobListing
