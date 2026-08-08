import type {
	CreateExpenseTypeDto,
	ExpenseTypeResponseDto,
	ExpenseTypeSummaryResponseDto,
	UpdateExpenseTypeDto,
} from '$lib/dto/expense-type';
import type {
	ExpenseType,
	ExpenseTypeFormValues,
	ExpenseTypeSummary,
} from '$lib/models/expense-type';

export function toExpenseTypeSummary(
	dto: ExpenseTypeSummaryResponseDto,
): ExpenseTypeSummary {
	return {
		id: dto.id,
		code: dto.code,
		name: dto.name,
		active: dto.active,
	};
}

export function toExpenseType(dto: ExpenseTypeResponseDto): ExpenseType {
	return {
		id: dto.id,
		code: dto.code,
		name: dto.name,
		active: dto.active,
		createdAt: dto.createdAt,
		updatedAt: dto.updatedAt,
	};
}

export function toCreateExpenseTypeDto(
	values: ExpenseTypeFormValues,
): CreateExpenseTypeDto {
	return {
		code: values.code.trim(),
		name: values.name.trim(),
		active: values.active,
	};
}

export function toUpdateExpenseTypeDto(
	values: ExpenseTypeFormValues,
): UpdateExpenseTypeDto {
	return {
		name: values.name.trim(),
		active: values.active,
	};
}
