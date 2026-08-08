<script lang="ts">
	import { goto } from '$app/navigation';
	import { resolve } from '$app/paths';
	import {
		createMutation,
		createQuery,
		keepPreviousData,
		useQueryClient,
	} from '@tanstack/svelte-query';
	import { Pencil, Plus, UserCheck, UserX } from '@lucide/svelte';
	import { getApiErrorMessage } from '$lib/api/api-error';
	import { Button } from '$lib/components/ui/button';
	import { ConfirmDialog, PageHeader } from '$lib/components/common';
	import { toast } from '$lib/components/feedback';
	import { UserFilters, UserTable } from '$lib/components/users';
	import { ROUTES } from '$lib/constants/routes';
	import type { UserStatusFilter, UserSummary } from '$lib/models/user';
	import { userService } from '$lib/services';

	let search = $state('');
	let activeFilter = $state<UserStatusFilter>('all');
	let page = $state(0);
	let pageSize = $state(10);
	let debouncedSearch = $state('');

	$effect(() => {
		const timeout = window.setTimeout(() => {
			debouncedSearch = search;
		}, 300);
		return () => window.clearTimeout(timeout);
	});

	let lastFilterKey = '';
	$effect(() => {
		const key = `${activeFilter}:${debouncedSearch}`;
		if (key !== lastFilterKey) {
			lastFilterKey = key;
			page = 0;
		}
	});

	const query = createQuery(() => ({
		queryKey: [
			'users',
			{ page, pageSize, search: debouncedSearch, active: activeFilter },
		],
		queryFn: () =>
			userService.getUsers(
				{
					page,
					size: pageSize,
					sort: [{ property: 'id', direction: 'asc' }],
				},
				{
					search: debouncedSearch || undefined,
					active:
						activeFilter === 'all' ? undefined : activeFilter === 'active',
				},
			),
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
				? userService.activate(id)
				: userService.deactivate(id),
		onSuccess: (_result, variables) => {
			void queryClient.invalidateQueries({ queryKey: ['users'] });
			toast.success(
				variables.action === 'activate'
					? 'Usuario activado'
					: 'Usuario desactivado',
			);
		},
		onError: (error) => {
			toast.error('No se pudo actualizar el estado', getApiErrorMessage(error));
		},
	}));

	let statusTarget = $state<UserSummary | null>(null);

	const dialogOpen = $derived(statusTarget !== null);

	function openStatusDialog(row: UserSummary) {
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
	<Button onclick={() => void goto(resolve(ROUTES.admin.usersNew))}>
		<Plus aria-hidden="true" />
		Nuevo usuario
	</Button>
{/snippet}

{#snippet rowActions(row: UserSummary)}
	<div class="flex items-center justify-end gap-0.5">
		<Button
			href={`${ROUTES.admin.users}/${row.id}`}
			variant="ghost"
			size="icon-sm"
			aria-label={`Editar a ${row.firstName} ${row.lastName}`}
		>
			<Pencil aria-hidden="true" />
		</Button>
		{#if row.active}
			<Button
				variant="ghost"
				size="icon-sm"
				onclick={() => openStatusDialog(row)}
				aria-label={`Desactivar a ${row.firstName} ${row.lastName}`}
			>
				<UserX class="text-destructive" aria-hidden="true" />
			</Button>
		{:else}
			<Button
				variant="ghost"
				size="icon-sm"
				onclick={() => openStatusDialog(row)}
				aria-label={`Activar a ${row.firstName} ${row.lastName}`}
			>
				<UserCheck class="text-success" aria-hidden="true" />
			</Button>
		{/if}
	</div>
{/snippet}

<div class="space-y-4">
	<PageHeader
		title="Usuarios"
		description="Gestiona las cuentas de acceso y los roles del personal."
		actions={headerActions}
	/>

	<UserFilters bind:search bind:activeFilter />

	<UserTable
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
		title={statusTarget?.active ? 'Desactivar usuario' : 'Activar usuario'}
		description={statusTarget
			? `¿Seguro que quieres ${statusTarget.active ? 'desactivar' : 'activar'} a ${statusTarget.firstName} ${statusTarget.lastName}?`
			: undefined}
		confirmLabel={statusTarget?.active ? 'Desactivar' : 'Activar'}
		variant={statusTarget?.active ? 'destructive' : 'default'}
		onConfirm={confirmStatusChange}
		onCancel={closeStatusDialog}
	/>
</div>
