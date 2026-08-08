<script lang="ts">
	import { goto } from '$app/navigation';
	import { resolve } from '$app/paths';
	import { createMutation, useQueryClient } from '@tanstack/svelte-query';
	import { getApiErrorMessage } from '$lib/api/api-error';
	import { PageHeader } from '$lib/components/common';
	import { toast } from '$lib/components/feedback';
	import { ExpenseTypeForm } from '$lib/components/expense-types';
	import { ROUTES } from '$lib/constants/routes';
	import { toCreateExpenseTypeDto } from '$lib/mapper/expense-type.mapper';
	import type { ExpenseTypeFormValues } from '$lib/models/expense-type';
	import { expenseTypeService } from '$lib/services';

	const queryClient = useQueryClient();

	const createExpenseTypeMutation = createMutation(() => ({
		mutationFn: (values: ExpenseTypeFormValues) =>
			expenseTypeService.create(toCreateExpenseTypeDto(values)),
		onSuccess: () => {
			void queryClient.invalidateQueries({ queryKey: ['expense-types'] });
			toast.success('Tipo de gasto creado correctamente');
			void goto(resolve(ROUTES.admin.expenseTypes));
		},
	}));

	const serverError = $derived(
		createExpenseTypeMutation.isError
			? getApiErrorMessage(createExpenseTypeMutation.error)
			: null,
	);

	async function handleSubmit(values: ExpenseTypeFormValues) {
		await createExpenseTypeMutation.mutateAsync(values);
	}

	function handleCancel() {
		void goto(resolve(ROUTES.admin.expenseTypes));
	}
</script>

<div class="space-y-6">
	<PageHeader
		title="Nuevo tipo de gasto"
		description="Crea un tipo de gasto para asociarlo a las legalizaciones."
	/>

	<div class="border-border bg-background rounded-md border p-6">
		<ExpenseTypeForm
			mode="create"
			saving={createExpenseTypeMutation.isPending}
			error={serverError}
			onsubmit={handleSubmit}
			onCancel={handleCancel}
		/>
	</div>
</div>
