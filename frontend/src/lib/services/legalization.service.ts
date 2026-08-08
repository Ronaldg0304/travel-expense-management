import { apiClient } from '$lib/api/api-client';
import type {
	CreateLegalizationDto,
	LegalizationResponseDto,
} from '$lib/dto/legalization';
import type { SettlementAnalysisResponseDto } from '$lib/dto/settlement';
import { BaseService } from '$lib/services/base.service';

class LegalizationService extends BaseService<
	LegalizationResponseDto,
	CreateLegalizationDto
> {
	constructor() {
		super('/v1/legalizations');
	}

	findByTravelRequest(
		travelRequestId: number,
	): Promise<LegalizationResponseDto> {
		return apiClient.get<LegalizationResponseDto>(
			`${this.resourcePath}/travel-request/${travelRequestId}`,
		);
	}

	getSettlementAnalysis(
		legalizationId: number,
	): Promise<SettlementAnalysisResponseDto> {
		return apiClient.get<SettlementAnalysisResponseDto>(
			`${this.resourcePath}/${legalizationId}/settlement-analysis`,
		);
	}

	validate(
		legalizationId: number,
	): Promise<SettlementAnalysisResponseDto> {
		return apiClient.post<SettlementAnalysisResponseDto>(
			`${this.resourcePath}/${legalizationId}/validate`,
		);
	}
}

export const legalizationService = new LegalizationService();
