export const ACCOUNT_TYPES = ['AHORROS', 'CORRIENTE'] as const;

export type AccountType = (typeof ACCOUNT_TYPES)[number];

export const ACCOUNT_TYPE_LABELS: Record<AccountType, string> = {
	AHORROS: 'Ahorros',
	CORRIENTE: 'Corriente',
};

export interface AccountSummary {
	id: number;
	accountType: AccountType;
	bankName: string;
	accountNumber: string;
	active: boolean;
}

export interface Account {
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

export interface AccountFormValues {
	userId: number | null;
	accountType: AccountType;
	bankName: string;
	accountNumber: string;
	accountHolderName: string;
	active: boolean;
}
