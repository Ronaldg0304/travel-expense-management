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
	import { AccountForm } from '$lib/components/accounts';
	import { ROUTES } from '$lib/constants/routes';
	import { toCreateAccountDto } from '$lib/mapper/account.mapper';
	import { toUserSummary } from '$lib/mapper/user.mapper';
	import type { AccountFormValues } from '$lib/models/account';
	import { accountService, userService } from '$lib/services';

	const usersQuery = createQuery(() => ({
		queryKey: ['users', { active: true }],
		queryFn: () =>
			userService.getUsers(
				{
					page: 0,
					size: 100,
					sort: [{ property: 'firstName', direction: 'asc' }],
				},
				{ active: true },
			),
	}));

	const users = $derived((usersQuery.data?.content ?? []).map(toUserSummary));

	const queryClient = useQueryClient();

	const createAccountMutation = createMutation(() => ({
		mutationFn: (values: AccountFormValues) =>
			accountService.create(toCreateAccountDto(values)),
		onSuccess: () => {
			void queryClient.invalidateQueries({ queryKey: ['accounts'] });
			toast.success('Cuenta creada correctamente');
			void goto(resolve(ROUTES.admin.accounts));
		},
	}));

	const serverError = $derived(
		createAccountMutation.isError
			? getApiErrorMessage(createAccountMutation.error)
			: null,
	);

	async function handleSubmit(values: AccountFormValues) {
		await createAccountMutation.mutateAsync(values);
	}

	function handleCancel() {
		void goto(resolve(ROUTES.admin.accounts));
	}
</script>

<div class="space-y-6">
	<PageHeader
		title="Nueva cuenta"
		description="Registra una cuenta bancaria para la organización."
	/>

	{#if usersQuery.isPending}
		<div class="border-border bg-background rounded-md border p-6">
			<LoadingState label="Cargando usuarios…" />
		</div>
	{:else if usersQuery.error}
		<div class="border-border bg-background rounded-md border p-6">
			<ApiError
				error={usersQuery.error}
				title="No se pudieron cargar los usuarios"
				onRetry={() => {
					void usersQuery.refetch();
				}}
			/>
		</div>
	{:else}
		<div class="border-border bg-background rounded-md border p-6">
			<AccountForm
				mode="create"
				{users}
				saving={createAccountMutation.isPending}
				error={serverError}
				onsubmit={handleSubmit}
				onCancel={handleCancel}
			/>
		</div>
	{/if}
</div>
