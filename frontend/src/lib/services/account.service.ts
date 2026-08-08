import { apiClient } from '$lib/api/api-client';
import type {
	AccountResponseDto,
	AccountSummaryResponseDto,
	CreateAccountDto,
	UpdateAccountDto,
} from '$lib/dto/account';
import { BaseService } from '$lib/services/base.service';
import type { PageRequest, PageResponse } from '$lib/types/pagination';

class AccountService extends BaseService<
	AccountResponseDto,
	CreateAccountDto,
	UpdateAccountDto
> {
	constructor() {
		super('/v1/accounts');
	}

	getAccounts(
		pageRequest: PageRequest,
	): Promise<PageResponse<AccountSummaryResponseDto>> {
		return this.getPage<AccountSummaryResponseDto>(pageRequest);
	}

	activate(id: number): Promise<void> {
		return apiClient.patch<void>(`${this.resourcePath}/${id}/activate`);
	}

	deactivate(id: number): Promise<void> {
		return apiClient.patch<void>(`${this.resourcePath}/${id}/deactivate`);
	}
}

export const accountService = new AccountService();
