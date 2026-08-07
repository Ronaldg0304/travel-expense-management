import { ROUTES } from '$lib/constants/routes';
import { STORAGE_KEYS } from '$lib/constants/storage';

export const AUTH_ROUTES = ROUTES.auth;
export const DASHBOARD_ROUTE = ROUTES.dashboard.home;

export const AUTH_STORAGE = {
	accessToken: STORAGE_KEYS.ACCESS_TOKEN,
	authenticatedUser: STORAGE_KEYS.SESSION_USER,
} as const;

/** Paths are relative to `PUBLIC_API_BASE_URL` (default `/api`). */
export const AUTH_ENDPOINTS = {
	login: '/v1/auth/login',
} as const;

export const PUBLIC_ROUTES: ReadonlySet<string> = new Set<string>([
	AUTH_ROUTES.login,
]);

export const ACCESS_TOKEN_HEADER = 'Authorization';
export const ACCESS_TOKEN_SCHEME = 'Bearer';
