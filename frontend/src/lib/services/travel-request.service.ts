import { apiClient } from '$lib/api/api-client';
import type {
	CreateTravelRequestDto,
	TravelRequestResponseDto,
	TravelRequestSummaryResponseDto,
} from '$lib/dto/travel-request';
import { BaseService, serializePageRequest } from '$lib/services/base.service';
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

	getMyTravelRequests(
		pageRequest: PageRequest,
	): Promise<PageResponse<TravelRequestSummaryResponseDto>> {
		return apiClient.get<PageResponse<TravelRequestSummaryResponseDto>>(
			`${this.resourcePath}/my`,
			{ params: serializePageRequest(pageRequest) },
		);
	}

	createDraft(
		payload: CreateTravelRequestDto,
	): Promise<TravelRequestResponseDto> {
		return apiClient.post<TravelRequestResponseDto>(
			`${this.resourcePath}/draft`,
			payload,
		);
	}

	submitForApproval(id: number): Promise<TravelRequestResponseDto> {
		return apiClient.post<TravelRequestResponseDto>(
			`${this.resourcePath}/${id}/submit`,
		);
	}
}

export const travelRequestService = new TravelRequestService();
