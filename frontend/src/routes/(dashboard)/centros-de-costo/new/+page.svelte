<script lang="ts">
	import { goto } from '$app/navigation';
	import { resolve } from '$app/paths';
	import { createMutation, useQueryClient } from '@tanstack/svelte-query';
	import { getApiErrorMessage } from '$lib/api/api-error';
	import { PageHeader } from '$lib/components/common';
	import { toast } from '$lib/components/feedback';
	import { CostCenterForm } from '$lib/components/cost-centers';
	import { ROUTES } from '$lib/constants/routes';
	import { toCreateCostCenterDto } from '$lib/mapper/cost-center.mapper';
	import type { CostCenterFormValues } from '$lib/models/cost-center';
	import { costCenterService } from '$lib/services';

	const queryClient = useQueryClient();

	const createCostCenterMutation = createMutation(() => ({
		mutationFn: (values: CostCenterFormValues) =>
			costCenterService.create(toCreateCostCenterDto(values)),
		onSuccess: () => {
			void queryClient.invalidateQueries({ queryKey: ['cost-centers'] });
			toast.success('Centro de costo creado correctamente');
			void goto(resolve(ROUTES.admin.costCenters));
		},
	}));

	const serverError = $derived(
		createCostCenterMutation.isError
			? getApiErrorMessage(createCostCenterMutation.error)
			: null,
	);

	async function handleSubmit(values: CostCenterFormValues) {
		await createCostCenterMutation.mutateAsync(values);
	}

	function handleCancel() {
		void goto(resolve(ROUTES.admin.costCenters));
	}
</script>

<div class="space-y-6">
	<PageHeader
		title="Nuevo centro de costo"
		description="Crea un centro de costo para asociarlo a las solicitudes de viaje."
	/>

	<div class="border-border bg-background rounded-md border p-6">
		<CostCenterForm
			mode="create"
			saving={createCostCenterMutation.isPending}
			error={serverError}
			onsubmit={handleSubmit}
			onCancel={handleCancel}
		/>
	</div>
</div>
