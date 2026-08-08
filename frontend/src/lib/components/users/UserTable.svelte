<script lang="ts">
	import type { Snippet } from 'svelte';
	import { ROLE_LABELS } from '$lib/auth/auth.types';
	import { DataTable } from '$lib/components/tables';
	import type { Column } from '$lib/components/tables/types';
	import type { UserSummary } from '$lib/models/user';
	import { cn } from '$lib/utils';
	import UserStatusBadge from './UserStatusBadge.svelte';

	interface Props {
		rows: UserSummary[];
		loading?: boolean;
		error?: unknown;
		page: number;
		pageSize: number;
		totalPages: number;
		totalElements: number;
		pageSizeOptions?: number[];
		rowActions?: Snippet<[UserSummary]>;
		onRetry?: () => void;
		class?: string;
	}

	let {
		rows,
		loading = false,
		error,
		page = $bindable(0),
		pageSize = $bindable(10),
		totalPages,
		totalElements,
		pageSizeOptions,
		rowActions,
		onRetry,
		class: className,
	}: Props = $props();
</script>

{#snippet userCell(row: UserSummary)}
	<div class="min-w-0">
		<p class="truncate font-medium">
			{row.firstName}
			{row.lastName}
		</p>
		<p class="text-muted-foreground truncate text-xs">{row.email}</p>
	</div>
{/snippet}

{#snippet roleCell(row: UserSummary)}
	{ROLE_LABELS[row.role]}
{/snippet}

{#snippet statusCell(row: UserSummary)}
	<UserStatusBadge active={row.active} />
{/snippet}

{#snippet table()}
	{@const columns: Column<UserSummary>[] = [
		{ header: 'Usuario', cell: userCell, cellClass: 'w-[30%]' },
		{ header: 'Rol', cell: roleCell },
		{ key: 'departmentName', header: 'Departamento' },
		{
			header: 'Estado',
			cell: statusCell,
			align: 'center',
			cellClass: 'w-32',
		},
	]}

	<DataTable
		{rows}
		{columns}
		{loading}
		{error}
		bind:page
		bind:pageSize
		{totalPages}
		{totalElements}
		{pageSizeOptions}
		{rowActions}
		{onRetry}
		getRowId={(row) => row.id}
		emptyTitle="No hay usuarios"
		emptyDescription="No se encontraron usuarios para los criterios actuales."
		class={cn(className)}
	/>
{/snippet}

{@render table()}
