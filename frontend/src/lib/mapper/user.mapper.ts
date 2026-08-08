import type {
	CreateUserDto,
	DepartmentOptionDto,
	UpdateUserDto,
	UserResponseDto,
	UserSummaryResponseDto,
} from '$lib/dto/user';
import type {
	DepartmentOption,
	User,
	UserFormValues,
	UserSummary,
} from '$lib/models/user';

export function toUserSummary(dto: UserSummaryResponseDto): UserSummary {
	return {
		id: dto.id,
		firstName: dto.firstName,
		lastName: dto.lastName,
		email: dto.email,
		role: dto.role,
		active: dto.active,
		departmentName: dto.departmentName,
	};
}

export function toUser(dto: UserResponseDto): User {
	return {
		id: dto.id,
		firstName: dto.firstName,
		lastName: dto.lastName,
		email: dto.email,
		documentType: dto.documentType,
		documentNumber: dto.documentNumber,
		phone: dto.phone,
		position: dto.position,
		role: dto.role,
		active: dto.active,
		departmentId: dto.departmentId,
		departmentName: dto.departmentName,
		createdAt: dto.createdAt,
		updatedAt: dto.updatedAt,
	};
}

export function toDepartmentOption(dto: DepartmentOptionDto): DepartmentOption {
	return {
		id: dto.id,
		code: dto.code,
		name: dto.name,
		active: dto.active,
	};
}

export function toCreateUserDto(values: UserFormValues): CreateUserDto {
	return {
		firstName: values.firstName.trim(),
		lastName: values.lastName.trim(),
		email: values.email.trim(),
		documentType: values.documentType,
		documentNumber: values.documentNumber.trim(),
		phone: values.phone.trim(),
		position: values.position.trim(),
		role: values.role,
		departmentId: values.departmentId as number,
	};
}

export function toUpdateUserDto(values: UserFormValues): UpdateUserDto {
	return {
		firstName: values.firstName.trim(),
		lastName: values.lastName.trim(),
		phone: values.phone.trim(),
		position: values.position.trim(),
		role: values.role,
		departmentId: values.departmentId as number,
		active: values.active,
	};
}
