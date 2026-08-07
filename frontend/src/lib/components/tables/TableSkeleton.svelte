<script lang="ts">
	import { cn } from '$lib/utils';

	interface Props {
		rows?: number;
		columns?: number;
		class?: string;
	}

	let { rows = 5, columns = 4, class: className }: Props = $props();

	function range(length: number): number[] {
		const result: number[] = [];
		for (let index = 0; index < length; index++) result.push(index);
		return result;
	}

	const rowIndices = $derived(range(rows));
	const columnIndices = $derived(range(columns));
</script>

<tbody class={cn('', className)} aria-hidden="true">
	{#each rowIndices as i (i)}
		<tr class="border-border border-b last:border-0">
			{#each columnIndices as j (j)}
				<td class="px-3 py-3">
					<div
						class={cn(
							'bg-muted h-4 animate-pulse rounded',
							j === columns - 1 ? 'w-2/3' : 'w-full',
						)}
					></div>
				</td>
			{/each}
		</tr>
	{/each}
</tbody>
