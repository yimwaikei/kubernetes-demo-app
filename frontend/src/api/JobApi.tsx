import { API_BASE } from "./config";

export async function getJobsByNameApi(name: string, pageNumber: string, pageSize: string): Promise<Response> {
  const params = new URLSearchParams({ name, pageNumber, pageSize });
  return await fetch(`${API_BASE}/api/v1/jobs?${params.toString()}`);
}

export async function createJobApi(filePath: string): Promise<Response> {
  const res = await fetch(`${API_BASE}/api/v1/jobs`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({ filePath }),
  });

  return res;
}
