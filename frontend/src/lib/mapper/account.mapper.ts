import type {
	AccountResponseDto,
	AccountSummaryResponseDto,
	CreateAccountDto,
	UpdateAccountDto,
} from '$lib/dto/account';
import type {
	Account,
	AccountFormValues,
	AccountSummary,
} from '$lib/models/account';

export function toAccountSummary(
	dto: AccountSummaryResponseDto,
): AccountSummary {
	return {
		id: dto.id,
		accountType: dto.accountType,
		bankName: dto.bankName,
		accountNumber: dto.accountNumber,
		active: dto.active,
	};
}

export function toAccount(dto: AccountResponseDto): Account {
	return {
		id: dto.id,
		userId: dto.userId,
		userFullName: dto.userFullName,
		accountType: dto.accountType,
		bankName: dto.bankName,
		accountNumber: dto.accountNumber,
		accountHolderName: dto.accountHolderName,
		active: dto.active,
		createdAt: dto.createdAt,
		updatedAt: dto.updatedAt,
	};
}

export function toCreateAccountDto(
	values: AccountFormValues,
): CreateAccountDto {
	return {
		userId: values.userId as number,
		accountType: values.accountType,
		bankName: values.bankName.trim(),
		accountNumber: values.accountNumber.trim(),
		accountHolderName: values.accountHolderName.trim(),
		active: values.active,
	};
}

export function toUpdateAccountDto(
	values: AccountFormValues,
): UpdateAccountDto {
	return {
		bankName: values.bankName.trim(),
		accountHolderName: values.accountHolderName.trim(),
		active: values.active,
	};
}
