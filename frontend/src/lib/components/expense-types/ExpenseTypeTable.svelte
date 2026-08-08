<script lang="ts">
	import type { Snippet } from 'svelte';
	import { StatusBadge } from '$lib/components/common';
	import { DataTable } from '$lib/components/tables';
	import type { Column } from '$lib/components/tables/types';
	import type { ExpenseTypeSummary } from '$lib/models/expense-type';
	import { cn } from '$lib/utils';

	interface Props {
		rows: ExpenseTypeSummary[];
		loading?: boolean;
		error?: unknown;
		page: number;
		pageSize: number;
		totalPages: number;
		totalElements: number;
		pageSizeOptions?: number[];
		rowActions?: Snippet<[ExpenseTypeSummary]>;
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

{#snippet statusCell(row: ExpenseTypeSummary)}
	{#if row.active}
		<StatusBadge status="Activo" variant="success" />
	{:else}
		<StatusBadge status="Inactivo" variant="neutral" />
	{/if}
{/snippet}

{#snippet table()}
	{@const columns: Column<ExpenseTypeSummary>[] = [
		{ key: 'code', header: 'Código' },
		{ key: 'name', header: 'Nombre', cellClass: 'w-[40%]' },
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
		emptyTitle="No hay tipos de gasto"
		emptyDescription="No se encontraron tipos de gasto para los criterios actuales."
		class={cn(className)}
	/>
{/snippet}

{@render table()}
