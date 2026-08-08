import { z } from 'zod';
import { ACCOUNT_TYPES } from '$lib/models/account';
import type { AccountFormValues } from '$lib/models/account';

const userId = z
	.number({ message: 'Selecciona el titular de la cuenta.' })
	.int()
	.positive('Selecciona el titular de la cuenta.');

const accountType = z.enum(ACCOUNT_TYPES, {
	message: 'Selecciona el tipo de cuenta.',
});

const bankName = z
	.string()
	.trim()
	.min(1, 'El banco es obligatorio.')
	.max(100, 'El banco no puede superar los 100 caracteres.');

const accountNumber = z
	.string()
	.trim()
	.min(1, 'El número de cuenta es obligatorio.')
	.max(50, 'El número de cuenta no puede superar los 50 caracteres.');

const accountHolderName = z
	.string()
	.trim()
	.min(1, 'El titular es obligatorio.')
	.max(150, 'El titular no puede superar los 150 caracteres.');

/** Mirrors backend `CreateAccountRequest` validation. */
export const createAccountSchema = z.object({
	userId,
	accountType,
	bankName,
	accountNumber,
	accountHolderName,
	active: z.boolean(),
});

/** Mirrors backend `UpdateAccountRequest` validation. */
export const updateAccountSchema = z.object({
	bankName,
	accountHolderName,
	active: z.boolean(),
});

export type AccountFormErrors = Partial<
	Record<keyof AccountFormValues, string>
>;

export type AccountFormValidation =
	{ success: true } | { success: false; errors: AccountFormErrors };

function collectZodErrors<T>(error: z.ZodError<T>): AccountFormErrors {
	const errors: AccountFormErrors = {};
	for (const issue of error.issues) {
		const key = issue.path[0] as keyof AccountFormValues | undefined;
		if (key && !errors[key]) errors[key] = issue.message;
	}
	return errors;
}

export function validateAccountForm(
	mode: 'create' | 'edit',
	values: AccountFormValues,
): AccountFormValidation {
	const schema = mode === 'create' ? createAccountSchema : updateAccountSchema;
	const result = schema.safeParse(values);
	if (result.success) return { success: true };
	return { success: false, errors: collectZodErrors(result.error) };
}

const BACKEND_FIELD_KEYS: ReadonlySet<keyof AccountFormValues> = new Set([
	'userId',
	'accountType',
	'bankName',
	'accountNumber',
	'accountHolderName',
	'active',
]);

/**
 * Backend validation messages come as `field: message; field: message`
 * (see `GlobalExceptionHandler.handleValidationExceptions`). Map them back
 * to form fields; anything that is not a known field is ignored so it falls
 * through to the form-level error.
 */
export function applyAccountBackendFieldErrors(
	message: string | null | undefined,
): AccountFormErrors {
	if (!message) return {};
	const errors: AccountFormErrors = {};
	for (const part of message.split(';')) {
		const separator = part.indexOf(':');
		if (separator < 0) continue;
		const field = part.slice(0, separator).trim();
		const detail = part.slice(separator + 1).trim();
		if (!field || !detail) continue;
		if (!BACKEND_FIELD_KEYS.has(field as keyof AccountFormValues)) continue;
		errors[field as keyof AccountFormValues] = detail;
	}
	return errors;
}
