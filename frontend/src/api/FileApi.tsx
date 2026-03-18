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
