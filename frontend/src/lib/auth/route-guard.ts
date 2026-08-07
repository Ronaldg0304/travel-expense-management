import { goto } from '$app/navigation';
import { resolve } from '$app/paths';
import { get } from 'svelte/store';
import {
	AUTH_ROUTES,
	DASHBOARD_ROUTE,
	PUBLIC_ROUTES,
} from '$lib/auth/auth.constants';
import { authStatus } from '$lib/auth/auth.store';
import type { AuthStatus } from '$lib/auth/auth.types';

type ResolveRoute = Parameters<typeof resolve>[0];

export function resolveRouteRedirect(
	status: AuthStatus,
	pathname: string,
): string | null {
	const normalized = normalizePath(pathname);
	const isPublic = PUBLIC_ROUTES.has(normalized);
	if (status === 'authenticated' && isPublic) return DASHBOARD_ROUTE;
	if (status === 'unauthenticated' && !isPublic) return AUTH_ROUTES.login;
	return null;
}

export function applyRouteGuard(status: AuthStatus, pathname: string): void {
	const target = resolveRouteRedirect(status, pathname);
	if (target) {
		void goto(resolve(target as ResolveRoute));
	}
}

export function requireAuth(): boolean {
	const status = get(authStatus);
	if (status === 'unauthenticated') {
		void goto(resolve(AUTH_ROUTES.login as ResolveRoute));
		return false;
	}
	return status === 'authenticated';
}

function normalizePath(pathname: string): string {
	const withoutQuery = pathname.split('?')[0];
	return withoutQuery.length > 1
		? withoutQuery.replace(/\/+$/, '')
		: withoutQuery;
}
