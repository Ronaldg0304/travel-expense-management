import { apiClient } from '$lib/api/api-client';
import type {
	CreateDepartmentApproverDto,
	DepartmentApproverResponseDto,
	DepartmentApproverSummaryResponseDto,
	UpdateDepartmentApproverDto,
} from '$lib/dto/department';
import { BaseService } from '$lib/services/base.service';

class DepartmentApproverService extends BaseService<
	DepartmentApproverResponseDto,
	CreateDepartmentApproverDto,
	UpdateDepartmentApproverDto
> {
	constructor() {
		super('/v1/department-approvers');
	}

	findByDepartment(
		departmentId: number,
	): Promise<DepartmentApproverSummaryResponseDto[]> {
		return apiClient.get<DepartmentApproverSummaryResponseDto[]>(
			`${this.resourcePath}/department/${departmentId}`,
		);
	}

	/** All department approvers (single large page) for list summaries. */
	async getApprovers(): Promise<DepartmentApproverSummaryResponseDto[]> {
		const page = await this.getPage<DepartmentApproverSummaryResponseDto>({
			page: 0,
			size: 500,
		});
		return page.content;
	}

	activate(id: number): Promise<void> {
		return apiClient.patch<void>(`${this.resourcePath}/${id}/activate`);
	}

	deactivate(id: number): Promise<void> {
		return apiClient.patch<void>(`${this.resourcePath}/${id}/deactivate`);
	}
}

export const departmentApproverService = new DepartmentApproverService();
