import { apiClient } from '$lib/api/api-client';
import type {
	CreateDepartmentDto,
	DepartmentResponseDto,
	DepartmentSummaryResponseDto,
	UpdateDepartmentDto,
} from '$lib/dto/department';
import type { DepartmentOptionDto } from '$lib/dto/user';
import { BaseService } from '$lib/services/base.service';
import type { PageRequest, PageResponse } from '$lib/types/pagination';

class DepartmentService extends BaseService<
	DepartmentResponseDto,
	CreateDepartmentDto,
	UpdateDepartmentDto
> {
	constructor() {
		super('/v1/departments');
	}

	getDepartments(
		pageRequest: PageRequest,
	): Promise<PageResponse<DepartmentSummaryResponseDto>> {
		return this.getPage<DepartmentSummaryResponseDto>(pageRequest);
	}

	/** All departments (single large page) for form selects. */
	async getOptions(): Promise<DepartmentOptionDto[]> {
		const page = await this.getPage<DepartmentOptionDto>({
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

export const departmentService = new DepartmentService();
