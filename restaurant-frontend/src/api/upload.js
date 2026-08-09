import request from "@/utils/request";

export function uploadImage(file) {

  const formData = new FormData();

  formData.append("file", file);

  return request.post("/upload", formData);

}
