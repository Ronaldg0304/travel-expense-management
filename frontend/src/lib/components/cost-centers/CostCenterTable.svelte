<script lang="ts">
	import type { Snippet } from 'svelte';
	import { DataTable } from '$lib/components/tables';
	import type { Column } from '$lib/components/tables/types';
	import {
		COST_CENTER_TYPE_LABELS,
		type CostCenterSummary,
	} from '$lib/models/cost-center';
	import { cn } from '$lib/utils';
	import CostCenterStatusBadge from './CostCenterStatusBadge.svelte';

	interface Props {
		rows: CostCenterSummary[];
		loading?: boolean;
		error?: unknown;
		page: number;
		pageSize: number;
		totalPages: number;
		totalElements: number;
		pageSizeOptions?: number[];
		rowActions?: Snippet<[CostCenterSummary]>;
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

{#snippet typeCell(row: CostCenterSummary)}
	{COST_CENTER_TYPE_LABELS[row.type]}
{/snippet}

{#snippet statusCell(row: CostCenterSummary)}
	<CostCenterStatusBadge active={row.active} />
{/snippet}

{#snippet table()}
	{@const columns: Column<CostCenterSummary>[] = [
		{ key: 'code', header: 'Código' },
		{ key: 'name', header: 'Nombre', cellClass: 'w-[40%]' },
		{ header: 'Tipo', cell: typeCell },
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
		emptyTitle="No hay centros de costo"
		emptyDescription="No se encontraron centros de costo para los criterios actuales."
		class={cn(className)}
	/>
{/snippet}

{@render table()}
