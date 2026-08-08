import { apiClient } from '$lib/api/api-client';
import type {
	RefundResponseDto,
	RegisterRefundDto,
} from '$lib/dto/refund';

class RefundService {
	private readonly resourcePath = '/v1/refunds';

	registerRefund(
		legalizationId: number,
		payload: RegisterRefundDto,
	): Promise<RefundResponseDto> {
		return apiClient.post<RefundResponseDto>(
			`${this.resourcePath}/legalization/${legalizationId}`,
			payload,
		);
	}
}

export const refundService = new RefundService();
