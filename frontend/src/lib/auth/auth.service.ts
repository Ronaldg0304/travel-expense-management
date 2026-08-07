import { request } from '$lib/api/axios';
import { AUTH_ENDPOINTS } from '$lib/auth/auth.constants';
import type {
	AuthenticatedUser,
	LoginCredentials,
	LoginPayload,
} from '$lib/auth/auth.types';
import { clearSession, setSession } from '$lib/stores/session.store';

async function login(
	credentials: LoginCredentials,
): Promise<AuthenticatedUser> {
	const payload = await request<LoginPayload>({
		method: 'POST',
		url: AUTH_ENDPOINTS.login,
		data: credentials,
	});
	setSession({
		accessToken: payload.accessToken,
		authenticatedUser: payload.authenticatedUser,
	});
	return payload.authenticatedUser;
}

async function logout(): Promise<void> {
	try {
		await request<null>({ method: 'POST', url: AUTH_ENDPOINTS.logout });
	} catch {
		// session is cleared regardless of the backend result
	} finally {
		clearSession();
	}
}

async function fetchCurrentUser(): Promise<AuthenticatedUser> {
	return request<AuthenticatedUser>({ method: 'GET', url: AUTH_ENDPOINTS.me });
}

export const authService = { login, logout, fetchCurrentUser };
