import { apiClient } from '$lib/api/api-client';
import type {
	CostCenterResponseDto,
	CostCenterSummaryResponseDto,
	CreateCostCenterDto,
	UpdateCostCenterDto,
} from '$lib/dto/cost-center';
import { BaseService } from '$lib/services/base.service';
import type { PageRequest, PageResponse } from '$lib/types/pagination';

class CostCenterService extends BaseService<
	CostCenterResponseDto,
	CreateCostCenterDto,
	UpdateCostCenterDto
> {
	constructor() {
		super('/v1/cost-centers');
	}

	getCostCenters(
		pageRequest: PageRequest,
	): Promise<PageResponse<CostCenterSummaryResponseDto>> {
		return this.getPage<CostCenterSummaryResponseDto>(pageRequest);
	}

	activate(id: number): Promise<void> {
		return apiClient.patch<void>(`${this.resourcePath}/${id}/activate`);
	}

	deactivate(id: number): Promise<void> {
		return apiClient.patch<void>(`${this.resourcePath}/${id}/deactivate`);
	}
}

export const costCenterService = new CostCenterService();
