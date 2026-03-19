export interface UploadFileResponse {
  filePath: string;
}
export interface FileDto {
  filePath: string;
  fileName?: string | null;
}

export interface FileDtoList {
  files: FileDto[]
}
