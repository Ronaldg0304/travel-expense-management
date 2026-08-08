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
	import { AccountForm } from '$lib/components/accounts';
	import { ROUTES } from '$lib/constants/routes';
	import {
		toAccount,
		toUpdateAccountDto,
	} from '$lib/mapper/account.mapper';
	import type { AccountFormValues } from '$lib/models/account';
	import { accountService } from '$lib/services';

	const accountId = Number(page.params.id);

	const accountQuery = createQuery(() => ({
		queryKey: ['account', accountId],
		queryFn: () => accountService.getById(accountId),
	}));

	const account = $derived(
		accountQuery.data ? toAccount(accountQuery.data) : undefined,
	);

	const initialValues = $derived(
		account
			? {
					userId: account.userId,
					accountType: account.accountType,
					bankName: account.bankName,
					accountNumber: account.accountNumber,
					accountHolderName: account.accountHolderName,
					active: account.active,
				}
			: undefined,
	);

	const queryClient = useQueryClient();

	const updateMutation = createMutation(() => ({
		mutationFn: (values: AccountFormValues) =>
			accountService.update(accountId, toUpdateAccountDto(values)),
		onSuccess: () => {
			void queryClient.invalidateQueries({ queryKey: ['accounts'] });
			void queryClient.invalidateQueries({
				queryKey: ['account', accountId],
			});
			toast.success('Cuenta actualizada correctamente');
			void goto(resolve(ROUTES.admin.accounts));
		},
	}));

	const serverError = $derived(
		updateMutation.isError ? getApiErrorMessage(updateMutation.error) : null,
	);

	async function handleSubmit(values: AccountFormValues) {
		await updateMutation.mutateAsync(values);
	}

	function handleCancel() {
		void goto(resolve(ROUTES.admin.accounts));
	}
</script>

<div class="space-y-6">
	<PageHeader
		title="Editar cuenta"
		description={account
			? `${account.bankName} · ${account.accountNumber}`
			: 'Edita los datos de la cuenta.'}
	/>

	{#if accountQuery.isPending}
		<div class="border-border bg-background rounded-md border p-6">
			<LoadingState label="Cargando información…" />
		</div>
	{:else if accountQuery.error}
		<div class="border-border bg-background rounded-md border p-6">
			<ApiError
				error={accountQuery.error}
				title="No se pudo cargar la cuenta"
				onRetry={() => {
					void accountQuery.refetch();
				}}
			/>
		</div>
	{:else if account && initialValues}
		<div class="border-border bg-background rounded-md border p-6">
			<AccountForm
				mode="edit"
				initial={initialValues}
				saving={updateMutation.isPending}
				error={serverError}
				onsubmit={handleSubmit}
				onCancel={handleCancel}
			/>
		</div>
	{/if}
</div>
