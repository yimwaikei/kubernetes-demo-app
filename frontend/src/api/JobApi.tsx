import type { JobList } from "../models/job";
import { API_BASE } from "./config";

export async function getJobsByName(name: string, pageNumber: string, pageSize: string): Promise<JobList> {
  const params = new URLSearchParams({ name, pageNumber, pageSize });
  const res = await fetch(`${API_BASE}/api/v1/jobs?${params.toString()}`);
  
  if (!res.ok) throw new Error("Jobs not found");
  return res.json() as Promise<JobList>;
}
