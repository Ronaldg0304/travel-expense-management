export function formatCurrency(value: number): string {
	return new Intl.NumberFormat('es-CO', {
		style: 'currency',
		currency: 'COP',
		maximumFractionDigits: 0,
	}).format(value);
}

/**
 * Formats an ISO date string for display. Backend `LocalDate` values are
 * date-only (`YYYY-MM-DD`); parsing them with `new Date(...)` would treat
 * them as UTC midnight and could shift a day in negative-offset timezones,
 * so date-only strings are parsed as local calendar dates.
 */
export function formatDate(value: string): string {
	const dateOnly = /^(\d{4})-(\d{2})-(\d{2})$/.exec(value);
	if (dateOnly) {
		const [, year, month, day] = dateOnly;
		return new Date(
			Number(year),
			Number(month) - 1,
			Number(day),
		).toLocaleDateString('es-CO');
	}
	const date = new Date(value);
	if (Number.isNaN(date.getTime())) return value;
	return date.toLocaleDateString('es-CO');
}
