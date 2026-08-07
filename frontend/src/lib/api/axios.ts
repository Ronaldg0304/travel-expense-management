import axios, { type AxiosInstance, type AxiosRequestConfig } from 'axios';
import { setupAuthInterceptor } from '$lib/api/auth.interceptor';
import { setupErrorInterceptor } from '$lib/api/error.interceptor';
import { env } from '$lib/config/env';

export interface ApiResponse<T> {
	success: boolean;
	message: string | null;
	data: T;
	timestamp: string;
}

export interface Page<T> {
	content: T[];
	page: number;
	size: number;
	totalElements: number;
	totalPages: number;
	last: boolean;
}

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
