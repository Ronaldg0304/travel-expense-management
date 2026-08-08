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
	import { AccountTable } from '$lib/components/accounts';
	import { ROUTES } from '$lib/constants/routes';
	import type { AccountSummary } from '$lib/models/account';
	import { accountService } from '$lib/services';

	let page = $state(0);
	let pageSize = $state(10);

	const query = createQuery(() => ({
		queryKey: ['accounts', { page, pageSize }],
		queryFn: () =>
			accountService.getAccounts({
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
				? accountService.activate(id)
				: accountService.deactivate(id),
		onSuccess: (_result, variables) => {
			void queryClient.invalidateQueries({ queryKey: ['accounts'] });
			toast.success(
				variables.action === 'activate'
					? 'Cuenta activada'
					: 'Cuenta desactivada',
			);
		},
		onError: (error) => {
			toast.error('No se pudo actualizar el estado', getApiErrorMessage(error));
		},
	}));

	let statusTarget = $state<AccountSummary | null>(null);

	const dialogOpen = $derived(statusTarget !== null);

	function openStatusDialog(row: AccountSummary) {
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
	<Button onclick={() => void goto(resolve(ROUTES.admin.accountsNew))}>
		<Plus aria-hidden="true" />
		Nueva cuenta
	</Button>
{/snippet}

{#snippet rowActions(row: AccountSummary)}
	<div class="flex items-center justify-end gap-0.5">
		<Button
			href={`${ROUTES.admin.accounts}/${row.id}`}
			variant="ghost"
			size="icon-sm"
			aria-label={`Editar cuenta ${row.accountNumber}`}
		>
			<Pencil aria-hidden="true" />
		</Button>
		{#if row.active}
			<Button
				variant="ghost"
				size="icon-sm"
				onclick={() => openStatusDialog(row)}
				aria-label={`Desactivar cuenta ${row.accountNumber}`}
			>
				<PowerOff class="text-destructive" aria-hidden="true" />
			</Button>
		{:else}
			<Button
				variant="ghost"
				size="icon-sm"
				onclick={() => openStatusDialog(row)}
				aria-label={`Activar cuenta ${row.accountNumber}`}
			>
				<Power class="text-success" aria-hidden="true" />
			</Button>
		{/if}
	</div>
{/snippet}

<div class="space-y-4">
	<PageHeader
		title="Cuentas"
		description="Administra las cuentas bancarias de la organización."
		actions={headerActions}
	/>

	<AccountTable
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
			? 'Desactivar cuenta'
			: 'Activar cuenta'}
		description={statusTarget
			? `¿Seguro que quieres ${statusTarget.active ? 'desactivar' : 'activar'} la cuenta ${statusTarget.accountNumber}?`
			: undefined}
		confirmLabel={statusTarget?.active ? 'Desactivar' : 'Activar'}
		variant={statusTarget?.active ? 'destructive' : 'default'}
		onConfirm={confirmStatusChange}
		onCancel={closeStatusDialog}
	/>
</div>
