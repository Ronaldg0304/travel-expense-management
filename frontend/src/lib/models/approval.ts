export const APPROVAL_DECISIONS = ['APROBADA', 'DEVUELTA', 'RECHAZADA'] as const;

export type ApprovalDecision = (typeof APPROVAL_DECISIONS)[number];

export const APPROVAL_DECISION_LABELS: Record<ApprovalDecision, string> = {
	APROBADA: 'Aprobada',
	DEVUELTA: 'Devuelta',
	RECHAZADA: 'Rechazada',
};
