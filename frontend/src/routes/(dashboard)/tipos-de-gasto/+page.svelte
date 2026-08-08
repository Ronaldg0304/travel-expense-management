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
	import { ExpenseTypeTable } from '$lib/components/expense-types';
	import { ROUTES } from '$lib/constants/routes';
	import type { ExpenseTypeSummary } from '$lib/models/expense-type';
	import { expenseTypeService } from '$lib/services';

	let page = $state(0);
	let pageSize = $state(10);

	const query = createQuery(() => ({
		queryKey: ['expense-types', { page, pageSize }],
		queryFn: () =>
			expenseTypeService.getExpenseTypes({
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
				? expenseTypeService.activate(id)
				: expenseTypeService.deactivate(id),
		onSuccess: (_result, variables) => {
			void queryClient.invalidateQueries({ queryKey: ['expense-types'] });
			toast.success(
				variables.action === 'activate'
					? 'Tipo de gasto activado'
					: 'Tipo de gasto desactivado',
			);
		},
		onError: (error) => {
			toast.error('No se pudo actualizar el estado', getApiErrorMessage(error));
		},
	}));

	let statusTarget = $state<ExpenseTypeSummary | null>(null);

	const dialogOpen = $derived(statusTarget !== null);

	function openStatusDialog(row: ExpenseTypeSummary) {
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
	<Button onclick={() => void goto(resolve(ROUTES.admin.expenseTypesNew))}>
		<Plus aria-hidden="true" />
		Nuevo tipo de gasto
	</Button>
{/snippet}

{#snippet rowActions(row: ExpenseTypeSummary)}
	<div class="flex items-center justify-end gap-0.5">
		<Button
			href={`${ROUTES.admin.expenseTypes}/${row.id}`}
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
		title="Tipos de gasto"
		description="Administra el catálogo de tipos de gasto de la organización."
		actions={headerActions}
	/>

	<ExpenseTypeTable
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
			? 'Desactivar tipo de gasto'
			: 'Activar tipo de gasto'}
		description={statusTarget
			? `¿Seguro que quieres ${statusTarget.active ? 'desactivar' : 'activar'} el tipo de gasto ${statusTarget.name}?`
			: undefined}
		confirmLabel={statusTarget?.active ? 'Desactivar' : 'Activar'}
		variant={statusTarget?.active ? 'destructive' : 'default'}
		onConfirm={confirmStatusChange}
		onCancel={closeStatusDialog}
	/>
</div>
