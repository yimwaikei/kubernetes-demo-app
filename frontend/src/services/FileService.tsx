import { downloadFileApi, getListOfFilesApi, uploadFileApi } from "../api/FileApi";
import type { FileDtoList, UploadFileResponse } from "../models/file";

export async function uploadFile(file: File): Promise<UploadFileResponse> {
  if (!file) throw new Error("No file provided");

  const res = await uploadFileApi(file);

  if (!res.ok) throw new Error("Upload failed");
  return res.json() as Promise<UploadFileResponse>;
}

export async function getListOfProcessedFiles(): Promise<FileDtoList> {
  const res = await getListOfFilesApi("processed");

  if (!res.ok) throw new Error("Files not found");
  return res.json() as Promise<FileDtoList>;
}

export async function downloadFile(filePath: string): Promise<void> {
  const res = await downloadFileApi(filePath);

  if (!res.ok) throw new Error("File not found");

  const blob = await res.blob();

  const url = window.URL.createObjectURL(blob);
  const a = document.createElement("a");
  a.href = url;

  const contentDisposition = res.headers.get("Content-Disposition");
  const fileNameMatch = contentDisposition?.match(/filename="(.+)"/);
  const fileName = fileNameMatch?.[1] || filePath.split("/").pop() || "file";

  a.download = fileName;
  document.body.appendChild(a);
  a.click();
  a.remove();

  window.URL.revokeObjectURL(url);
}
