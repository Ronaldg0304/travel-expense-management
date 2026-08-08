<script lang="ts">
	import { createQuery, keepPreviousData } from '@tanstack/svelte-query';
	import { Eye } from '@lucide/svelte';
	import { Button } from '$lib/components/ui/button';
	import { PageHeader } from '$lib/components/common';
	import { TravelRequestTable } from '$lib/components/travel-requests';
	import { ROUTES } from '$lib/constants/routes';
	import type { RequestStatus } from '$lib/models/travel-request';
	import type { TravelRequestSummary } from '$lib/models/travel-request';
	import { travelRequestService } from '$lib/services';

	type LegalizationListFilter = Extract<
		RequestStatus,
		'DESEMBOLSADA' | 'LEGALIZADA'
	>;

	const FILTERS: { value: LegalizationListFilter; label: string }[] = [
		{ value: 'DESEMBOLSADA', label: 'Por legalizar' },
		{ value: 'LEGALIZADA', label: 'Por validar' },
	];

	let page = $state(0);
	let pageSize = $state(10);
	let statusFilter = $state<LegalizationListFilter>('LEGALIZADA');

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
		(data?.content ?? []).filter((row) => row.status === statusFilter),
	);
	const totalPages = $derived(rows.length > 0 ? 1 : 0);
	const totalElements = $derived(rows.length);

	const headerByFilter = $derived.by(() => {
		if (statusFilter === 'DESEMBOLSADA') {
			return {
				title: 'Legalizaciones pendientes',
				description:
					'Revisa las solicitudes desembolsadas que están listas para legalizar.',
				emptyTitle: 'No hay solicitudes para legalizar',
				emptyDescription:
					'No hay solicitudes desembolsadas pendientes de legalización en este momento.',
				actionLabel: 'Legalizar solicitud',
			};
		}
		return {
			title: 'Legalizaciones por validar',
			description:
				'Revisa las legalizaciones enviadas que están listas para la validación financiera.',
			emptyTitle: 'No hay legalizaciones por validar',
			emptyDescription:
				'No hay legalizaciones enviadas pendientes de validación financiera en este momento.',
			actionLabel: 'Validar legalización',
		};
	});

	function handleRetry() {
		void query.refetch();
	}

	function selectFilter(value: LegalizationListFilter) {
		statusFilter = value;
		page = 0;
	}
</script>

{#snippet rowActions(row: TravelRequestSummary)}
	<div class="flex items-center justify-end gap-0.5">
		<Button
			href={`${ROUTES.finance.settlements}/${row.id}`}
			variant="ghost"
			size="icon-sm"
			aria-label={`${headerByFilter.actionLabel} ${row.requestNumber}`}
		>
			<Eye aria-hidden="true" />
		</Button>
	</div>
{/snippet}

<div class="space-y-4">
	<PageHeader
		title={headerByFilter.title}
		description={headerByFilter.description}
	>
		{#snippet actions()}
			<div class="flex items-center gap-2">
				{#each FILTERS as filter (filter.value)}
					<Button
						variant={statusFilter === filter.value ? 'secondary' : 'outline'}
						size="sm"
						onclick={() => selectFilter(filter.value)}
					>
						{filter.label}
					</Button>
				{/each}
			</div>
		{/snippet}
	</PageHeader>

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
		emptyTitle={headerByFilter.emptyTitle}
		emptyDescription={headerByFilter.emptyDescription}
	/>
</div>
