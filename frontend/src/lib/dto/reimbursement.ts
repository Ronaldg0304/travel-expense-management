/** POST /api/v1/reimbursements/legalization/{id} — mirrors backend `RegisterReimbursementRequest`. */
export interface RegisterReimbursementDto {
	accountId: number;
	paymentReference: string;
	paymentDate: string;
	comments: string | null;
}

/** POST /api/v1/reimbursements/legalization/{id} — mirrors backend `ReimbursementResponse`. */
export interface ReimbursementResponseDto {
	id: number;
	legalizationId: number;
	requestNumber: string;
	reimbursementAmount: number;
	accountId: number;
	accountNumber: string;
	paymentReference: string;
	paymentDate: string;
	comments: string | null;
	registeredById: number;
	registeredByName: string;
	createdAt: string;
}
