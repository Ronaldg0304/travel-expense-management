import type { UserRole } from '$lib/auth/auth.types';
import type { DocumentType } from '$lib/models/user';

/** POST /api/v1/users — mirrors backend `CreateUserRequest`. */
export interface CreateUserDto {
	firstName: string;
	lastName: string;
	email: string;
	documentType: DocumentType;
	documentNumber: string;
	phone: string;
	position: string;
	role: UserRole;
	departmentId: number;
}

/** PUT /api/v1/users/{id} — mirrors backend `UpdateUserRequest`. */
export interface UpdateUserDto {
	firstName: string;
	lastName: string;
	phone: string;
	position: string;
	role: UserRole;
	departmentId: number;
	active: boolean;
}

/** GET /api/v1/users/{id} — mirrors backend `UserResponse`. */
export interface UserResponseDto {
	id: number;
	firstName: string;
	lastName: string;
	email: string;
	documentType: DocumentType;
	documentNumber: string;
	phone: string | null;
	position: string | null;
	role: UserRole;
	active: boolean;
	departmentId: number;
	departmentName: string;
	createdAt: string;
	updatedAt: string;
}

/** GET /api/v1/users — mirrors backend `UserSummaryResponse`. */
export interface UserSummaryResponseDto {
	id: number;
	firstName: string;
	lastName: string;
	email: string;
	role: UserRole;
	active: boolean;
	departmentName: string;
}

/** GET /api/v1/departments — mirrors backend `DepartmentSummaryResponse`. */
export interface DepartmentOptionDto {
	id: number;
	code: string;
	name: string;
	active: boolean;
}
