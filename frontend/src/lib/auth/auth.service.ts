import { request } from '$lib/api/axios';
import { AUTH_ENDPOINTS } from '$lib/auth/auth.constants';
import type {
	AuthenticatedUser,
	AuthLoginResponse,
	LoginCredentials,
} from '$lib/auth/auth.types';
import { clearSession, setSession } from '$lib/stores/session.store';

async function login(
	credentials: LoginCredentials,
): Promise<AuthenticatedUser> {
	const response = await request<AuthLoginResponse>({
		method: 'POST',
		url: AUTH_ENDPOINTS.login,
		data: credentials,
	});
	const authenticatedUser: AuthenticatedUser = {
		id: response.user.id,
		email: response.user.email,
		firstName: response.user.firstName,
		lastName: response.user.lastName,
		roles: [response.user.role],
	};
	setSession({
		accessToken: response.accessToken,
		authenticatedUser,
	});
	return authenticatedUser;
}

async function logout(): Promise<void> {
	clearSession();
}

export const authService = { login, logout };
