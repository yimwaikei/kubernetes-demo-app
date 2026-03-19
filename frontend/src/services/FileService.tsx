import { getListOfFilesApi, uploadFileApi } from "../api/FileApi";
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
