import { apiClient } from '$lib/api/api-client';
import type {
	CreateTravelRequestDto,
	TravelRequestResponseDto,
	TravelRequestSummaryResponseDto,
} from '$lib/dto/travel-request';
import { BaseService } from '$lib/services/base.service';
import type { PageRequest, PageResponse } from '$lib/types/pagination';

class TravelRequestService extends BaseService<TravelRequestResponseDto> {
	constructor() {
		super('/v1/travel-requests');
	}

	getTravelRequests(
		pageRequest: PageRequest,
	): Promise<PageResponse<TravelRequestSummaryResponseDto>> {
		return this.getPage<TravelRequestSummaryResponseDto>(pageRequest);
	}

	createDraft(
		payload: CreateTravelRequestDto,
	): Promise<TravelRequestResponseDto> {
		return apiClient.post<TravelRequestResponseDto>(
			`${this.resourcePath}/draft`,
			payload,
		);
	}
}

export const travelRequestService = new TravelRequestService();
