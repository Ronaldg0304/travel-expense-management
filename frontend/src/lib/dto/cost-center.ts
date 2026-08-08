import type { CostCenterType } from '$lib/models/cost-center';

/** POST /api/v1/cost-centers — mirrors backend `CreateCostCenterRequest`. */
export interface CreateCostCenterDto {
	code: string;
	name: string;
	type: CostCenterType;
	active: boolean;
}

/** PUT /api/v1/cost-centers/{id} — mirrors backend `UpdateCostCenterRequest`. */
export interface UpdateCostCenterDto {
	name: string;
	type: CostCenterType;
	active: boolean;
}

/** GET /api/v1/cost-centers/{id} — mirrors backend `CostCenterResponse`. */
export interface CostCenterResponseDto {
	id: number;
	code: string;
	name: string;
	type: CostCenterType;
	active: boolean;
	createdAt: string;
	updatedAt: string;
}

/** GET /api/v1/cost-centers — mirrors backend `CostCenterSummaryResponse`. */
export interface CostCenterSummaryResponseDto {
	id: number;
	code: string;
	name: string;
	type: CostCenterType;
	active: boolean;
}
