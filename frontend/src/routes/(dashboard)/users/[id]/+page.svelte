<script lang="ts">
	import { goto } from '$app/navigation';
	import { resolve } from '$app/paths';
	import { page } from '$app/state';
	import {
		createMutation,
		createQuery,
		useQueryClient,
	} from '@tanstack/svelte-query';
	import { getApiErrorMessage } from '$lib/api/api-error';
	import { PageHeader } from '$lib/components/common';
	import { ApiError, LoadingState, toast } from '$lib/components/feedback';
	import { UserForm } from '$lib/components/users';
	import { ROUTES } from '$lib/constants/routes';
	import { toUpdateUserDto, toUser } from '$lib/mapper/user.mapper';
	import type { UserFormValues } from '$lib/models/user';
	import { departmentService, userService } from '$lib/services';

	const userId = Number(page.params.id);

	const departmentsQuery = createQuery(() => ({
		queryKey: ['departments'],
		queryFn: () => departmentService.getOptions(),
	}));

	const departments = $derived(departmentsQuery.data ?? []);

	const userQuery = createQuery(() => ({
		queryKey: ['user', userId],
		queryFn: () => userService.getById(userId),
	}));

	const user = $derived(userQuery.data ? toUser(userQuery.data) : undefined);

	const initialValues = $derived(
		user
			? {
					firstName: user.firstName,
					lastName: user.lastName,
					email: user.email,
					documentType: user.documentType,
					documentNumber: user.documentNumber,
					phone: user.phone ?? '',
					position: user.position ?? '',
					departmentId: user.departmentId,
					role: user.role,
					active: user.active,
				}
			: undefined,
	);

	const queryClient = useQueryClient();

	const updateMutation = createMutation(() => ({
		mutationFn: (values: UserFormValues) =>
			userService.update(userId, toUpdateUserDto(values)),
		onSuccess: () => {
			void queryClient.invalidateQueries({ queryKey: ['users'] });
			void queryClient.invalidateQueries({ queryKey: ['user', userId] });
			toast.success('Usuario actualizado correctamente');
			void goto(resolve(ROUTES.admin.users));
		},
	}));

	const serverError = $derived(
		updateMutation.isError ? getApiErrorMessage(updateMutation.error) : null,
	);

	async function handleSubmit(values: UserFormValues) {
		await updateMutation.mutateAsync(values);
	}

	function handleCancel() {
		void goto(resolve(ROUTES.admin.users));
	}
</script>

<div class="space-y-6">
	<PageHeader
		title="Editar usuario"
		description={user
			? `${user.firstName} ${user.lastName} · ${user.email}`
			: 'Edita los datos del usuario.'}
	/>

	{#if userQuery.isPending || departmentsQuery.isPending}
		<div class="border-border bg-background rounded-md border p-6">
			<LoadingState label="Cargando información…" />
		</div>
	{:else if userQuery.error}
		<div class="border-border bg-background rounded-md border p-6">
			<ApiError
				error={userQuery.error}
				title="No se pudo cargar el usuario"
				onRetry={() => {
					void userQuery.refetch();
				}}
			/>
		</div>
	{:else if departmentsQuery.error}
		<div class="border-border bg-background rounded-md border p-6">
			<ApiError
				error={departmentsQuery.error}
				title="No se pudieron cargar los departamentos"
				onRetry={() => {
					void departmentsQuery.refetch();
				}}
			/>
		</div>
	{:else if user && initialValues}
		<div class="border-border bg-background rounded-md border p-6">
			<UserForm
				mode="edit"
				initial={initialValues}
				{departments}
				saving={updateMutation.isPending}
				error={serverError}
				onsubmit={handleSubmit}
				onCancel={handleCancel}
			/>
		</div>
	{/if}
</div>
