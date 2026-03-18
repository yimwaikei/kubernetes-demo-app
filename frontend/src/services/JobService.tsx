import { getJobsByName } from "../api/JobApi";
import type { JobList } from "../models/job";

export async function fetchJobsByName(name: string, pageNumber: number, pageSize: number): Promise<JobList> {
  return getJobsByName(name, pageNumber.toString(), pageSize.toString());
}
