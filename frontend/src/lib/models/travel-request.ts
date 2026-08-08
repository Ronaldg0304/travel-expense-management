export const REQUEST_STATUSES = [
	'BORRADOR',
	'ENVIADA',
	'DEVUELTA',
	'APROBADA',
	'RECHAZADA',
	'DESEMBOLSADA',
	'LEGALIZADA',
	'LEGALIZACION_DEVUELTA',
	'VALIDADA',
	'PENDIENTE_DEVOLUCION',
	'PENDIENTE_REEMBOLSO',
	'CERRADA',
] as const;

export type RequestStatus = (typeof REQUEST_STATUSES)[number];

export const REQUEST_STATUS_LABELS: Record<RequestStatus, string> = {
	BORRADOR: 'Borrador',
	ENVIADA: 'Enviada',
	DEVUELTA: 'Devuelta',
	APROBADA: 'Aprobada',
	RECHAZADA: 'Rechazada',
	DESEMBOLSADA: 'Desembolsada',
	LEGALIZADA: 'Legalizada',
	LEGALIZACION_DEVUELTA: 'Legalización devuelta',
	VALIDADA: 'Validada',
	PENDIENTE_DEVOLUCION: 'Pendiente devolución',
	PENDIENTE_REEMBOLSO: 'Pendiente reembolso',
	CERRADA: 'Cerrada',
};

export interface TravelRequestFormValues {
	travelPurpose: string;
	destination: string;
	departureDate: string;
	returnDate: string;
	requestedAmount: string;
}

export interface TravelRequestSummary {
	id: number;
	requestNumber: string;
	applicantFullName: string;
	destination: string;
	requestedAmount: number;
	status: RequestStatus;
	departureDate: string;
	returnDate: string;
}

export interface TravelRequest {
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
