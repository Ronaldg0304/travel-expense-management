import axios, { type AxiosInstance, type AxiosRequestConfig } from 'axios';
import { setupInterceptors } from '$lib/api/interceptors';
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

setupInterceptors(api);

export async function request<T>(config: AxiosRequestConfig): Promise<T> {
	const response = await api.request<ApiResponse<T>>(config);
	return response.data.data;
}
