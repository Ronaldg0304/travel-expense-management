import { writable, type Writable } from 'svelte/store';

export type ToastType = 'success' | 'error' | 'info';

export interface ToastMessage {
	id: number;
	type: ToastType;
	title: string;
	description?: string;
}

export const toasts: Writable<ToastMessage[]> = writable<ToastMessage[]>([]);

let nextId = 1;

export function showToast(message: Omit<ToastMessage, 'id'>): void {
	const id = nextId++;
	toasts.update((list) => [...list, { id, ...message }]);
	window.setTimeout(() => dismissToast(id), 4000);
}

export function dismissToast(id: number): void {
	toasts.update((list) => list.filter((toast) => toast.id !== id));
}

export const toast = {
	success: (title: string, description?: string) =>
		showToast({ type: 'success', title, description }),
	error: (title: string, description?: string) =>
		showToast({ type: 'error', title, description }),
	info: (title: string, description?: string) =>
		showToast({ type: 'info', title, description }),
} as const;
