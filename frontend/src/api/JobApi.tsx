import type { Job } from "../models/job";
import { API_BASE } from "./config";

export async function getJobsByName(name: string): Promise<Job[]> {
  const params = new URLSearchParams({ name });
  const res = await fetch(`${API_BASE}/api/v1/jobs?${params.toString()}`);
  
  if (!res.ok) throw new Error("Jobs not found");
  return res.json() as Promise<Job[]>;
}
