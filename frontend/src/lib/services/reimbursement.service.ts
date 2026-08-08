import { apiClient } from '$lib/api/api-client';
import type {
	ReimbursementResponseDto,
	RegisterReimbursementDto,
} from '$lib/dto/reimbursement';

class ReimbursementService {
	private readonly resourcePath = '/v1/reimbursements';

	registerReimbursement(
		legalizationId: number,
		payload: RegisterReimbursementDto,
	): Promise<ReimbursementResponseDto> {
		return apiClient.post<ReimbursementResponseDto>(
			`${this.resourcePath}/legalization/${legalizationId}`,
			payload,
		);
	}
}

export const reimbursementService = new ReimbursementService();
