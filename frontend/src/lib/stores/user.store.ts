import { derived, type Readable } from 'svelte/store';
import type { AuthenticatedUser } from '$lib/auth/auth.types';
import { session } from '$lib/stores/session.store';

export const user: Readable<AuthenticatedUser | null> = derived(
	session,
	($session) => $session?.authenticatedUser ?? null,
);
