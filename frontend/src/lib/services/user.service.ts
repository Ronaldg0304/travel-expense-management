import { apiClient } from '$lib/api/api-client';
import type {
	CreateUserDto,
	UpdateUserDto,
	UserResponseDto,
	UserSummaryResponseDto,
} from '$lib/dto/user';
import { BaseService } from '$lib/services/base.service';
import type { PageRequest, PageResponse } from '$lib/types/pagination';

export interface UserListFilters {
	search?: string;
	active?: boolean;
}

class UserService extends BaseService<
	UserResponseDto,
	CreateUserDto,
	UpdateUserDto
> {
	constructor() {
		super('/v1/users');
	}

	getUsers(
		pageRequest: PageRequest,
		filters?: UserListFilters,
	): Promise<PageResponse<UserSummaryResponseDto>> {
		return this.getPage<UserSummaryResponseDto>(pageRequest, filters);
	}

	activate(id: number): Promise<void> {
		return apiClient.patch<void>(`${this.resourcePath}/${id}/activate`);
	}

	deactivate(id: number): Promise<void> {
		return apiClient.patch<void>(`${this.resourcePath}/${id}/deactivate`);
	}
}

export const userService = new UserService();
