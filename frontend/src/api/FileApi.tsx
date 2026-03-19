import { API_BASE } from "./config";

export async function uploadFileApi(file: File): Promise<Response> {
  const formData = new FormData();
  formData.append("file", file);

  const res = await fetch(`${API_BASE}/api/v1/files`, {
    method: "POST",
    body: formData,
  });

  return res;
}

export async function getListOfFilesApi(folder: string): Promise<Response> {
  const params = new URLSearchParams({ folder });
  return await fetch(`${API_BASE}/api/v1/files?${params.toString()}`);
}

export async function downloadFileApi(filePath: string): Promise<Response> {
  const params = new URLSearchParams({ filePath });
  return await fetch(`${API_BASE}/api/v1/files/download?${params.toString()}`);
}
