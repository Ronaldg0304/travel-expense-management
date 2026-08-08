export interface ExpenseTypeSummary {
	id: number;
	code: string;
	name: string;
	active: boolean;
}

export interface ExpenseType {
	id: number;
	code: string;
	name: string;
	active: boolean;
	createdAt: string;
	updatedAt: string;
}

export interface ExpenseTypeFormValues {
	code: string;
	name: string;
	active: boolean;
}
