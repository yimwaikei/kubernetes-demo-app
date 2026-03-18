import { uploadFileApi } from "../api/FileApi";
import type { UploadFileResponse } from "../models/file";

export async function uploadFile(file: File): Promise<UploadFileResponse> {
  if (!file) throw new Error("No file provided");

  const res = await uploadFileApi(file);

  if (!res.ok) throw new Error("Upload failed");
  return res.json() as Promise<UploadFileResponse>;
}
