import { API_BASE } from "./config";

export async function getJobsByNameApi(name: string, pageNumber: string, pageSize: string): Promise<Response> {
  const params = new URLSearchParams({ name, pageNumber, pageSize });
  return await fetch(`${API_BASE}/api/v1/jobs?${params.toString()}`);
}
