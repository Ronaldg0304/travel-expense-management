import type { Component } from 'svelte';
import type { UserRole } from '$lib/auth';

export interface NavItem {
	label: string;
	href: string;
	icon?: Component<{ class?: string }>;
	badge?: string;
	disabled?: boolean;
}

export interface NavSection {
	label?: string;
	items: NavItem[];
}

export interface RoleNavigation {
	/** Roles that can see these sections. Empty = visible to all authenticated users. */
	roles: readonly UserRole[];
	sections: NavSection[];
}
