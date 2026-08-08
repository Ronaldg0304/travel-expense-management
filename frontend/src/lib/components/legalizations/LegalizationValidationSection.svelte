<script lang="ts">
	import { BadgeCheck, LoaderCircle } from '@lucide/svelte';
	import { ApiError, LoadingState } from '$lib/components/feedback';
	import { Button } from '$lib/components/ui/button';
	import { RequestStatusBadge } from '$lib/components/travel-requests';
	import type { SettlementAnalysis } from '$lib/models/settlement';
	import type { RequestStatus } from '$lib/models/travel-request';
	import { formatCurrency } from '$lib/utils';

	interface Props {
		analysis: SettlementAnalysis | null;
		isPending: boolean;
		error?: unknown;
		onRetry?: () => void;
		status: RequestStatus;
		validating: boolean;
		onValidate: () => void;
	}

	let {
		analysis,
		isPending,
		error,
		onRetry,
		status,
		validating,
		onValidate,
	}: Props = $props();
</script>

{#snippet item(label: string, value: string)}
	<div>
		<dt class="text-muted-foreground text-xs font-medium uppercase">
			{label}
		</dt>
		<dd class="mt-1 text-sm font-medium">{value}</dd>
	</div>
{/snippet}

<div class="border-border bg-background rounded-md border p-6">
	<div
		class="flex flex-col gap-2 sm:flex-row sm:items-center sm:justify-between"
	>
		<div>
			<h3 class="text-base font-semibold">Validación financiera</h3>
			<p class="text-muted-foreground text-sm">
				Revisa los montos reportados por el sistema antes de validar.
			</p>
		</div>
		<RequestStatusBadge status={status} />
	</div>

	{#if isPending}
		<div class="mt-5">
			<LoadingState label="Calculando liquidación…" />
		</div>
	{:else if error}
		<div class="mt-5">
			<ApiError
				error={error}
				title="No se pudo calcular la liquidación"
				onRetry={onRetry}
			/>
		</div>
	{:else if analysis}
		<dl class="mt-5 grid gap-x-6 gap-y-4 sm:grid-cols-3">
			{@render item('Monto desembolsado', formatCurrency(analysis.disbursedAmount))}
			{@render item('Total legalizado', formatCurrency(analysis.totalExpenses))}
			{@render item('Diferencia financiera', formatCurrency(analysis.difference))}
		</dl>

		<div
			class="border-border mt-5 flex flex-col gap-3 border-t pt-4 sm:flex-row sm:items-center sm:justify-between"
		>
			<p class="text-muted-foreground text-sm">
				Al validar, el sistema calculará la liquidación y actualizará el
				estado de la solicitud según el resultado.
			</p>
			<Button onclick={onValidate} disabled={validating}>
				{#if validating}
					<LoaderCircle class="size-4 animate-spin" aria-hidden="true" />
				{:else}
					<BadgeCheck aria-hidden="true" />
				{/if}
				Validar legalización
			</Button>
		</div>
	{/if}
</div>
