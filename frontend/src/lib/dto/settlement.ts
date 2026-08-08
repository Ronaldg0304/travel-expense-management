import type { SettlementType } from '$lib/models/settlement';

/**
 * GET/POST /api/v1/legalizations/{id}/settlement-analysis and
 * POST /api/v1/legalizations/{id}/validate — mirrors backend `SettlementAnalysisResponse`.
 */
export interface SettlementAnalysisResponseDto {
	legalizationId: number;
	requestNumber: string;
	disbursedAmount: number;
	totalExpenses: number;
	difference: number;
	settlementType: SettlementType;
}
