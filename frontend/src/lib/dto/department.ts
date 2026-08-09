/** POST /api/v1/departments — mirrors backend `CreateDepartmentRequest`. */
export interface CreateDepartmentDto {
	code: string;
	name: string;
	active: boolean;
}

/** PUT /api/v1/departments/{id} — mirrors backend `UpdateDepartmentRequest`. */
export interface UpdateDepartmentDto {
	name: string;
	active: boolean;
}

/** GET /api/v1/departments/{id} — mirrors backend `DepartmentResponse`. */
export interface DepartmentResponseDto {
	id: number;
	code: string;
	name: string;
	active: boolean;
	createdAt: string;
	updatedAt: string;
}

/** GET /api/v1/departments — mirrors backend `DepartmentSummaryResponse`. */
export interface DepartmentSummaryResponseDto {
	id: number;
	code: string;
	name: string;
	active: boolean;
}

/** POST /api/v1/department-approvers — mirrors backend `CreateDepartmentApproverRequest`. */
export interface CreateDepartmentApproverDto {
	departmentId: number;
	userId: number;
	active: boolean;
}

/** PUT /api/v1/department-approvers/{id} — mirrors backend `UpdateDepartmentApproverRequest`. */
export interface UpdateDepartmentApproverDto {
	active: boolean;
}

/** GET /api/v1/department-approvers/{id} — mirrors backend `DepartmentApproverResponse`. */
export interface DepartmentApproverResponseDto {
	id: number;
	departmentId: number;
	departmentCode: string;
	departmentName: string;
	userId: number;
	userFullName: string;
	userEmail: string;
	active: boolean;
	createdAt: string;
	updatedAt: string;
}

/** GET /api/v1/department-approvers — mirrors backend `DepartmentApproverSummaryResponse`. */
export interface DepartmentApproverSummaryResponseDto {
	id: number;
	departmentName: string;
	userFullName: string;
	active: boolean;
}
