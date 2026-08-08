import { z } from 'zod';
import type { TravelRequestFormValues } from '$lib/models/travel-request';

const travelPurpose = z
	.string()
	.trim()
	.min(1, 'El motivo del viaje es obligatorio.')
	.max(500, 'El motivo del viaje no puede superar los 500 caracteres.');

const destination = z
	.string()
	.trim()
	.min(1, 'El destino es obligatorio.')
	.max(200, 'El destino no puede superar los 200 caracteres.');

const dateOnly = /^\d{4}-\d{2}-\d{2}$/;

const departureDate = z
	.string()
	.trim()
	.min(1, 'La fecha de salida es obligatoria.')
	.regex(dateOnly, 'La fecha de salida no es válida.');

const returnDate = z
	.string()
	.trim()
	.min(1, 'La fecha de regreso es obligatoria.')
	.regex(dateOnly, 'La fecha de regreso no es válida.');

const requestedAmount = z
	.string()
	.trim()
	.min(1, 'El valor solicitado es obligatorio.')
	.regex(/^\d+$/, 'El valor solicitado debe ser un número entero positivo.')
	.refine((value) => Number(value) > 0, {
		message: 'El valor solicitado debe ser mayor a cero.',
	});

/** Mirrors backend `CreateTravelRequestRequest` validation. */
export const createTravelRequestSchema = z
	.object({
		travelPurpose,
		destination,
		departureDate,
		returnDate,
		requestedAmount,
	})
	.superRefine((data, ctx) => {
		if (data.returnDate && data.departureDate && data.returnDate < data.departureDate) {
			ctx.addIssue({
				code: z.ZodIssueCode.custom,
				path: ['returnDate'],
				message: 'La fecha de regreso debe ser posterior a la fecha de salida.',
			});
		}
	});

export type TravelRequestFormErrors = Partial<
	Record<keyof TravelRequestFormValues, string>
>;

export type TravelRequestFormValidation =
	| { success: true }
	| { success: false; errors: TravelRequestFormErrors };

function collectZodErrors<T>(error: z.ZodError<T>): TravelRequestFormErrors {
	const errors: TravelRequestFormErrors = {};
	for (const issue of error.issues) {
		const key = issue.path[0] as keyof TravelRequestFormValues | undefined;
		if (key && !errors[key]) errors[key] = issue.message;
	}
	return errors;
}

export function validateTravelRequestForm(
	values: TravelRequestFormValues,
): TravelRequestFormValidation {
	const result = createTravelRequestSchema.safeParse(values);
	if (result.success) return { success: true };
	return { success: false, errors: collectZodErrors(result.error) };
}

const BACKEND_FIELD_KEYS: ReadonlySet<keyof TravelRequestFormValues> = new Set([
	'travelPurpose',
	'destination',
	'departureDate',
	'returnDate',
	'requestedAmount',
]);

/**
 * Backend validation messages come as `field: message; field: message`
 * (see `GlobalExceptionHandler.handleValidationExceptions`). Map them back
 * to form fields; anything that is not a known field is ignored so it falls
 * through to the form-level error.
 */
export function applyTravelRequestBackendFieldErrors(
	message: string | null | undefined,
): TravelRequestFormErrors {
	if (!message) return {};
	const errors: TravelRequestFormErrors = {};
	for (const part of message.split(';')) {
		const separator = part.indexOf(':');
		if (separator < 0) continue;
		const field = part.slice(0, separator).trim();
		const detail = part.slice(separator + 1).trim();
		if (!field || !detail) continue;
		if (!BACKEND_FIELD_KEYS.has(field as keyof TravelRequestFormValues)) continue;
		errors[field as keyof TravelRequestFormValues] = detail;
	}
	return errors;
}
