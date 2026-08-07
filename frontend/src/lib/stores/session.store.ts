import { browser } from '$app/environment';
import { writable, type Writable } from 'svelte/store';
import { AUTH_STORAGE } from '$lib/auth/auth.constants';
import type { Session } from '$lib/auth/auth.types';

function readStoredSession(): Session | null {
	if (!browser) return null;
	try {
		const accessToken = localStorage.getItem(AUTH_STORAGE.accessToken);
		const rawUser = localStorage.getItem(AUTH_STORAGE.authenticatedUser);
		if (!accessToken || !rawUser) return null;
		return { accessToken, authenticatedUser: JSON.parse(rawUser) };
	} catch {
		return null;
	}
}

function writeStoredSession(next: Session | null): void {
	if (!browser) return;
	if (!next) {
		localStorage.removeItem(AUTH_STORAGE.accessToken);
		localStorage.removeItem(AUTH_STORAGE.authenticatedUser);
		return;
	}
	localStorage.setItem(AUTH_STORAGE.accessToken, next.accessToken);
	localStorage.setItem(
		AUTH_STORAGE.authenticatedUser,
		JSON.stringify(next.authenticatedUser),
	);
}

export const session: Writable<Session | null> = writable<Session | null>(
	readStoredSession(),
);

export function setSession(next: Session): void {
	session.set(next);
	writeStoredSession(next);
}

export function clearSession(): void {
	session.set(null);
	writeStoredSession(null);
}
