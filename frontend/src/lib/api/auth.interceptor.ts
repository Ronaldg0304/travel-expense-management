import type { AxiosInstance, InternalAxiosRequestConfig } from 'axios';
import { get } from 'svelte/store';
import {
	ACCESS_TOKEN_HEADER,
	ACCESS_TOKEN_SCHEME,
} from '$lib/auth/auth.constants';
import { session } from '$lib/stores/session.store';

function onRequest(
	config: InternalAxiosRequestConfig,
): InternalAxiosRequestConfig {
	const current = get(session);
	if (current?.accessToken) {
		config.headers.set(
			ACCESS_TOKEN_HEADER,
			`${ACCESS_TOKEN_SCHEME} ${current.accessToken}`,
		);
	}
	return config;
}

export function setupAuthInterceptor(client: AxiosInstance): void {
	client.interceptors.request.use(onRequest);
}
