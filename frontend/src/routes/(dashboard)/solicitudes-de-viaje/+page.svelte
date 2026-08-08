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
	const rows = $derived(data?.content ?? []);
	const totalPages = $derived(data?.totalPages ?? 0);
	const totalElements = $derived(data?.totalElements ?? 0);

	$effect(() => {
		if (totalPages > 0 && page >= totalPages) {
			page = Math.max(totalPages - 1, 0);
		}
	});

	function handleRetry() {
		void query.refetch();
	}
</script>

{#snippet rowActions(row: TravelRequestSummary)}
	<div class="flex items-center justify-end gap-0.5">
		<Button
			href={`${ROUTES.admin.travelRequests}/${row.id}`}
			variant="ghost"
			size="icon-sm"
			aria-label={`Ver solicitud ${row.requestNumber}`}
		>
			<Eye aria-hidden="true" />
		</Button>
	</div>
{/snippet}

<div class="space-y-4">
	<PageHeader
		title="Solicitudes de viaje"
		description="Consulta las solicitudes de viaje de la organización."
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
	/>
</div>
