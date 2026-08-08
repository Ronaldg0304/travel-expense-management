/** POST /api/v1/expense-types — mirrors backend `CreateExpenseTypeRequest`. */
export interface CreateExpenseTypeDto {
	code: string;
	name: string;
	active: boolean;
}

/** PUT /api/v1/expense-types/{id} — mirrors backend `UpdateExpenseTypeRequest`. */
export interface UpdateExpenseTypeDto {
	name: string;
	active: boolean;
}

/** GET /api/v1/expense-types/{id} — mirrors backend `ExpenseTypeResponse`. */
export interface ExpenseTypeResponseDto {
	id: number;
	code: string;
	name: string;
	active: boolean;
	createdAt: string;
	updatedAt: string;
}

/** GET /api/v1/expense-types — mirrors backend `ExpenseTypeSummaryResponse`. */
export interface ExpenseTypeSummaryResponseDto {
	id: number;
	code: string;
	name: string;
	active: boolean;
}
