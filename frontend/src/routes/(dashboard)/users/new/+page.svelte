<script lang="ts">
	import { goto } from '$app/navigation';
	import { resolve } from '$app/paths';
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
	import { toCreateUserDto } from '$lib/mapper/user.mapper';
	import type { UserFormValues } from '$lib/models/user';
	import { departmentService, userService } from '$lib/services';

	const departmentsQuery = createQuery(() => ({
		queryKey: ['departments'],
		queryFn: () => departmentService.getOptions(),
	}));

	const departments = $derived(departmentsQuery.data ?? []);

	const queryClient = useQueryClient();

	const createUserMutation = createMutation(() => ({
		mutationFn: (values: UserFormValues) =>
			userService.create(toCreateUserDto(values)),
		onSuccess: () => {
			void queryClient.invalidateQueries({ queryKey: ['users'] });
			toast.success(
				'Usuario creado correctamente',
				'El usuario podrá iniciar sesión con la contraseña temporal generada.',
			);
			void goto(resolve(ROUTES.admin.users));
		},
	}));

	const serverError = $derived(
		createUserMutation.isError
			? getApiErrorMessage(createUserMutation.error)
			: null,
	);

	async function handleSubmit(values: UserFormValues) {
		await createUserMutation.mutateAsync(values);
	}

	function handleCancel() {
		void goto(resolve(ROUTES.admin.users));
	}
</script>

<div class="space-y-6">
	<PageHeader
		title="Nuevo usuario"
		description="Crea una cuenta de acceso para un miembro de la organización."
	/>

	{#if departmentsQuery.isPending}
		<div class="border-border bg-background rounded-md border p-6">
			<LoadingState label="Cargando departamentos…" />
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
	{:else}
		<div class="border-border bg-background rounded-md border p-6">
			<UserForm
				mode="create"
				{departments}
				saving={createUserMutation.isPending}
				error={serverError}
				onsubmit={handleSubmit}
				onCancel={handleCancel}
			/>
		</div>
	{/if}
</div>
