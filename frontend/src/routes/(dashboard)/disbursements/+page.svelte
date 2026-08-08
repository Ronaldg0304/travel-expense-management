<script lang="ts">
	import { createQuery, keepPreviousData } from '@tanstack/svelte-query';
	import { Eye } from '@lucide/svelte';
	import { Button } from '$lib/components/ui/button';
	import { PageHeader } from '$lib/components/common';
	import { TravelRequestTable } from '$lib/components/travel-requests';
	import { ROUTES } from '$lib/constants/routes';
	import type { TravelRequestSummary } from '$lib/models/travel-request';
	import { travelRequestService } from '$lib/services';

	let page = $state(0);
	let pageSize = $state(10);

	const query = createQuery(() => ({
		queryKey: ['travel-requests', { page, pageSize }],
		queryFn: () =>
			travelRequestService.getTravelRequests({
				page,
				size: pageSize,
				sort: [{ property: 'id', direction: 'desc' }],
			}),
		placeholderData: keepPreviousData,
	}));

	const data = $derived(query.data);
	const rows = $derived(
		(data?.content ?? []).filter((row) => row.status === 'APROBADA'),
	);
	const totalPages = $derived(rows.length > 0 ? 1 : 0);
	const totalElements = $derived(rows.length);

	function handleRetry() {
		void query.refetch();
	}
</script>

{#snippet rowActions(row: TravelRequestSummary)}
	<div class="flex items-center justify-end gap-0.5">
		<Button
			href={`${ROUTES.finance.disbursements}/${row.id}`}
			variant="ghost"
			size="icon-sm"
			aria-label={`Desembolsar solicitud ${row.requestNumber}`}
		>
			<Eye aria-hidden="true" />
		</Button>
	</div>
{/snippet}

<div class="space-y-4">
	<PageHeader
		title="Desembolsos pendientes"
		description="Revisa las solicitudes aprobadas que están listas para desembolsar."
	/>

	<TravelRequestTable
		{rows}
		loading={query.isPending}
		error={query.error}
		bind:page
		bind:pageSize
		{totalPages}
		{totalElements}
		onRetry={handleRetry}
		{rowActions}
		emptyTitle="No hay solicitudes para desembolsar"
		emptyDescription="No hay solicitudes aprobadas pendientes de desembolso en este momento."
	/>
</div>
