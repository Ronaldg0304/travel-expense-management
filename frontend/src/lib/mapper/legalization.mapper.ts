import type {
	ExpenseResponseDto,
	LegalizationResponseDto,
} from '$lib/dto/legalization';
import type { Expense, Legalization } from '$lib/models/legalization';

export function toExpense(dto: ExpenseResponseDto): Expense {
	return {
		id: dto.id,
		expenseTypeId: dto.expenseTypeId,
		expenseTypeName: dto.expenseTypeName,
		expenseDate: dto.expenseDate,
		description: dto.description,
		amount: dto.amount,
	};
}

export function toLegalization(dto: LegalizationResponseDto): Legalization {
	return {
		id: dto.id,
		travelRequestId: dto.travelRequestId,
		requestNumber: dto.requestNumber,
		applicantName: dto.applicantName,
		costCenterId: dto.costCenterId,
		costCenterName: dto.costCenterName,
		totalExpenses: dto.totalExpenses,
		status: dto.status,
		submittedAt: dto.submittedAt,
		expenses: dto.expenses.map(toExpense),
	};
}
