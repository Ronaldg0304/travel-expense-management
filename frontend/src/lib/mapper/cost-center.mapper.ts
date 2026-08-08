import type {
	CostCenterResponseDto,
	CostCenterSummaryResponseDto,
	CreateCostCenterDto,
	UpdateCostCenterDto,
} from '$lib/dto/cost-center';
import type {
	CostCenter,
	CostCenterFormValues,
	CostCenterSummary,
} from '$lib/models/cost-center';

export function toCostCenterSummary(
	dto: CostCenterSummaryResponseDto,
): CostCenterSummary {
	return {
		id: dto.id,
		code: dto.code,
		name: dto.name,
		type: dto.type,
		active: dto.active,
	};
}

export function toCostCenter(dto: CostCenterResponseDto): CostCenter {
	return {
		id: dto.id,
		code: dto.code,
		name: dto.name,
		type: dto.type,
		active: dto.active,
		createdAt: dto.createdAt,
		updatedAt: dto.updatedAt,
	};
}

export function toCreateCostCenterDto(
	values: CostCenterFormValues,
): CreateCostCenterDto {
	return {
		code: values.code.trim(),
		name: values.name.trim(),
		type: values.type,
		active: values.active,
	};
}

export function toUpdateCostCenterDto(
	values: CostCenterFormValues,
): UpdateCostCenterDto {
	return {
		name: values.name.trim(),
		type: values.type,
		active: values.active,
	};
}
