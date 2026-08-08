/** POST /api/v1/refunds/legalization/{id} — mirrors backend `RegisterRefundRequest`. */
export interface RegisterRefundDto {
	companyAccount: string;
	refundReference: string;
	refundDate: string;
	comments: string | null;
}

/** POST /api/v1/refunds/legalization/{id} — mirrors backend `RefundResponse`. */
export interface RefundResponseDto {
	id: number;
	legalizationId: number;
	requestNumber: string;
	refundAmount: number;
	companyAccount: string;
	refundReference: string;
	refundDate: string;
	comments: string | null;
	registeredById: number;
	registeredByName: string;
	createdAt: string;
}
