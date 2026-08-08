import type { DepartmentOptionDto } from '$lib/dto/user';
import { BaseService } from '$lib/services/base.service';

class DepartmentService extends BaseService<DepartmentOptionDto> {
	constructor() {
		super('/v1/departments');
	}

	/** All departments (single large page) for form selects. */
	async getOptions(): Promise<DepartmentOptionDto[]> {
		const page = await this.getPage<DepartmentOptionDto>({
			page: 0,
			size: 500,
		});
		return page.content;
	}
}

export const departmentService = new DepartmentService();
