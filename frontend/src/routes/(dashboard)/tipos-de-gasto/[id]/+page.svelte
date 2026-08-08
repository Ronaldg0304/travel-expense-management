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
	import { ExpenseTypeForm } from '$lib/components/expense-types';
	import { ROUTES } from '$lib/constants/routes';
	import {
		toExpenseType,
		toUpdateExpenseTypeDto,
	} from '$lib/mapper/expense-type.mapper';
	import type { ExpenseTypeFormValues } from '$lib/models/expense-type';
	import { expenseTypeService } from '$lib/services';

	const expenseTypeId = Number(page.params.id);

	const expenseTypeQuery = createQuery(() => ({
		queryKey: ['expense-type', expenseTypeId],
		queryFn: () => expenseTypeService.getById(expenseTypeId),
	}));

	const expenseType = $derived(
		expenseTypeQuery.data ? toExpenseType(expenseTypeQuery.data) : undefined,
	);

	const initialValues = $derived(
		expenseType
			? {
					code: expenseType.code,
					name: expenseType.name,
					active: expenseType.active,
				}
			: undefined,
	);

	const queryClient = useQueryClient();

	const updateMutation = createMutation(() => ({
		mutationFn: (values: ExpenseTypeFormValues) =>
			expenseTypeService.update(expenseTypeId, toUpdateExpenseTypeDto(values)),
		onSuccess: () => {
			void queryClient.invalidateQueries({ queryKey: ['expense-types'] });
			void queryClient.invalidateQueries({
				queryKey: ['expense-type', expenseTypeId],
			});
			toast.success('Tipo de gasto actualizado correctamente');
			void goto(resolve(ROUTES.admin.expenseTypes));
		},
	}));

	const serverError = $derived(
		updateMutation.isError ? getApiErrorMessage(updateMutation.error) : null,
	);

	async function handleSubmit(values: ExpenseTypeFormValues) {
		await updateMutation.mutateAsync(values);
	}

	function handleCancel() {
		void goto(resolve(ROUTES.admin.expenseTypes));
	}
</script>

<div class="space-y-6">
	<PageHeader
		title="Editar tipo de gasto"
		description={expenseType
			? `${expenseType.code} · ${expenseType.name}`
			: 'Edita los datos del tipo de gasto.'}
	/>

	{#if expenseTypeQuery.isPending}
		<div class="border-border bg-background rounded-md border p-6">
			<LoadingState label="Cargando información…" />
		</div>
	{:else if expenseTypeQuery.error}
		<div class="border-border bg-background rounded-md border p-6">
			<ApiError
				error={expenseTypeQuery.error}
				title="No se pudo cargar el tipo de gasto"
				onRetry={() => {
					void expenseTypeQuery.refetch();
				}}
			/>
		</div>
	{:else if expenseType && initialValues}
		<div class="border-border bg-background rounded-md border p-6">
			<ExpenseTypeForm
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
