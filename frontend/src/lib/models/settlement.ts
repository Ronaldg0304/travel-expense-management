export const SETTLEMENT_TYPES = ['REFUND', 'REIMBURSEMENT', 'BALANCED'] as const;

export type SettlementType = (typeof SETTLEMENT_TYPES)[number];

export const SETTLEMENT_TYPE_LABELS: Record<SettlementType, string> = {
	REFUND: 'Devolución',
	REIMBURSEMENT: 'Reembolso',
	BALANCED: 'Equilibrado',
};

export interface SettlementAnalysis {
	legalizationId: number;
	requestNumber: string;
	disbursedAmount: number;
	totalExpenses: number;
	difference: number;
	settlementType: SettlementType;
}
