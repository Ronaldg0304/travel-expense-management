import { apiClient } from '$lib/api/api-client';
import type {
	CreateExpenseTypeDto,
	ExpenseTypeResponseDto,
	ExpenseTypeSummaryResponseDto,
	UpdateExpenseTypeDto,
} from '$lib/dto/expense-type';
import { BaseService } from '$lib/services/base.service';
import type { PageRequest, PageResponse } from '$lib/types/pagination';

class ExpenseTypeService extends BaseService<
	ExpenseTypeResponseDto,
	CreateExpenseTypeDto,
	UpdateExpenseTypeDto
> {
	constructor() {
		super('/v1/expense-types');
	}

	getExpenseTypes(
		pageRequest: PageRequest,
	): Promise<PageResponse<ExpenseTypeSummaryResponseDto>> {
		return this.getPage<ExpenseTypeSummaryResponseDto>(pageRequest);
	}

	activate(id: number): Promise<void> {
		return apiClient.patch<void>(`${this.resourcePath}/${id}/activate`);
	}

	deactivate(id: number): Promise<void> {
		return apiClient.patch<void>(`${this.resourcePath}/${id}/deactivate`);
	}
}

export const expenseTypeService = new ExpenseTypeService();
