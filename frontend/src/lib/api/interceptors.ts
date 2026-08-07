import type {
	AxiosError,
	AxiosInstance,
	InternalAxiosRequestConfig,
} from 'axios';
import { STORAGE_KEYS } from '$lib/constants/storage';

export interface ApiErrorBody {
	success: boolean;
	message: string | null;
	timestamp: string;
}

function onRequest(
	config: InternalAxiosRequestConfig,
): InternalAxiosRequestConfig {
	if (typeof localStorage !== 'undefined') {
		const token = localStorage.getItem(STORAGE_KEYS.ACCESS_TOKEN);
		if (token) {
			config.headers.Authorization = `Bearer ${token}`;
		}
	}
	return config;
}

function onResponseError(error: AxiosError): Promise<never> {
	const body = error.response?.data as ApiErrorBody | undefined;
	if (body?.message) {
		error.message = body.message;
	}
	return Promise.reject(error);
}

export function setupInterceptors(client: AxiosInstance): void {
	client.interceptors.request.use(onRequest);
	client.interceptors.response.use((response) => response, onResponseError);
}
