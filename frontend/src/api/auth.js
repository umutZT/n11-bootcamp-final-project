import { apiClient } from "./client";

export async function signin(username, password) {
  const { data } = await apiClient.post("/api/user/signin", { username, password });
  return data;
}

export async function signup(username, email, password) {
  const { data } = await apiClient.post("/api/user/signup", { username, email, password });
  return data;
}
