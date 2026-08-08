import { apiClient } from '$lib/api/api-client';
import type {
	CreateLegalizationDto,
	LegalizationResponseDto,
} from '$lib/dto/legalization';
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
}

export const legalizationService = new LegalizationService();
