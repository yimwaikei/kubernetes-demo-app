import { getJobsByName } from "../api/JobApi";
import type { Job } from "../models/job";

export async function fetchJobsByName(name: string): Promise<Job[]> {
  return getJobsByName(name);
}
