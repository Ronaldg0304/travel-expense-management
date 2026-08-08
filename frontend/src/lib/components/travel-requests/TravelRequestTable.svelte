<script lang="ts">
	import type { Snippet } from 'svelte';
	import { DataTable } from '$lib/components/tables';
	import type { Column } from '$lib/components/tables/types';
	import type { TravelRequestSummary } from '$lib/models/travel-request';
	import { cn, formatCurrency, formatDate } from '$lib/utils';
	import RequestStatusBadge from './RequestStatusBadge.svelte';

	interface Props {
		rows: TravelRequestSummary[];
		loading?: boolean;
		error?: unknown;
		page: number;
		pageSize: number;
		totalPages: number;
		totalElements: number;
		pageSizeOptions?: number[];
		rowActions?: Snippet<[TravelRequestSummary]>;
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

{#snippet requestNumberCell(row: TravelRequestSummary)}
	<span class="font-medium">{row.requestNumber}</span>
{/snippet}

{#snippet amountCell(row: TravelRequestSummary)}
	{formatCurrency(row.requestedAmount)}
{/snippet}

{#snippet departureDateCell(row: TravelRequestSummary)}
	{formatDate(row.departureDate)}
{/snippet}

{#snippet returnDateCell(row: TravelRequestSummary)}
	{formatDate(row.returnDate)}
{/snippet}

{#snippet statusCell(row: TravelRequestSummary)}
	<RequestStatusBadge status={row.status} />
{/snippet}

{#snippet table()}
	{@const columns: Column<TravelRequestSummary>[] = [
		{ header: 'Nº solicitud', cell: requestNumberCell },
		{
			key: 'applicantFullName',
			header: 'Solicitante',
			cellClass: 'w-[22%]',
		},
		{ key: 'destination', header: 'Destino', cellClass: 'w-[18%]' },
		{ header: 'Valor solicitado', cell: amountCell },
		{ header: 'Salida', cell: departureDateCell, cellClass: 'w-32' },
		{ header: 'Regreso', cell: returnDateCell, cellClass: 'w-32' },
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
		emptyTitle="No hay solicitudes de viaje"
		emptyDescription="No se encontraron solicitudes de viaje para los criterios actuales."
		class={cn(className)}
	/>
{/snippet}

{@render table()}
