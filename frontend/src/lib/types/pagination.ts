/** Spring Data `Pageable` sort entry, serialized as `property,direction`. */
export interface SortField {
	property: string;
	direction: 'asc' | 'desc';
}

/** Request params sent to a Spring Data `Pageable` endpoint (`page` is 0-based). */
export interface PageRequest {
	page: number;
	size: number;
	sort?: SortField[];
}

/** Metadata portion of a Spring Data `Page` response. */
export interface PageMetadata {
	page: number;
	size: number;
	totalElements: number;
	totalPages: number;
	last: boolean;
}

/** Spring Data `Page` response shape (`content` plus paging metadata). */
export interface PageResponse<T> {
	content: T[];
	page: number;
	size: number;
	totalElements: number;
	totalPages: number;
	last: boolean;
}

/** Backwards-compatible alias used by the existing Axios layer. */
export type Page<T> = PageResponse<T>;
