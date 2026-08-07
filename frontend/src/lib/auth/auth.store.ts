import { browser } from '$app/environment';
import {
	derived,
	get,
	writable,
	type Readable,
	type Writable,
} from 'svelte/store';
import type { AuthStatus } from '$lib/auth/auth.types';
import { session } from '$lib/stores/session.store';

const authInitialized: Writable<boolean> = writable(false);

export const authStatus: Readable<AuthStatus> = derived(
	[authInitialized, session],
	([$initialized, $session]) => {
		if (!$initialized) return 'idle';
		return $session ? 'authenticated' : 'unauthenticated';
	},
);

export const isAuthenticated: Readable<boolean> = derived(
	authStatus,
	($status) => $status === 'authenticated',
);

export function initializeAuth(): void {
	if (!browser) return;
	if (get(authInitialized)) return;
	authInitialized.set(true);
}
