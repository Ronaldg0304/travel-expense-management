<script lang="ts">
	import { ChevronLeft, ChevronRight } from '@lucide/svelte';
	import { Button } from '$lib/components/ui/button';
	import { cn } from '$lib/utils';

	interface Props {
		page: number;
		pageSize: number;
		totalPages: number;
		totalElements: number;
		pageSizeOptions?: number[];
		disabled?: boolean;
		class?: string;
	}

	let {
		page = $bindable(0),
		pageSize = $bindable(10),
		totalPages,
		totalElements,
		pageSizeOptions = [10, 20, 50],
		disabled = false,
		class: className,
	}: Props = $props();

	const isFirst = $derived(page <= 0);
	const isLast = $derived(page >= totalPages - 1);

	function goToPage(next: number) {
		if (disabled) return;
		page = Math.min(Math.max(next, 0), Math.max(totalPages - 1, 0));
	}

	function handlePageSizeChange(event: Event) {
		if (disabled) return;
		pageSize = Number((event.target as HTMLSelectElement).value);
		page = 0;
	}
</script>

<div
	class={cn(
		'flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between',
		className,
	)}
>
	<p class="text-muted-foreground text-sm tabular-nums">
		{totalElements}
		{totalElements === 1 ? 'record' : 'records'}
	</p>

	<div class="flex flex-wrap items-center gap-3">
		<label class="text-muted-foreground flex items-center gap-2 text-sm">
			<span>Rows per page</span>
			<select
				class="border-border bg-background h-8 rounded-md border px-2 text-sm"
				value={pageSize}
				onchange={handlePageSizeChange}
				{disabled}
			>
				{#each pageSizeOptions as option (option)}
					<option value={option}>{option}</option>
				{/each}
			</select>
		</label>

		<div class="flex items-center gap-1">
			<Button
				variant="outline"
				size="icon-sm"
				onclick={() => goToPage(page - 1)}
				disabled={disabled || isFirst}
				aria-label="Previous page"
			>
				<ChevronLeft class="size-4" aria-hidden="true" />
			</Button>
			<span
				class="text-muted-foreground min-w-24 px-1 text-center text-sm tabular-nums"
			>
				Page {page + 1} of {Math.max(totalPages, 1)}
			</span>
			<Button
				variant="outline"
				size="icon-sm"
				onclick={() => goToPage(page + 1)}
				disabled={disabled || isLast}
				aria-label="Next page"
			>
				<ChevronRight class="size-4" aria-hidden="true" />
			</Button>
		</div>
	</div>
</div>
