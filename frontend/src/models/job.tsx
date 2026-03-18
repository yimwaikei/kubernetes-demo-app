import type { Pagination } from "./pagination";

export interface Job {
  id: string;
  name: string;
  status: string;
  metadata?: string | null;
  startAt?: string | null;
  endAt?: string | null;
  createdAt: string;
  error?: string | null;
  filePath?: string | null;
}

export interface JobList {
  content: Job[];
  page: Pagination;
}
