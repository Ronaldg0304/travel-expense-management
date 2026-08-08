<script lang="ts">
	import type { TravelRequest } from '$lib/models/travel-request';
	import { cn, formatCurrency, formatDate } from '$lib/utils';
	import RequestStatusBadge from './RequestStatusBadge.svelte';

	interface Props {
		request: TravelRequest;
		class?: string;
	}

	let { request, class: className }: Props = $props();
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
			<p class="text-sm font-medium">{request.requestNumber}</p>
			<p class="text-muted-foreground text-sm">{request.applicantFullName}</p>
		</div>
		<RequestStatusBadge status={request.status} />
	</div>

	<dl class="mt-4 grid gap-x-6 gap-y-4 sm:grid-cols-2">
		<div class="sm:col-span-2">
			{@render item('Motivo del viaje', request.travelPurpose)}
		</div>

		{@render item('Destino', request.destination)}

		{@render item('Departamento', request.departmentName)}

		{@render item('Fecha de salida', formatDate(request.departureDate))}

		{@render item('Fecha de regreso', formatDate(request.returnDate))}

		{@render item('Valor solicitado', formatCurrency(request.requestedAmount))}

		{@render item(
			'Valor aprobado',
			request.approvedAmount === null
				? '—'
				: formatCurrency(request.approvedAmount),
		)}

		{@render item('Creada el', formatDate(request.createdAt))}

		{@render item('Actualizada el', formatDate(request.updatedAt))}
	</dl>
</div>
