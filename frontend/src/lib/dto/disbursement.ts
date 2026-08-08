/** POST /api/v1/disbursements/travel-request/{id} — mirrors backend `RegisterDisbursementRequest`. */
export interface RegisterDisbursementDto {
	disbursedAmount: number;
	adjustmentJustification: string | null;
}

/** POST/GET /api/v1/disbursements/travel-request/{id} — mirrors backend `DisbursementResponse`. */
export interface DisbursementResponseDto {
	id: number;
	travelRequestId: number;
	requestNumber: string;
	applicantName: string;
	approvedAmount: number | null;
	disbursedAmount: number;
	adjustmentJustification: string | null;
	registeredById: number;
	registeredByName: string;
	disbursementDate: string;
	createdAt: string;
}

/** GET /api/v1/disbursements — mirrors backend `DisbursementSummaryResponse`. */
export interface DisbursementSummaryResponseDto {
	id: number;
	requestNumber: string;
	applicantName: string;
	disbursedAmount: number;
	disbursementDate: string;
}
