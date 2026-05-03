import { apiClient } from "./client";

export async function createOrder(payload) {
  const { data } = await apiClient.post("/api/order", payload);
  return data;
}

export async function getOrder(id) {
  const { data } = await apiClient.get(`/api/order/${id}`);
  return data;
}

export async function getMyOrders() {
  const { data } = await apiClient.get("/api/order");
  return data;
}
