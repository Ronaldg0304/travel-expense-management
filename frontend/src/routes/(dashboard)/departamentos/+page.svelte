<script lang="ts">
	import {
		createMutation,
		createQuery,
		keepPreviousData,
		useQueryClient,
	} from '@tanstack/svelte-query';
	import { Pencil, Plus, Power, PowerOff, Users } from '@lucide/svelte';
	import { getApiErrorMessage } from '$lib/api/api-error';
	import { ConfirmDialog, PageHeader } from '$lib/components/common';
	import { toast } from '$lib/components/feedback';
	import {
		DepartmentApproverDialog,
		DepartmentForm,
		DepartmentTable,
	} from '$lib/components/departments';
	import { Button } from '$lib/components/ui/button';
	import * as Dialog from '$lib/components/ui/dialog';
	import {
		toCreateDepartmentApproverDto,
		toCreateDepartmentDto,
		toDepartmentFormValues,
		toUpdateDepartmentDto,
	} from '$lib/mapper/department.mapper';
	import type {
		DepartmentApproverSummary,
		DepartmentFormValues,
		DepartmentSummary,
	} from '$lib/models/department';
	import type { UserSummary } from '$lib/models/user';
	import { departmentApproverService, departmentService, userService } from '$lib/services';

	let page = $state(0);
	let pageSize = $state(10);

	const query = createQuery(() => ({
		queryKey: ['departments', { page, pageSize }],
		queryFn: () =>
			departmentService.getDepartments({
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

	const approverCountsQuery = createQuery(() => ({
		queryKey: ['department-approvers', 'all'],
		queryFn: () => departmentApproverService.getApprovers(),
	}));

	const approverCounts = $derived.by(() => {
		const counts: Record<string, number> = {};
		for (const approver of approverCountsQuery.data ?? []) {
			counts[approver.departmentName] =
				(counts[approver.departmentName] ?? 0) + 1;
		}
		return counts;
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
				? departmentService.activate(id)
				: departmentService.deactivate(id),
		onSuccess: (_result, variables) => {
			void queryClient.invalidateQueries({ queryKey: ['departments'] });
			toast.success(
				variables.action === 'activate'
					? 'Departamento activado'
					: 'Departamento desactivado',
			);
		},
		onError: (error) => {
			toast.error('No se pudo actualizar el estado', getApiErrorMessage(error));
		},
	}));

	let statusTarget = $state<DepartmentSummary | null>(null);

	const statusDialogOpen = $derived(statusTarget !== null);

	function openStatusDialog(row: DepartmentSummary) {
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

	let createOpen = $state(false);

	const createMutation = createMutation(() => ({
		mutationFn: (values: DepartmentFormValues) =>
			departmentService.create(toCreateDepartmentDto(values)),
		onSuccess: () => {
			void queryClient.invalidateQueries({ queryKey: ['departments'] });
			toast.success('Departamento creado correctamente');
			createOpen = false;
		},
	}));

	const createServerError = $derived(
		createMutation.isError ? getApiErrorMessage(createMutation.error) : null,
	);

	async function handleCreate(values: DepartmentFormValues) {
		await createMutation.mutateAsync(values);
	}

	let editTarget = $state<DepartmentSummary | null>(null);

	const editOpen = $derived(editTarget !== null);

	const editMutation = createMutation(() => ({
		mutationFn: (values: DepartmentFormValues) =>
			departmentService.update(editTarget!.id, toUpdateDepartmentDto(values)),
		onSuccess: () => {
			void queryClient.invalidateQueries({ queryKey: ['departments'] });
			toast.success('Departamento actualizado correctamente');
			editTarget = null;
		},
	}));

	const editServerError = $derived(
		editMutation.isError ? getApiErrorMessage(editMutation.error) : null,
	);

	async function handleEdit(values: DepartmentFormValues) {
		await editMutation.mutateAsync(values);
	}

	let approverOpen = $state(false);
	let approverTarget = $state<DepartmentSummary | null>(null);

	const approversQuery = createQuery(() => ({
		queryKey: ['department-approvers', { departmentId: approverTarget?.id }],
		queryFn: () => departmentApproverService.findByDepartment(approverTarget!.id),
		enabled: approverTarget !== null,
	}));

	const approvers = $derived(approversQuery.data ?? []);

	const activeUsersQuery = createQuery(() => ({
		queryKey: ['users', { active: true }],
		queryFn: () =>
			userService.getUsers({ page: 0, size: 500 }, { active: true }),
		enabled: approverTarget !== null,
	}));

	const activeUsers = $derived(activeUsersQuery.data?.content ?? []);

	function openApprovers(row: DepartmentSummary) {
		approverTarget = row;
		approverOpen = true;
	}

	function closeApprovers() {
		approverTarget = null;
	}

	const assignMutation = createMutation(() => ({
		mutationFn: ({ userId, active }: { userId: number; active: boolean }) =>
			departmentApproverService.create(
				toCreateDepartmentApproverDto(approverTarget!.id, userId, active),
			),
		onSuccess: () => {
			void queryClient.invalidateQueries({ queryKey: ['department-approvers'] });
			toast.success('Aprobador asignado correctamente');
		},
		onError: (error) => {
			toast.error('No se pudo asignar el aprobador', getApiErrorMessage(error));
		},
	}));

	async function handleAssign(input: { userId: number; active: boolean }) {
		await assignMutation.mutateAsync(input);
	}

	const toggleApproverMutation = createMutation(() => ({
		mutationFn: (approver: DepartmentApproverSummary) =>
			approver.active
				? departmentApproverService.deactivate(approver.id)
				: departmentApproverService.activate(approver.id),
		onSuccess: (_result, variables) => {
			void queryClient.invalidateQueries({
				queryKey: ['department-approvers'],
			});
			toast.success(
				variables.active ? 'Aprobador desactivado' : 'Aprobador activado',
			);
		},
		onError: (error) => {
			toast.error(
				'No se pudo actualizar el estado del aprobador',
				getApiErrorMessage(error),
			);
		},
	}));

	async function handleToggleApprover(approver: DepartmentApproverSummary) {
		await toggleApproverMutation.mutateAsync(approver);
	}

	function handleRetry() {
		void query.refetch();
	}
</script>

{#snippet headerActions()}
	<Button onclick={() => (createOpen = true)}>
		<Plus aria-hidden="true" />
		Nuevo departamento
	</Button>
{/snippet}

{#snippet rowActions(row: DepartmentSummary)}
	<div class="flex items-center justify-end gap-0.5">
		<Button
			variant="ghost"
			size="icon-sm"
			onclick={() => (editTarget = row)}
			aria-label={`Editar ${row.name}`}
		>
			<Pencil aria-hidden="true" />
		</Button>
		<Button
			variant="ghost"
			size="icon-sm"
			onclick={() => openApprovers(row)}
			aria-label={`Ver aprobadores de ${row.name}`}
		>
			<Users aria-hidden="true" />
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
		title="Departamentos"
		description="Administra los departamentos y sus aprobadores."
		actions={headerActions}
	/>

	<DepartmentTable
		{rows}
		loading={query.isPending}
		error={query.error}
		bind:page
		bind:pageSize
		{totalPages}
		{totalElements}
		{approverCounts}
		onRetry={handleRetry}
		{rowActions}
	/>

	<ConfirmDialog
		open={statusDialogOpen}
		title={statusTarget?.active
			? 'Desactivar departamento'
			: 'Activar departamento'}
		description={statusTarget
			? `¿Seguro que quieres ${statusTarget.active ? 'desactivar' : 'activar'} el departamento ${statusTarget.name}?`
			: undefined}
		confirmLabel={statusTarget?.active ? 'Desactivar' : 'Activar'}
		variant={statusTarget?.active ? 'destructive' : 'default'}
		onConfirm={confirmStatusChange}
		onCancel={closeStatusDialog}
	/>

	<Dialog.Root bind:open={createOpen}>
		<Dialog.Content class="sm:max-w-lg">
			<Dialog.Header>
				<Dialog.Title>Nuevo departamento</Dialog.Title>
				<Dialog.Description>
					Crea un departamento para organizar a los usuarios y sus
					aprobadores.
				</Dialog.Description>
			</Dialog.Header>
			<DepartmentForm
				mode="create"
				saving={createMutation.isPending}
				error={createServerError}
				onsubmit={handleCreate}
				onCancel={() => (createOpen = false)}
			/>
		</Dialog.Content>
	</Dialog.Root>

	<Dialog.Root
		open={editOpen}
		onOpenChange={(isOpen) => {
			if (!isOpen) editTarget = null;
		}}
	>
		<Dialog.Content class="sm:max-w-lg">
			<Dialog.Header>
				<Dialog.Title>Editar departamento</Dialog.Title>
				<Dialog.Description>
					{editTarget
						? `Actualiza los datos de ${editTarget.name}.`
						: 'Actualiza los datos del departamento.'}
				</Dialog.Description>
			</Dialog.Header>
			{#if editTarget}
				<DepartmentForm
					mode="edit"
					initial={toDepartmentFormValues(editTarget)}
					saving={editMutation.isPending}
					error={editServerError}
					onsubmit={handleEdit}
					onCancel={() => (editTarget = null)}
				/>
			{/if}
		</Dialog.Content>
	</Dialog.Root>

	<DepartmentApproverDialog
		bind:open={approverOpen}
		departmentName={approverTarget?.name ?? ''}
		{approvers}
		loading={approversQuery.isPending}
		error={approversQuery.error}
		{activeUsers}
		assigning={assignMutation.isPending}
		toggling={toggleApproverMutation.isPending}
		onAssign={handleAssign}
		onToggle={handleToggleApprover}
		onRetry={() => {
			void approversQuery.refetch();
		}}
		onClose={closeApprovers}
	/>
</div>
