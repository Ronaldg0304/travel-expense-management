export const USER_ROLES = [
	'ADMINISTRADOR',
	'EMPLEADO',
	'APROBADOR',
	'FINANCIERA',
] as const;

export type UserRole = (typeof USER_ROLES)[number];

export interface AuthenticatedUser {
	id: number;
	email: string;
	firstName: string;
	lastName: string;
	roles: UserRole[];
}

export interface Session {
	accessToken: string;
	authenticatedUser: AuthenticatedUser;
}

export interface LoginPayload {
	accessToken: string;
	authenticatedUser: AuthenticatedUser;
}

export interface LoginCredentials {
	email: string;
	password: string;
}

export type AuthStatus = 'idle' | 'authenticated' | 'unauthenticated';
