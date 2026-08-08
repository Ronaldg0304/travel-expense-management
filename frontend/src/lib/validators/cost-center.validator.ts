import { z } from 'zod';
import { COST_CENTER_TYPES } from '$lib/models/cost-center';
import type { CostCenterFormValues } from '$lib/models/cost-center';

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

const type = z.enum(COST_CENTER_TYPES, { message: 'Selecciona un tipo.' });

/** Mirrors backend `CreateCostCenterRequest` validation. */
export const createCostCenterSchema = z.object({
	code,
	name,
	type,
	active: z.boolean(),
});

/** Mirrors backend `UpdateCostCenterRequest` validation. */
export const updateCostCenterSchema = z.object({
	name,
	type,
	active: z.boolean(),
});

export type CostCenterFormErrors = Partial<
	Record<keyof CostCenterFormValues, string>
>;

export type CostCenterFormValidation =
	{ success: true } | { success: false; errors: CostCenterFormErrors };

function collectZodErrors<T>(error: z.ZodError<T>): CostCenterFormErrors {
	const errors: CostCenterFormErrors = {};
	for (const issue of error.issues) {
		const key = issue.path[0] as keyof CostCenterFormValues | undefined;
		if (key && !errors[key]) errors[key] = issue.message;
	}
	return errors;
}

export function validateCostCenterForm(
	mode: 'create' | 'edit',
	values: CostCenterFormValues,
): CostCenterFormValidation {
	const schema =
		mode === 'create' ? createCostCenterSchema : updateCostCenterSchema;
	const result = schema.safeParse(values);
	if (result.success) return { success: true };
	return { success: false, errors: collectZodErrors(result.error) };
}

const BACKEND_FIELD_KEYS: ReadonlySet<keyof CostCenterFormValues> = new Set([
	'code',
	'name',
	'type',
	'active',
]);

/**
 * Backend validation messages come as `field: message; field: message`
 * (see `GlobalExceptionHandler.handleValidationExceptions`). Map them back
 * to form fields; anything that is not a known field is ignored so it falls
 * through to the form-level error.
 */
export function applyCostCenterBackendFieldErrors(
	message: string | null | undefined,
): CostCenterFormErrors {
	if (!message) return {};
	const errors: CostCenterFormErrors = {};
	for (const part of message.split(';')) {
		const separator = part.indexOf(':');
		if (separator < 0) continue;
		const field = part.slice(0, separator).trim();
		const detail = part.slice(separator + 1).trim();
		if (!field || !detail) continue;
		if (!BACKEND_FIELD_KEYS.has(field as keyof CostCenterFormValues)) continue;
		errors[field as keyof CostCenterFormValues] = detail;
	}
	return errors;
}
