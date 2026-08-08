import { resolve } from '$app/paths';
import type { PathnameWithSearchOrHash } from '$app/types';
import type { AuthenticatedUser } from '$lib/auth';
import { hasAnyRole } from '$lib/auth/permission';
import { ROUTES } from '$lib/constants/routes';
import {
	Building2,
	CircleDollarSign,
	CreditCard,
	FileCheck2,
	FileClock,
	History,
	Landmark,
	LayoutDashboard,
	ListChecks,
	Plane,
	PlusCircle,
	Receipt,
	Tags,
	Undo2,
	Users,
	Wallet,
} from '@lucide/svelte';
import type { NavItem, NavSection, RoleNavigation } from './navigation.types';

export const navigationConfig: readonly RoleNavigation[] = [
	{
		roles: ['ADMINISTRADOR'],
		sections: [
			{
				label: 'General',
				items: [
					{
						label: 'Dashboard',
						href: ROUTES.dashboard.home,
						icon: LayoutDashboard,
					},
				],
			},
			{
				label: 'Administración',
				items: [
					{ label: 'Usuarios', href: ROUTES.admin.users, icon: Users },
					{
						label: 'Departamentos',
						href: ROUTES.admin.departments,
						icon: Building2,
					},
					{
						label: 'Centros de costo',
						href: ROUTES.admin.costCenters,
						icon: CircleDollarSign,
					},
					{
						label: 'Tipos de gasto',
						href: ROUTES.admin.expenseTypes,
						icon: Tags,
					},
					{ label: 'Cuentas', href: ROUTES.admin.accounts, icon: CreditCard },
				],
			},
			{
				label: 'Operación',
				items: [
					{
						label: 'Solicitudes de viaje',
						href: ROUTES.admin.travelRequests,
						icon: Plane,
					},
					{ label: 'Auditoría', href: ROUTES.admin.audit, icon: History },
				],
			},
		],
	},
	{
		roles: ['EMPLEADO'],
		sections: [
			{
				label: 'General',
				items: [
					{
						label: 'Dashboard',
						href: ROUTES.dashboard.home,
						icon: LayoutDashboard,
					},
				],
			},
			{
				label: 'Solicitudes',
				items: [
					{
						label: 'Mis solicitudes',
						href: ROUTES.employee.myRequests,
						icon: ListChecks,
					},
					{
						label: 'Nueva solicitud',
						href: ROUTES.employee.newRequest,
						icon: PlusCircle,
					},
				],
			},
		],
	},
	{
		roles: ['AVALADOR'],
		sections: [
			{
				label: 'General',
				items: [
					{
						label: 'Dashboard',
						href: ROUTES.dashboard.home,
						icon: LayoutDashboard,
					},
				],
			},
			{
				label: 'Aprobaciones',
				items: [
					{
						label: 'Solicitudes pendientes',
						href: ROUTES.approver.pendingRequests,
						icon: FileClock,
					},
					{
						label: 'Historial de aprobaciones',
						href: ROUTES.approver.approvalHistory,
						icon: FileCheck2,
					},
				],
			},
		],
	},
	{
		roles: ['FINANCIERA'],
		sections: [
			{
				label: 'General',
				items: [
					{
						label: 'Dashboard',
						href: ROUTES.dashboard.home,
						icon: LayoutDashboard,
					},
				],
			},
			{
				label: 'Finanzas',
				items: [
					{
						label: 'Desembolsos',
						href: ROUTES.finance.disbursements,
						icon: Wallet,
					},
					{
						label: 'Legalizaciones',
						href: ROUTES.finance.settlements,
						icon: Receipt,
					},
					{
						label: 'Reembolsos',
						href: ROUTES.finance.reimbursements,
						icon: Landmark,
					},
					{ label: 'Devoluciones', href: ROUTES.finance.refunds, icon: Undo2 },
				],
			},
		],
	},
];

export function getNavigationForRoles(
	user: AuthenticatedUser | null,
): NavSection[] {
	const merged = new Map<string, NavSection>();
	for (const navigation of navigationConfig) {
		if (!hasAnyRole(user, navigation.roles)) continue;
		for (const section of navigation.sections) {
			const key = section.label ?? '';
			const existing = merged.get(key);
			if (existing) {
				for (const item of section.items) {
					if (
						!existing.items.some(
							(existingItem) => existingItem.href === item.href,
						)
					) {
						existing.items.push(item);
					}
				}
			} else {
				merged.set(key, { label: section.label, items: [...section.items] });
			}
		}
	}
	return [...merged.values()];
}

type ResolveRoute = PathnameWithSearchOrHash;

export function isNavItemActive(item: NavItem, pathname: string): boolean {
	const href = resolve(item.href as ResolveRoute);
	if (href === '/') return pathname === '/';
	return pathname === href || pathname.startsWith(`${href}/`);
}

export function findActiveNavigationItem(
	sections: readonly NavSection[],
	pathname: string,
): NavItem | null {
	for (const section of sections) {
		for (const item of section.items) {
			if (isNavItemActive(item, pathname)) return item;
		}
	}
	return null;
}
