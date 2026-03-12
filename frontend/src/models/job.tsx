export interface Job {
  id: string;
  name: string;
  status: string;
  metadata?: string | null;
  startAt?: string | null;
  endAt?: string | null;
  createdAt: string;
}
