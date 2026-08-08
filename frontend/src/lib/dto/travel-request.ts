import type { RequestStatus } from '$lib/models/travel-request';

/** POST /api/v1/travel-requests/draft — mirrors backend `CreateTravelRequestRequest`. */
export interface CreateTravelRequestDto {
	travelPurpose: string;
	destination: string;
	departureDate: string;
	returnDate: string;
	requestedAmount: number;
}

/** GET /api/v1/travel-requests — mirrors backend `TravelRequestSummaryResponse`. */
export interface TravelRequestSummaryResponseDto {
	id: number;
	requestNumber: string;
	applicantFullName: string;
	destination: string;
	requestedAmount: number;
	status: RequestStatus;
	departureDate: string;
	returnDate: string;
}

/** GET /api/v1/travel-requests/{id} — mirrors backend `TravelRequestResponse`. */
export interface TravelRequestResponseDto {
	id: number;
	requestNumber: string;
	applicantId: number;
	applicantFullName: string;
	departmentId: number;
	departmentName: string;
	travelPurpose: string;
	destination: string;
	departureDate: string;
	returnDate: string;
	requestedAmount: number;
	approvedAmount: number | null;
	status: RequestStatus;
	createdAt: string;
	updatedAt: string;
}
