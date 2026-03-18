import { getJobsByNameApi } from "../api/JobApi";
import type { JobList } from "../models/job";

export async function getJobsByName(name: string, pageNumber: number, pageSize: number): Promise<JobList> {
  const res = await getJobsByNameApi(name, pageNumber.toString(), pageSize.toString());
  
  if (!res.ok) throw new Error("Jobs not found");
  return res.json() as Promise<JobList>;
}
