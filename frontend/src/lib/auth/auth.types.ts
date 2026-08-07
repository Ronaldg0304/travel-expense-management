export const USER_ROLES = [
	'ADMINISTRADOR',
	'EMPLEADO',
	'AVALADOR',
	'FINANCIERA',
] as const;

export type UserRole = (typeof USER_ROLES)[number];

export const ROLE_LABELS: Record<UserRole, string> = {
	ADMINISTRADOR: 'Administrador',
	EMPLEADO: 'Empleado',
	AVALADOR: 'Avalador',
	FINANCIERA: 'Financiera',
};

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

export interface LoginCredentials {
	email: string;
	password: string;
}

/** Backend `LoginResponse` payload returned by `POST /api/v1/auth/login`. */
export interface AuthLoginResponse {
	accessToken: string;
	tokenType: string;
	expiresIn: number;
	user: {
		id: number;
		firstName: string;
		lastName: string;
		email: string;
		role: UserRole;
	};
}

export type AuthStatus = 'idle' | 'authenticated' | 'unauthenticated';
