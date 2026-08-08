import type { SettlementAnalysisResponseDto } from '$lib/dto/settlement';
import type { SettlementAnalysis } from '$lib/models/settlement';

export function toSettlementAnalysis(
	dto: SettlementAnalysisResponseDto,
): SettlementAnalysis {
	return {
		legalizationId: dto.legalizationId,
		requestNumber: dto.requestNumber,
		disbursedAmount: dto.disbursedAmount,
		totalExpenses: dto.totalExpenses,
		difference: dto.difference,
		settlementType: dto.settlementType,
	};
}
