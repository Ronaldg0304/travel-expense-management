import { apiClient } from '$lib/api/api-client';
import type { PageRequest, PageResponse } from '$lib/types/pagination';

function toPageParams(pageRequest: PageRequest): URLSearchParams {
	const params = new URLSearchParams();
	params.set('page', String(pageRequest.page));
	params.set('size', String(pageRequest.size));
	for (const sort of pageRequest.sort ?? []) {
		params.append('sort', `${sort.property},${sort.direction}`);
	}
	return params;
}

/**
 * Generic CRUD service over a REST resource path (e.g. `/v1/cost-centers`).
 *
 * `getPage` serializes `PageRequest` as Spring Data `Pageable` query params
 * (`page`, `size`, repeated `sort=property,direction`; `page` is 0-based).
 */
export abstract class BaseService<T, TCreate = T, TUpdate = T, TId = number> {
	protected constructor(protected readonly resourcePath: string) {}

	getById(id: TId): Promise<T> {
		return apiClient.get<T>(`${this.resourcePath}/${id}`);
	}

	getPage(pageRequest: PageRequest): Promise<PageResponse<T>> {
		return apiClient.get<PageResponse<T>>(this.resourcePath, {
			params: toPageParams(pageRequest),
		});
	}

	create(payload: TCreate): Promise<T> {
		return apiClient.post<T>(this.resourcePath, payload);
	}

	update(id: TId, payload: TUpdate): Promise<T> {
		return apiClient.put<T>(`${this.resourcePath}/${id}`, payload);
	}

	patch(id: TId, payload: Partial<TUpdate>): Promise<T> {
		return apiClient.patch<T>(`${this.resourcePath}/${id}`, payload);
	}

	delete(id: TId): Promise<void> {
		return apiClient.delete<void>(`${this.resourcePath}/${id}`);
	}
}
