import { z } from 'zod';
import { USER_ROLES } from '$lib/auth/auth.types';
import { DOCUMENT_TYPES } from '$lib/models/user';
import type { UserFormValues } from '$lib/models/user';

const firstName = z
	.string()
	.trim()
	.min(1, 'El nombre es obligatorio.')
	.max(100, 'El nombre no puede superar los 100 caracteres.');

const lastName = z
	.string()
	.trim()
	.min(1, 'El apellido es obligatorio.')
	.max(100, 'El apellido no puede superar los 100 caracteres.');

const phone = z
	.string()
	.trim()
	.max(20, 'El teléfono no puede superar los 20 caracteres.');

const position = z
	.string()
	.trim()
	.max(100, 'El cargo no puede superar los 100 caracteres.');

const departmentId = z
	.number({ message: 'El departamento es obligatorio.' })
	.int()
	.positive('El departamento es obligatorio.');

const role = z.enum(USER_ROLES, { message: 'Selecciona un rol.' });

/** Mirrors backend `CreateUserRequest` validation. */
export const createUserSchema = z.object({
	firstName,
	lastName,
	email: z
		.string()
		.trim()
		.min(1, 'El correo electrónico es obligatorio.')
		.email('Introduce un correo electrónico válido.')
		.max(150, 'El correo no puede superar los 150 caracteres.'),
	documentType: z.enum(DOCUMENT_TYPES, {
		message: 'Selecciona un tipo de documento.',
	}),
	documentNumber: z
		.string()
		.trim()
		.min(1, 'El número de documento es obligatorio.')
		.max(30, 'El número de documento no puede superar los 30 caracteres.'),
	phone,
	position,
	departmentId,
	role,
	active: z.boolean(),
});

/** Mirrors backend `UpdateUserRequest` validation. */
export const updateUserSchema = z.object({
	firstName,
	lastName,
	phone,
	position,
	departmentId,
	role,
	active: z.boolean(),
});

export type UserFormErrors = Partial<Record<keyof UserFormValues, string>>;

export type UserFormValidation =
	{ success: true } | { success: false; errors: UserFormErrors };

function collectZodErrors<T>(error: z.ZodError<T>): UserFormErrors {
	const errors: UserFormErrors = {};
	for (const issue of error.issues) {
		const key = issue.path[0] as keyof UserFormValues | undefined;
		if (key && !errors[key]) errors[key] = issue.message;
	}
	return errors;
}

export function validateUserForm(
	mode: 'create' | 'edit',
	values: UserFormValues,
): UserFormValidation {
	const schema = mode === 'create' ? createUserSchema : updateUserSchema;
	const result = schema.safeParse(values);
	if (result.success) return { success: true };
	return { success: false, errors: collectZodErrors(result.error) };
}

const BACKEND_FIELD_KEYS: ReadonlySet<keyof UserFormValues> = new Set([
	'firstName',
	'lastName',
	'email',
	'documentType',
	'documentNumber',
	'phone',
	'position',
	'departmentId',
	'role',
	'active',
]);

/**
 * Backend validation messages come as `field: message; field: message`
 * (see `GlobalExceptionHandler.handleValidationExceptions`). Map them back
 * to form fields; anything that is not a known field is ignored so it falls
 * through to the form-level error.
 */
export function applyBackendFieldErrors(
	message: string | null | undefined,
): UserFormErrors {
	if (!message) return {};
	const errors: UserFormErrors = {};
	for (const part of message.split(';')) {
		const separator = part.indexOf(':');
		if (separator < 0) continue;
		const field = part.slice(0, separator).trim();
		const detail = part.slice(separator + 1).trim();
		if (!field || !detail) continue;
		if (!BACKEND_FIELD_KEYS.has(field as keyof UserFormValues)) continue;
		errors[field as keyof UserFormValues] = detail;
	}
	return errors;
}
