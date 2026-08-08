export const COST_CENTER_TYPES = ['DEPARTMENT', 'OPERATION_CENTER'] as const;

export type CostCenterType = (typeof COST_CENTER_TYPES)[number];

export const COST_CENTER_TYPE_LABELS: Record<CostCenterType, string> = {
	DEPARTMENT: 'Departamento',
	OPERATION_CENTER: 'Centro de operación',
};

export interface CostCenterSummary {
	id: number;
	code: string;
	name: string;
	type: CostCenterType;
	active: boolean;
}

export interface CostCenter {
	id: number;
	code: string;
	name: string;
	type: CostCenterType;
	active: boolean;
	createdAt: string;
	updatedAt: string;
}

export interface CostCenterFormValues {
	code: string;
	name: string;
	type: CostCenterType;
	active: boolean;
}
