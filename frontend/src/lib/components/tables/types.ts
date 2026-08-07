import type { Snippet } from 'svelte';

export interface Column<T> {
	key?: keyof T;
	header: string;
	cell?: Snippet<[T]>;
	align?: 'left' | 'center' | 'right';
	headerClass?: string;
	cellClass?: string;
}
