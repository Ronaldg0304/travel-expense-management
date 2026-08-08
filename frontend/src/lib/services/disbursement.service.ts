import { apiClient } from '$lib/api/api-client';
import type {
	DisbursementResponseDto,
	RegisterDisbursementDto,
} from '$lib/dto/disbursement';

class DisbursementService {
	private readonly resourcePath = '/v1/disbursements';

	registerDisbursement(
		travelRequestId: number,
		payload: RegisterDisbursementDto,
	): Promise<DisbursementResponseDto> {
		return apiClient.post<DisbursementResponseDto>(
			`${this.resourcePath}/travel-request/${travelRequestId}`,
			payload,
		);
	}
}

export const disbursementService = new DisbursementService();
