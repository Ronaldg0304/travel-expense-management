import type { RequestStatus } from '$lib/models/travel-request';

/** POST /api/v1/legalizations — mirrors backend `CreateLegalizationRequest`. */
export interface CreateLegalizationDto {
	travelRequestId: number;
	costCenterId: number;
	expenses: CreateExpenseDto[];
}

/** POST /api/v1/legalizations — mirrors backend `CreateExpenseRequest`. */
export interface CreateExpenseDto {
	expenseTypeId: number;
	expenseDate: string;
	description: string;
	amount: number;
}

/** GET /api/v1/legalizations/{id} — mirrors backend `ExpenseResponse`. */
export interface ExpenseResponseDto {
	id: number;
	expenseTypeId: number;
	expenseTypeName: string;
	expenseDate: string;
	description: string;
	amount: number;
}

/** GET /api/v1/legalizations/{id} — mirrors backend `LegalizationResponse`. */
export interface LegalizationResponseDto {
	id: number;
	travelRequestId: number;
	requestNumber: string;
	applicantName: string;
	costCenterId: number;
	costCenterName: string;
	totalExpenses: number;
	status: RequestStatus;
	submittedAt: string;
	expenses: ExpenseResponseDto[];
}

/** GET /api/v1/legalizations — mirrors backend `LegalizationSummaryResponse`. */
export interface LegalizationSummaryResponseDto {
	id: number;
	requestNumber: string;
	applicantName: string;
	totalExpenses: number;
	status: RequestStatus;
}
