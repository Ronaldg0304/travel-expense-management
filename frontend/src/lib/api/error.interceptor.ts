import { goto } from '$app/navigation';
import { resolve } from '$app/paths';
import type { PathnameWithSearchOrHash } from '$app/types';
import type { AxiosError, AxiosInstance } from 'axios';
import { AUTH_ROUTES } from '$lib/auth/auth.constants';
import { clearSession } from '$lib/stores/session.store';

interface ApiErrorBody {
	message?: string | null;
}

let handlingUnauthorized = false;

function isLoginPath(): boolean {
	return window.location.pathname === AUTH_ROUTES.login;
}

function describeError(error: AxiosError): string {
	const body = error.response?.data as ApiErrorBody | undefined;
	if (typeof body?.message === 'string' && body.message.length > 0) {
		return body.message;
	}
	if (error.code === 'ECONNABORTED') {
		return 'The request timed out. Please try again.';
	}
	if (!error.response) {
		return 'Network error. Please check your connection.';
	}
	return `Request failed with status ${error.response.status}.`;
}

async function onResponseError(error: AxiosError): Promise<never> {
	const status = error.response?.status;

	if (status === 401) {
		clearSession();
		if (!handlingUnauthorized && !isLoginPath()) {
			handlingUnauthorized = true;
			try {
				await goto(resolve(AUTH_ROUTES.login as PathnameWithSearchOrHash));
			} finally {
				handlingUnauthorized = false;
			}
		}
	}

	error.message = describeError(error);
	return Promise.reject(error);
}

export function setupErrorInterceptor(client: AxiosInstance): void {
	client.interceptors.response.use((response) => response, onResponseError);
}
