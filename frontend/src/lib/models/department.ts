export interface DepartmentSummary {
	id: number;
	code: string;
	name: string;
	active: boolean;
}

export interface Department {
	id: number;
	code: string;
	name: string;
	active: boolean;
	createdAt: string;
	updatedAt: string;
}

export interface DepartmentFormValues {
	code: string;
	name: string;
	active: boolean;
}

export interface DepartmentApproverSummary {
	id: number;
	departmentName: string;
	userFullName: string;
	active: boolean;
}
