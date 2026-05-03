import { apiClient } from "./client";

export async function listProducts(page = 0, size = 20) {
  const { data } = await apiClient.get(`/api/product?page=${page}&size=${size}&sort=name,asc`);
  return data;
}

export async function getProduct(id) {
  const { data } = await apiClient.get(`/api/product/${id}`);
  return data;
}
