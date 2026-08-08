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
	import { CostCenterForm } from '$lib/components/cost-centers';
	import { ROUTES } from '$lib/constants/routes';
	import {
		toCostCenter,
		toUpdateCostCenterDto,
	} from '$lib/mapper/cost-center.mapper';
	import type { CostCenterFormValues } from '$lib/models/cost-center';
	import { costCenterService } from '$lib/services';

	const costCenterId = Number(page.params.id);

	const costCenterQuery = createQuery(() => ({
		queryKey: ['cost-center', costCenterId],
		queryFn: () => costCenterService.getById(costCenterId),
	}));

	const costCenter = $derived(
		costCenterQuery.data ? toCostCenter(costCenterQuery.data) : undefined,
	);

	const initialValues = $derived(
		costCenter
			? {
					code: costCenter.code,
					name: costCenter.name,
					type: costCenter.type,
					active: costCenter.active,
				}
			: undefined,
	);

	const queryClient = useQueryClient();

	const updateMutation = createMutation(() => ({
		mutationFn: (values: CostCenterFormValues) =>
			costCenterService.update(costCenterId, toUpdateCostCenterDto(values)),
		onSuccess: () => {
			void queryClient.invalidateQueries({ queryKey: ['cost-centers'] });
			void queryClient.invalidateQueries({
				queryKey: ['cost-center', costCenterId],
			});
			toast.success('Centro de costo actualizado correctamente');
			void goto(resolve(ROUTES.admin.costCenters));
		},
	}));

	const serverError = $derived(
		updateMutation.isError ? getApiErrorMessage(updateMutation.error) : null,
	);

	async function handleSubmit(values: CostCenterFormValues) {
		await updateMutation.mutateAsync(values);
	}

	function handleCancel() {
		void goto(resolve(ROUTES.admin.costCenters));
	}
</script>

<div class="space-y-6">
	<PageHeader
		title="Editar centro de costo"
		description={costCenter
			? `${costCenter.code} · ${costCenter.name}`
			: 'Edita los datos del centro de costo.'}
	/>

	{#if costCenterQuery.isPending}
		<div class="border-border bg-background rounded-md border p-6">
			<LoadingState label="Cargando información…" />
		</div>
	{:else if costCenterQuery.error}
		<div class="border-border bg-background rounded-md border p-6">
			<ApiError
				error={costCenterQuery.error}
				title="No se pudo cargar el centro de costo"
				onRetry={() => {
					void costCenterQuery.refetch();
				}}
			/>
		</div>
	{:else if costCenter && initialValues}
		<div class="border-border bg-background rounded-md border p-6">
			<CostCenterForm
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
