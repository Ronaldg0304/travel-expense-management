import type { AccountType } from '$lib/models/account';

/** POST /api/v1/accounts — mirrors backend `CreateAccountRequest`. */
export interface CreateAccountDto {
	userId: number;
	accountType: AccountType;
	bankName: string;
	accountNumber: string;
	accountHolderName: string;
	active: boolean;
}

/** PUT /api/v1/accounts/{id} — mirrors backend `UpdateAccountRequest`. */
export interface UpdateAccountDto {
	bankName: string;
	accountHolderName: string;
	active: boolean;
}

/** GET /api/v1/accounts/{id} — mirrors backend `AccountResponse`. */
export interface AccountResponseDto {
	id: number;
	userId: number;
	userFullName: string;
	accountType: AccountType;
	bankName: string;
	accountNumber: string;
	accountHolderName: string;
	active: boolean;
	createdAt: string;
	updatedAt: string;
}

/** GET /api/v1/accounts — mirrors backend `AccountSummaryResponse`. */
export interface AccountSummaryResponseDto {
	id: number;
	accountType: AccountType;
	bankName: string;
	accountNumber: string;
	active: boolean;
}
