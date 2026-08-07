import type { AxiosRequestConfig } from 'axios';
import { request } from '$lib/api/axios';

export interface ApiClient {
	get<T>(url: string, config?: AxiosRequestConfig): Promise<T>;
	post<T>(url: string, data?: unknown, config?: AxiosRequestConfig): Promise<T>;
	put<T>(url: string, data?: unknown, config?: AxiosRequestConfig): Promise<T>;
	patch<T>(
		url: string,
		data?: unknown,
		config?: AxiosRequestConfig,
	): Promise<T>;
	delete<T>(url: string, config?: AxiosRequestConfig): Promise<T>;
}

/**
 * Thin, typed facade over the shared `api` Axios instance. Every method
 * unwraps the backend `ApiResponse.data` payload via `request<T>`.
 */
export const apiClient: ApiClient = {
	get: <T>(url: string, config?: AxiosRequestConfig) =>
		request<T>({ method: 'GET', url, ...config }),
	post: <T>(url: string, data?: unknown, config?: AxiosRequestConfig) =>
		request<T>({ method: 'POST', url, data, ...config }),
	put: <T>(url: string, data?: unknown, config?: AxiosRequestConfig) =>
		request<T>({ method: 'PUT', url, data, ...config }),
	patch: <T>(url: string, data?: unknown, config?: AxiosRequestConfig) =>
		request<T>({ method: 'PATCH', url, data, ...config }),
	delete: <T>(url: string, config?: AxiosRequestConfig) =>
		request<T>({ method: 'DELETE', url, ...config }),
};
