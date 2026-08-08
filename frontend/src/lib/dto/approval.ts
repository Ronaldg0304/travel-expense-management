import type { ApprovalDecision } from '$lib/models/approval';

/** POST /api/v1/approvals/travel-request/{id}/approve — mirrors backend `ApproveTravelRequestRequest`. */
export interface ApproveTravelRequestDto {
	approvedAmount: number;
}

/** POST /api/v1/approvals/travel-request/{id}/reject — mirrors backend `RejectTravelRequestRequest`. */
export interface RejectTravelRequestDto {
	comments: string;
}

/** POST /api/v1/approvals/travel-request/{id}/approve|reject — mirrors backend `ApprovalResponse`. */
export interface ApprovalResponseDto {
	id: number;
	travelRequestId: number;
	approverId: number;
	approverName: string;
	decision: ApprovalDecision;
	approvedAmount: number | null;
	comments: string | null;
	decisionDate: string;
	createdAt: string;
}

/** GET /api/v1/approvals/travel-request/{id}/history — mirrors backend `ApprovalSummaryResponse`. */
export interface ApprovalSummaryResponseDto {
	id: number;
	approverName: string;
	decision: ApprovalDecision;
	decisionDate: string;
}
