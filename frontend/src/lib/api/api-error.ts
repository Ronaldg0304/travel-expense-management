import axios from 'axios';
import type { ApiErrorResponse } from '$lib/types/api';

/**
 * Normalized frontend error for the UI layer. Never raw `AxiosError` instances.
 *
 * The message is already normalized by the error interceptor
 * (`describeError`): backend `ApiResponse.message`, timeout, network, or
 * fallback status text.
 */
export class ApiError extends Error {
	readonly status: number | null;
	readonly data: unknown;

	constructor(status: number | null, message: string, data?: unknown) {
		super(message);
		this.name = 'ApiError';
		this.status = status;
		this.data = data;
	}
}

function isApiErrorBody(value: unknown): value is ApiErrorResponse {
	if (typeof value !== 'object' || value === null) return false;
	const body = value as Record<string, unknown>;
	return body.success === false && typeof body.timestamp === 'string';
}

export function normalizeApiError(error: unknown): ApiError {
	if (error instanceof ApiError) return error;

	if (axios.isAxiosError(error)) {
		const status = error.response?.status ?? null;
		const body = error.response?.data;
		return new ApiError(status, error.message, body);
	}

	if (error instanceof Error) {
		return new ApiError(null, error.message);
	}

	return new ApiError(null, 'An unexpected error occurred.');
}

export function getApiErrorMessage(error: unknown): string {
	return normalizeApiError(error).message;
}

export function isApiErrorResponse(error: unknown): boolean {
	if (!axios.isAxiosError(error)) return false;
	return isApiErrorBody(error.response?.data);
}
