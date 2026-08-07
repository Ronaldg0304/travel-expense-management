<script lang="ts" generics="T">
	import type { Snippet } from 'svelte';
	import { ApiError, EmptyState } from '$lib/components/feedback';
	import { TablePagination, TableSkeleton } from '$lib/components/tables';
	import { cn } from '$lib/utils';
	import type { Column } from './types';

	interface Props {
		rows: T[];
		columns: Column<T>[];
		loading?: boolean;
		error?: unknown;
		page: number;
		pageSize: number;
		totalPages: number;
		totalElements: number;
		pageSizeOptions?: number[];
		getRowId?: (row: T) => string | number;
		emptyTitle?: string;
		emptyDescription?: string;
		emptyAction?: Snippet;
		emptySlot?: Snippet;
		errorSlot?: Snippet;
		loadingSlot?: Snippet;
		rowActions?: Snippet<[T]>;
		onRetry?: () => void;
		class?: string;
	}

	let {
		rows,
		columns,
		loading = false,
		error,
		page = $bindable(0),
		pageSize = $bindable(10),
		totalPages,
		totalElements,
		pageSizeOptions,
		getRowId,
		emptyTitle = 'No records',
		emptyDescription = 'There are no records to display yet.',
		emptyAction,
		emptySlot,
		errorSlot,
		loadingSlot,
		rowActions,
		onRetry,
		class: className,
	}: Props = $props();

	const columnCount = $derived(columns.length + (rowActions ? 1 : 0));

	function alignClass(column: Column<T>): string {
		switch (column.align) {
			case 'right':
				return 'text-right';
			case 'center':
				return 'text-center';
			default:
				return 'text-left';
		}
	}
</script>

<div
	class={cn(
		'border-border bg-background w-full overflow-hidden rounded-md border',
		className,
	)}
>
	<div class="overflow-x-auto">
		<table class="w-full caption-bottom text-sm">
			<thead>
				<tr class="border-border bg-muted/50 border-b">
					{#each columns as column, i (i)}
						<th
							scope="col"
							class={cn(
								'text-muted-foreground h-10 px-3 align-middle text-xs font-semibold tracking-wider uppercase',
								alignClass(column),
								column.headerClass,
							)}
						>
							{column.header}
						</th>
					{/each}
					{#if rowActions}
						<th
							scope="col"
							class="text-muted-foreground h-10 px-3 text-right align-middle text-xs font-semibold tracking-wider uppercase"
						>
							Actions
						</th>
					{/if}
				</tr>
			</thead>

			{#if loading}
				{#if loadingSlot}
					{@render loadingSlot()}
				{:else}
					<TableSkeleton rows={pageSize} columns={columnCount} />
				{/if}
			{:else if error}
				<tbody>
					<tr>
						<td colspan={columnCount} class="p-4">
							{#if errorSlot}
								{@render errorSlot()}
							{:else}
								<ApiError {error} {onRetry} />
							{/if}
						</td>
					</tr>
				</tbody>
			{:else if rows.length === 0}
				<tbody>
					<tr>
						<td colspan={columnCount} class="p-4">
							{#if emptySlot}
								{@render emptySlot()}
							{:else}
								<EmptyState
									title={emptyTitle}
									description={emptyDescription}
									action={emptyAction}
								/>
							{/if}
						</td>
					</tr>
				</tbody>
			{:else}
				<tbody>
					{#each rows as row, i (getRowId ? getRowId(row) : i)}
						<tr
							class="border-border hover:bg-muted/50 border-b transition-colors last:border-0"
						>
							{#each columns as column, i (i)}
								<td
									class={cn(
										'text-foreground px-3 py-2.5 align-middle',
										alignClass(column),
										column.cellClass,
									)}
								>
									{#if column.cell}
										{@render column.cell(row)}
									{:else if column.key}
										{row[column.key] == null ? '' : String(row[column.key])}
									{/if}
								</td>
							{/each}
							{#if rowActions}
								<td class="text-foreground px-3 py-2.5 text-right align-middle">
									{@render rowActions(row)}
								</td>
							{/if}
						</tr>
					{/each}
				</tbody>
			{/if}
		</table>
	</div>

	{#if totalPages > 1}
		<div class="border-border border-t p-3">
			<TablePagination
				bind:page
				bind:pageSize
				{totalPages}
				{totalElements}
				pageSizeOptions={pageSizeOptions ?? [10, 20, 50]}
				disabled={loading}
			/>
		</div>
	{/if}
</div>
