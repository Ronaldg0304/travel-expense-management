import type { UserRole } from '$lib/auth/auth.types';

export const DOCUMENT_TYPES = ['CC', 'CE', 'PASAPORTE'] as const;

export type DocumentType = (typeof DOCUMENT_TYPES)[number];

export const DOCUMENT_TYPE_LABELS: Record<DocumentType, string> = {
	CC: 'Cédula de ciudadanía',
	CE: 'Cédula de extranjería',
	PASAPORTE: 'Pasaporte',
};

export const USER_STATUS_FILTERS = ['all', 'active', 'inactive'] as const;

export type UserStatusFilter = (typeof USER_STATUS_FILTERS)[number];

export interface UserSummary {
	id: number;
	firstName: string;
	lastName: string;
	email: string;
	role: UserRole;
	active: boolean;
	departmentName: string;
}

export interface User {
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

export interface DepartmentOption {
	id: number;
	code: string;
	name: string;
	active: boolean;
}

export interface UserFormValues {
	firstName: string;
	lastName: string;
	email: string;
	documentType: DocumentType;
	documentNumber: string;
	phone: string;
	position: string;
	departmentId: number | null;
	role: UserRole;
	active: boolean;
}
