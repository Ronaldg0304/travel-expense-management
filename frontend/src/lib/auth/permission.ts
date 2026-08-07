import type { AuthenticatedUser, UserRole } from '$lib/auth/auth.types';

export function hasRole(
	user: AuthenticatedUser | null,
	role: UserRole,
): boolean {
	return user?.roles.includes(role) ?? false;
}

export function hasAnyRole(
	user: AuthenticatedUser | null,
	roles: readonly UserRole[],
): boolean {
	return roles.some((role) => hasRole(user, role));
}

export function hasAllRoles(
	user: AuthenticatedUser | null,
	roles: readonly UserRole[],
): boolean {
	return roles.every((role) => hasRole(user, role));
}

export function isAdmin(user: AuthenticatedUser | null): boolean {
	return hasRole(user, 'ADMINISTRADOR');
}

export function canAccess(
	user: AuthenticatedUser | null,
	roles: readonly UserRole[],
): boolean {
	if (isAdmin(user)) return true;
	return hasAnyRole(user, roles);
}
