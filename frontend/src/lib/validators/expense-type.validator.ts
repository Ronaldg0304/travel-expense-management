import { z } from 'zod';
import type { ExpenseTypeFormValues } from '$lib/models/expense-type';

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

/** Mirrors backend `CreateExpenseTypeRequest` validation. */
export const createExpenseTypeSchema = z.object({
	code,
	name,
	active: z.boolean(),
});

/** Mirrors backend `UpdateExpenseTypeRequest` validation. */
export const updateExpenseTypeSchema = z.object({
	name,
	active: z.boolean(),
});

export type ExpenseTypeFormErrors = Partial<
	Record<keyof ExpenseTypeFormValues, string>
>;

export type ExpenseTypeFormValidation =
	{ success: true } | { success: false; errors: ExpenseTypeFormErrors };

function collectZodErrors<T>(error: z.ZodError<T>): ExpenseTypeFormErrors {
	const errors: ExpenseTypeFormErrors = {};
	for (const issue of error.issues) {
		const key = issue.path[0] as keyof ExpenseTypeFormValues | undefined;
		if (key && !errors[key]) errors[key] = issue.message;
	}
	return errors;
}

export function validateExpenseTypeForm(
	mode: 'create' | 'edit',
	values: ExpenseTypeFormValues,
): ExpenseTypeFormValidation {
	const schema =
		mode === 'create' ? createExpenseTypeSchema : updateExpenseTypeSchema;
	const result = schema.safeParse(values);
	if (result.success) return { success: true };
	return { success: false, errors: collectZodErrors(result.error) };
}

const BACKEND_FIELD_KEYS: ReadonlySet<keyof ExpenseTypeFormValues> = new Set([
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
export function applyExpenseTypeBackendFieldErrors(
	message: string | null | undefined,
): ExpenseTypeFormErrors {
	if (!message) return {};
	const errors: ExpenseTypeFormErrors = {};
	for (const part of message.split(';')) {
		const separator = part.indexOf(':');
		if (separator < 0) continue;
		const field = part.slice(0, separator).trim();
		const detail = part.slice(separator + 1).trim();
		if (!field || !detail) continue;
		if (!BACKEND_FIELD_KEYS.has(field as keyof ExpenseTypeFormValues)) continue;
		errors[field as keyof ExpenseTypeFormValues] = detail;
	}
	return errors;
}
