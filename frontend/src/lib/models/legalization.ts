import type { RequestStatus } from './travel-request';

export interface Expense {
	id: number;
	expenseTypeId: number;
	expenseTypeName: string;
	expenseDate: string;
	description: string;
	amount: number;
}

export interface Legalization {
	id: number;
	travelRequestId: number;
	requestNumber: string;
	applicantName: string;
	costCenterId: number;
	costCenterName: string;
	totalExpenses: number;
	status: RequestStatus;
	submittedAt: string;
	expenses: Expense[];
}
