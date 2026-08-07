import axios, { type AxiosInstance, type AxiosRequestConfig } from 'axios';
import { setupAuthInterceptor } from '$lib/api/auth.interceptor';
import { setupErrorInterceptor } from '$lib/api/error.interceptor';
import { env } from '$lib/config/env';
import type { ApiResponse } from '$lib/types/api';
import type { Page } from '$lib/types/pagination';

export type { ApiResponse, Page };

export const api: AxiosInstance = axios.create({
	baseURL: env.PUBLIC_API_BASE_URL,
	timeout: 15_000,
	headers: { 'Content-Type': 'application/json' },
});

setupAuthInterceptor(api);
setupErrorInterceptor(api);

export async function request<T>(config: AxiosRequestConfig): Promise<T> {
	const response = await api.request<ApiResponse<T>>(config);
	return response.data.data;
}
