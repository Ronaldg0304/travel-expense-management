<script lang="ts">
	import type { Snippet } from 'svelte';
	import { StatusBadge } from '$lib/components/common';
	import { DataTable } from '$lib/components/tables';
	import type { Column } from '$lib/components/tables/types';
	import {
		ACCOUNT_TYPE_LABELS,
		type AccountSummary,
	} from '$lib/models/account';
	import { cn } from '$lib/utils';

	interface Props {
		rows: AccountSummary[];
		loading?: boolean;
		error?: unknown;
		page: number;
		pageSize: number;
		totalPages: number;
		totalElements: number;
		pageSizeOptions?: number[];
		rowActions?: Snippet<[AccountSummary]>;
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

{#snippet typeCell(row: AccountSummary)}
	{ACCOUNT_TYPE_LABELS[row.accountType]}
{/snippet}

{#snippet statusCell(row: AccountSummary)}
	{#if row.active}
		<StatusBadge status="Activo" variant="success" />
	{:else}
		<StatusBadge status="Inactivo" variant="neutral" />
	{/if}
{/snippet}

{#snippet table()}
	{@const columns: Column<AccountSummary>[] = [
		{ header: 'Tipo', cell: typeCell },
		{ key: 'bankName', header: 'Banco', cellClass: 'w-[30%]' },
		{ key: 'accountNumber', header: 'Número de cuenta', cellClass: 'w-[30%]' },
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
		emptyTitle="No hay cuentas"
		emptyDescription="No se encontraron cuentas para los criterios actuales."
		class={cn(className)}
	/>
{/snippet}

{@render table()}
