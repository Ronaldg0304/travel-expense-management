<script lang="ts">
	import { goto } from '$app/navigation';
	import { resolve } from '$app/paths';
	import {
		createMutation,
		createQuery,
		keepPreviousData,
		useQueryClient,
	} from '@tanstack/svelte-query';
	import { Pencil, Plus, Power, PowerOff } from '@lucide/svelte';
	import { getApiErrorMessage } from '$lib/api/api-error';
	import { Button } from '$lib/components/ui/button';
	import { ConfirmDialog, PageHeader } from '$lib/components/common';
	import { toast } from '$lib/components/feedback';
	import { CostCenterTable } from '$lib/components/cost-centers';
	import { ROUTES } from '$lib/constants/routes';
	import type { CostCenterSummary } from '$lib/models/cost-center';
	import { costCenterService } from '$lib/services';

	let page = $state(0);
	let pageSize = $state(10);

	const query = createQuery(() => ({
		queryKey: ['cost-centers', { page, pageSize }],
		queryFn: () =>
			costCenterService.getCostCenters({
				page,
				size: pageSize,
				sort: [{ property: 'id', direction: 'asc' }],
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

	const queryClient = useQueryClient();

	const statusMutation = createMutation(() => ({
		mutationFn: ({
			id,
			action,
		}: {
			id: number;
			action: 'activate' | 'deactivate';
		}) =>
			action === 'activate'
				? costCenterService.activate(id)
				: costCenterService.deactivate(id),
		onSuccess: (_result, variables) => {
			void queryClient.invalidateQueries({ queryKey: ['cost-centers'] });
			toast.success(
				variables.action === 'activate'
					? 'Centro de costo activado'
					: 'Centro de costo desactivado',
			);
		},
		onError: (error) => {
			toast.error('No se pudo actualizar el estado', getApiErrorMessage(error));
		},
	}));

	let statusTarget = $state<CostCenterSummary | null>(null);

	const dialogOpen = $derived(statusTarget !== null);

	function openStatusDialog(row: CostCenterSummary) {
		statusTarget = row;
	}

	function closeStatusDialog() {
		statusTarget = null;
	}

	async function confirmStatusChange() {
		if (!statusTarget) return;
		await statusMutation.mutateAsync({
			id: statusTarget.id,
			action: statusTarget.active ? 'deactivate' : 'activate',
		});
		statusTarget = null;
	}

	function handleRetry() {
		void query.refetch();
	}
</script>

{#snippet headerActions()}
	<Button onclick={() => void goto(resolve(ROUTES.admin.costCentersNew))}>
		<Plus aria-hidden="true" />
		Nuevo centro de costo
	</Button>
{/snippet}

{#snippet rowActions(row: CostCenterSummary)}
	<div class="flex items-center justify-end gap-0.5">
		<Button
			href={`${ROUTES.admin.costCenters}/${row.id}`}
			variant="ghost"
			size="icon-sm"
			aria-label={`Editar ${row.name}`}
		>
			<Pencil aria-hidden="true" />
		</Button>
		{#if row.active}
			<Button
				variant="ghost"
				size="icon-sm"
				onclick={() => openStatusDialog(row)}
				aria-label={`Desactivar ${row.name}`}
			>
				<PowerOff class="text-destructive" aria-hidden="true" />
			</Button>
		{:else}
			<Button
				variant="ghost"
				size="icon-sm"
				onclick={() => openStatusDialog(row)}
				aria-label={`Activar ${row.name}`}
			>
				<Power class="text-success" aria-hidden="true" />
			</Button>
		{/if}
	</div>
{/snippet}

<div class="space-y-4">
	<PageHeader
		title="Centros de costo"
		description="Administra los centros de costo de la organización."
		actions={headerActions}
	/>

	<CostCenterTable
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

	<ConfirmDialog
		open={dialogOpen}
		title={statusTarget?.active
			? 'Desactivar centro de costo'
			: 'Activar centro de costo'}
		description={statusTarget
			? `¿Seguro que quieres ${statusTarget.active ? 'desactivar' : 'activar'} el centro de costo ${statusTarget.name}?`
			: undefined}
		confirmLabel={statusTarget?.active ? 'Desactivar' : 'Activar'}
		variant={statusTarget?.active ? 'destructive' : 'default'}
		onConfirm={confirmStatusChange}
		onCancel={closeStatusDialog}
	/>
</div>
