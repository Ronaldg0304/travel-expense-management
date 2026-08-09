<script lang="ts">
	import type { Snippet } from 'svelte';
	import { StatusBadge } from '$lib/components/common';
	import { DataTable } from '$lib/components/tables';
	import type { Column } from '$lib/components/tables/types';
	import type { DepartmentSummary } from '$lib/models/department';
	import { cn } from '$lib/utils';

	interface Props {
		rows: DepartmentSummary[];
		loading?: boolean;
		error?: unknown;
		page: number;
		pageSize: number;
		totalPages: number;
		totalElements: number;
		pageSizeOptions?: number[];
		/** Number of assigned approvers keyed by department name. */
		approverCounts?: Record<string, number>;
		rowActions?: Snippet<[DepartmentSummary]>;
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
		approverCounts = {},
		rowActions,
		onRetry,
		class: className,
	}: Props = $props();
</script>

{#snippet statusCell(row: DepartmentSummary)}
	{#if row.active}
		<StatusBadge status="Activo" variant="success" />
	{:else}
		<StatusBadge status="Inactivo" variant="neutral" />
	{/if}
{/snippet}

{#snippet approversCell(row: DepartmentSummary)}
	{approverCounts[row.name] ?? 0}
{/snippet}

{#snippet table()}
	{@const columns: Column<DepartmentSummary>[] = [
		{ key: 'code', header: 'Código' },
		{ key: 'name', header: 'Nombre', cellClass: 'w-[40%]' },
		{
			header: 'Aprobadores',
			cell: approversCell,
			align: 'center',
			cellClass: 'w-32',
		},
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
		emptyTitle="No hay departamentos"
		emptyDescription="No se encontraron departamentos para los criterios actuales."
		class={cn(className)}
	/>
{/snippet}

{@render table()}
