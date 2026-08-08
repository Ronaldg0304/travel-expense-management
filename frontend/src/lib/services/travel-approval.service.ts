import { apiClient } from '$lib/api/api-client';
import type {
	ApproveTravelRequestDto,
	ApprovalResponseDto,
	RejectTravelRequestDto,
} from '$lib/dto/approval';

class TravelApprovalService {
	private readonly resourcePath = '/v1/approvals';

	approve(
		travelRequestId: number,
		approvedAmount: number,
	): Promise<ApprovalResponseDto> {
		const payload = { approvedAmount } satisfies ApproveTravelRequestDto;
		return apiClient.post<ApprovalResponseDto>(
			`${this.resourcePath}/travel-request/${travelRequestId}/approve`,
			payload,
		);
	}

	reject(
		travelRequestId: number,
		comments: string,
	): Promise<ApprovalResponseDto> {
		const payload = { comments } satisfies RejectTravelRequestDto;
		return apiClient.post<ApprovalResponseDto>(
			`${this.resourcePath}/travel-request/${travelRequestId}/reject`,
			payload,
		);
	}
}

export const travelApprovalService = new TravelApprovalService();
