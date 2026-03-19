import { createJobApi, getJobsByNameApi, rerunJobApi } from "../api/JobApi";
import type { CreateJobResponse, JobList } from "../models/job";

export async function getJobsByName(name: string, pageNumber: number, pageSize: number): Promise<JobList> {
  const res = await getJobsByNameApi(name, pageNumber.toString(), pageSize.toString());

  if (!res.ok) throw new Error("Jobs not found");
  return res.json() as Promise<JobList>;
}

export async function createJob(filePath: string): Promise<CreateJobResponse> {
  const res = await createJobApi(filePath);

  if (!res.ok) throw new Error("Fail to create job");
  return res.json() as Promise<CreateJobResponse>;
}

export async function rerunJob(id: string): Promise<CreateJobResponse> {
  const res = await rerunJobApi(id);
  
  if (!res.ok) throw new Error("Fail to rerun job");
  return res.json() as Promise<CreateJobResponse>;
}
