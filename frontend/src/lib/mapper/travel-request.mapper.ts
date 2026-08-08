import type {
	CreateTravelRequestDto,
	TravelRequestResponseDto,
	TravelRequestSummaryResponseDto,
} from '$lib/dto/travel-request';
import type {
	TravelRequest,
	TravelRequestFormValues,
	TravelRequestSummary,
} from '$lib/models/travel-request';

export function toCreateTravelRequestDto(
	values: TravelRequestFormValues,
): CreateTravelRequestDto {
	return {
		travelPurpose: values.travelPurpose.trim(),
		destination: values.destination.trim(),
		departureDate: values.departureDate.trim(),
		returnDate: values.returnDate.trim(),
		requestedAmount: Number(values.requestedAmount),
	};
}

export function toTravelRequestSummary(
	dto: TravelRequestSummaryResponseDto,
): TravelRequestSummary {
	return {
		id: dto.id,
		requestNumber: dto.requestNumber,
		applicantFullName: dto.applicantFullName,
		destination: dto.destination,
		requestedAmount: dto.requestedAmount,
		status: dto.status,
		departureDate: dto.departureDate,
		returnDate: dto.returnDate,
	};
}

export function toTravelRequest(dto: TravelRequestResponseDto): TravelRequest {
	return {
		id: dto.id,
		requestNumber: dto.requestNumber,
		applicantId: dto.applicantId,
		applicantFullName: dto.applicantFullName,
		departmentId: dto.departmentId,
		departmentName: dto.departmentName,
		travelPurpose: dto.travelPurpose,
		destination: dto.destination,
		departureDate: dto.departureDate,
		returnDate: dto.returnDate,
		requestedAmount: dto.requestedAmount,
		approvedAmount: dto.approvedAmount,
		status: dto.status,
		createdAt: dto.createdAt,
		updatedAt: dto.updatedAt,
	};
}
