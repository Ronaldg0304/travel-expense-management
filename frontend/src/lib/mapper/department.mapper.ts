import type {
	CreateDepartmentApproverDto,
	CreateDepartmentDto,
	DepartmentApproverSummaryResponseDto,
	DepartmentResponseDto,
	DepartmentSummaryResponseDto,
	UpdateDepartmentDto,
} from '$lib/dto/department';
import type {
	Department,
	DepartmentApproverSummary,
	DepartmentFormValues,
	DepartmentSummary,
} from '$lib/models/department';

export function toDepartmentSummary(
	dto: DepartmentSummaryResponseDto,
): DepartmentSummary {
	return {
		id: dto.id,
		code: dto.code,
		name: dto.name,
		active: dto.active,
	};
}

export function toDepartment(dto: DepartmentResponseDto): Department {
	return {
		id: dto.id,
		code: dto.code,
		name: dto.name,
		active: dto.active,
		createdAt: dto.createdAt,
		updatedAt: dto.updatedAt,
	};
}

export function toDepartmentFormValues(
	department: Pick<DepartmentSummary, 'code' | 'name' | 'active'>,
): DepartmentFormValues {
	return {
		code: department.code,
		name: department.name,
		active: department.active,
	};
}

export function toCreateDepartmentDto(
	values: DepartmentFormValues,
): CreateDepartmentDto {
	return {
		code: values.code.trim(),
		name: values.name.trim(),
		active: values.active,
	};
}

export function toUpdateDepartmentDto(
	values: DepartmentFormValues,
): UpdateDepartmentDto {
	return {
		name: values.name.trim(),
		active: values.active,
	};
}

export function toDepartmentApproverSummary(
	dto: DepartmentApproverSummaryResponseDto,
): DepartmentApproverSummary {
	return {
		id: dto.id,
		departmentName: dto.departmentName,
		userFullName: dto.userFullName,
		active: dto.active,
	};
}

export function toCreateDepartmentApproverDto(
	departmentId: number,
	userId: number,
	active: boolean,
): CreateDepartmentApproverDto {
	return {
		departmentId,
		userId,
		active,
	};
}
