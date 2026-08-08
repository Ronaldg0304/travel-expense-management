<script lang="ts">
	import type { Legalization } from '$lib/models/legalization';
	import { cn, formatCurrency, formatDate } from '$lib/utils';
	import RequestStatusBadge from '../travel-requests/RequestStatusBadge.svelte';

	interface Props {
		legalization: Legalization;
		class?: string;
	}

	let { legalization, class: className }: Props = $props();
</script>

{#snippet item(label: string, value: string)}
	<div>
		<dt class="text-muted-foreground text-xs font-medium uppercase">
			{label}
		</dt>
		<dd class="mt-1 text-sm">{value}</dd>
	</div>
{/snippet}

<div
	class={cn(
		'border-border bg-background rounded-md border p-6',
		className,
	)}
>
	<div
		class="border-border flex flex-col gap-2 border-b pb-4 sm:flex-row sm:items-center sm:justify-between"
	>
		<div>
			<p class="text-sm font-medium">{legalization.requestNumber}</p>
			<p class="text-muted-foreground text-sm">{legalization.applicantName}</p>
		</div>
		<RequestStatusBadge status={legalization.status} />
	</div>

	<dl class="mt-4 grid gap-x-6 gap-y-4 sm:grid-cols-2">
		{@render item('Centro de costo', legalization.costCenterName)}

		{@render item('Total legalizado', formatCurrency(legalization.totalExpenses))}

		{@render item('Enviada el', formatDate(legalization.submittedAt))}
	</dl>

	<div class="border-border mt-6 border-t pt-4">
		<h3 class="text-sm font-semibold">Gastos</h3>
		{#if legalization.expenses.length === 0}
			<p class="text-muted-foreground mt-2 text-sm">
				Sin gastos registrados.
			</p>
		{:else}
			<div class="mt-3 overflow-x-auto">
				<table class="w-full text-sm">
					<thead class="text-muted-foreground border-border border-b text-left">
						<tr>
							<th class="py-2 pr-4 font-medium">Tipo de gasto</th>
							<th class="py-2 pr-4 font-medium">Fecha</th>
							<th class="py-2 pr-4 font-medium">Descripción</th>
							<th class="py-2 text-right font-medium">Valor</th>
						</tr>
					</thead>
					<tbody>
						{#each legalization.expenses as expense (expense.id)}
							<tr class="border-border border-b last:border-0">
								<td class="py-2 pr-4">{expense.expenseTypeName}</td>
								<td class="py-2 pr-4">{formatDate(expense.expenseDate)}</td>
								<td class="py-2 pr-4">{expense.description}</td>
								<td class="py-2 text-right">{formatCurrency(expense.amount)}</td>
							</tr>
						{/each}
					</tbody>
					<tfoot>
						<tr>
							<td class="pt-3 font-semibold" colspan="3">Total</td>
							<td class="pt-3 text-right font-semibold">
								{formatCurrency(legalization.totalExpenses)}
							</td>
						</tr>
					</tfoot>
				</table>
			</div>
		{/if}
	</div>
</div>
