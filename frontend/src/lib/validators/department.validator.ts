import { z } from 'zod';
import type { DepartmentFormValues } from '$lib/models/department';

const code = z
	.string()
	.trim()
	.min(1, 'El código es obligatorio.')
	.max(20, 'El código no puede superar los 20 caracteres.');

const name = z
	.string()
	.trim()
	.min(1, 'El nombre es obligatorio.')
	.max(100, 'El nombre no puede superar los 100 caracteres.');

/** Mirrors backend `CreateDepartmentRequest` validation. */
export const createDepartmentSchema = z.object({
	code,
	name,
	active: z.boolean(),
});

/** Mirrors backend `UpdateDepartmentRequest` validation. */
export const updateDepartmentSchema = z.object({
	name,
	active: z.boolean(),
});

export type DepartmentFormErrors = Partial<
	Record<keyof DepartmentFormValues, string>
>;

export type DepartmentFormValidation =
	{ success: true } | { success: false; errors: DepartmentFormErrors };

function collectZodErrors<T>(error: z.ZodError<T>): DepartmentFormErrors {
	const errors: DepartmentFormErrors = {};
	for (const issue of error.issues) {
		const key = issue.path[0] as keyof DepartmentFormValues | undefined;
		if (key && !errors[key]) errors[key] = issue.message;
	}
	return errors;
}

export function validateDepartmentForm(
	mode: 'create' | 'edit',
	values: DepartmentFormValues,
): DepartmentFormValidation {
	const schema =
		mode === 'create' ? createDepartmentSchema : updateDepartmentSchema;
	const result = schema.safeParse(values);
	if (result.success) return { success: true };
	return { success: false, errors: collectZodErrors(result.error) };
}

const BACKEND_FIELD_KEYS: ReadonlySet<keyof DepartmentFormValues> = new Set([
	'code',
	'name',
	'active',
]);

/**
 * Backend validation messages come as `field: message; field: message`
 * (see `GlobalExceptionHandler.handleValidationExceptions`). Map them back
 * to form fields; anything that is not a known field is ignored so it falls
 * through to the form-level error.
 */
export function applyDepartmentBackendFieldErrors(
	message: string | null | undefined,
): DepartmentFormErrors {
	if (!message) return {};
	const errors: DepartmentFormErrors = {};
	for (const part of message.split(';')) {
		const separator = part.indexOf(':');
		if (separator < 0) continue;
		const field = part.slice(0, separator).trim();
		const detail = part.slice(separator + 1).trim();
		if (!field || !detail) continue;
		if (!BACKEND_FIELD_KEYS.has(field as keyof DepartmentFormValues)) continue;
		errors[field as keyof DepartmentFormValues] = detail;
	}
	return errors;
}
