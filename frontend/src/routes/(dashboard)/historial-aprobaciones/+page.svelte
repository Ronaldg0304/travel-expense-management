<script lang="ts">
	import { createQuery, keepPreviousData } from '@tanstack/svelte-query';
	import { Eye, History } from '@lucide/svelte';
	import { Button } from '$lib/components/ui/button';
	import { PageHeader, StatusBadge } from '$lib/components/common';
	import { DataTable } from '$lib/components/tables';
	import type { Column } from '$lib/components/tables/types';
	import { TravelRequestTable } from '$lib/components/travel-requests';
	import { ROUTES } from '$lib/constants/routes';
	import { APPROVAL_DECISION_LABELS } from '$lib/models/approval';
	import type { ApprovalDecision } from '$lib/models/approval';
	import type { ApprovalSummaryResponseDto } from '$lib/dto/approval';
	import type { TravelRequestSummary } from '$lib/models/travel-request';
	import { travelApprovalService, travelRequestService } from '$lib/services';
	import type { SemanticColor } from '$lib/theme/tokens';
	import { formatDate } from '$lib/utils/format';

	let page = $state(0);
	let pageSize = $state(10);
	let selectedRequestId = $state<number | null>(null);
	let historyPage = $state(0);
	let historyPageSize = $state(10);

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
		(data?.content ?? []).filter(
			(row) => row.status !== 'BORRADOR' && row.status !== 'ENVIADA',
		),
	);
	const totalPages = $derived(rows.length > 0 ? 1 : 0);
	const totalElements = $derived(rows.length);

	const selectedRequest = $derived(
		rows.find((row) => row.id === selectedRequestId),
	);

	const historyQuery = createQuery(() => ({
		queryKey: ['approval-history', selectedRequestId],
		queryFn: () => {
			if (selectedRequestId == null) {
				throw new Error('No hay una solicitud seleccionada');
			}
			return travelApprovalService.getHistory(selectedRequestId);
		},
		enabled: selectedRequestId != null,
	}));

	const historyRows = $derived(historyQuery.data ?? []);
	const historyTotalPages = $derived(historyRows.length > 0 ? 1 : 0);
	const historyTotalElements = $derived(historyRows.length);

	const decisionVariants: Record<ApprovalDecision, SemanticColor> = {
		APROBADA: 'success',
		DEVUELTA: 'warning',
		RECHAZADA: 'destructive',
	};

	function handleRetry() {
		void query.refetch();
	}

	function handleHistoryRetry() {
		void historyQuery.refetch();
	}
</script>

{#snippet rowActions(row: TravelRequestSummary)}
	<div class="flex items-center justify-end gap-0.5">
		<Button
			href={`${ROUTES.approver.approvals}/${row.id}`}
			variant="ghost"
			size="icon-sm"
			aria-label={`Ver solicitud ${row.requestNumber}`}
		>
			<Eye aria-hidden="true" />
		</Button>
		<Button
			variant="ghost"
			size="icon-sm"
			aria-label={`Ver historial de ${row.requestNumber}`}
			onclick={() => (selectedRequestId = row.id)}
		>
			<History aria-hidden="true" />
		</Button>
	</div>
{/snippet}

{#snippet approverCell(row: ApprovalSummaryResponseDto)}
	<span class="font-medium">{row.approverName}</span>
{/snippet}

{#snippet decisionCell(row: ApprovalSummaryResponseDto)}
	<StatusBadge
		status={APPROVAL_DECISION_LABELS[row.decision]}
		variant={decisionVariants[row.decision]}
	/>
{/snippet}

{#snippet decisionDateCell(row: ApprovalSummaryResponseDto)}
	{formatDate(row.decisionDate)}
{/snippet}

{#snippet historyTable()}
	{@const columns: Column<ApprovalSummaryResponseDto>[] = [
		{ header: 'Aprobador', cell: approverCell },
		{
			header: 'Decisión',
			cell: decisionCell,
			align: 'center',
			cellClass: 'w-32',
		},
		{
			header: 'Fecha',
			cell: decisionDateCell,
			cellClass: 'w-32',
		},
	]}

	<DataTable
		rows={historyRows}
		columns={columns}
		loading={historyQuery.isPending}
		error={historyQuery.error}
		bind:page={historyPage}
		bind:pageSize={historyPageSize}
		totalPages={historyTotalPages}
		totalElements={historyTotalElements}
		onRetry={handleHistoryRetry}
		getRowId={(row) => row.id}
		emptyTitle="Sin historial"
		emptyDescription="Esta solicitud no tiene decisiones de aprobación registradas."
	/>
{/snippet}

<div class="space-y-6">
	<PageHeader
		title="Historial de aprobaciones"
		description="Consulta el historial de decisiones de aprobación de las solicitudes de viaje."
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
		emptyTitle="No hay solicitudes con decisiones"
		emptyDescription="No hay solicitudes de viaje con decisiones de aprobación registradas."
	/>

	{#if selectedRequestId != null}
		<section class="space-y-3" aria-labelledby="approval-history-title">
			<div class="flex flex-wrap items-center justify-between gap-2">
				<h2 id="approval-history-title" class="text-lg font-semibold">
					Historial de la solicitud
					{#if selectedRequest}
						{selectedRequest.requestNumber}
					{/if}
				</h2>
				{#if selectedRequest}
					<Button
						href={`${ROUTES.approver.approvals}/${selectedRequest.id}`}
						variant="outline"
						size="sm"
					>
						<Eye aria-hidden="true" />
						Ver solicitud
					</Button>
				{/if}
			</div>

			{@render historyTable()}
		</section>
	{/if}
</div>
