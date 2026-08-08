<script lang="ts">
	import { goto } from '$app/navigation';
	import { resolve } from '$app/paths';
	import { createMutation, useQueryClient } from '@tanstack/svelte-query';
	import { getApiErrorMessage } from '$lib/api/api-error';
	import { PageHeader } from '$lib/components/common';
	import { toast } from '$lib/components/feedback';
	import { TravelRequestForm } from '$lib/components/travel-requests';
	import { ROUTES } from '$lib/constants/routes';
	import { toCreateTravelRequestDto } from '$lib/mapper/travel-request.mapper';
	import type { TravelRequestFormValues } from '$lib/models/travel-request';
	import { travelRequestService } from '$lib/services';
	import { resolvePath } from '$lib/utils';

	const queryClient = useQueryClient();

	const createTravelRequestMutation = createMutation(() => ({
		mutationFn: (values: TravelRequestFormValues) =>
			travelRequestService.createDraft(toCreateTravelRequestDto(values)),
		onSuccess: (data) => {
			void queryClient.invalidateQueries({ queryKey: ['travel-requests'] });
			toast.success('Solicitud de viaje creada correctamente');
			void goto(resolvePath(`${ROUTES.admin.travelRequests}/${data.id}`));
		},
	}));

	const serverError = $derived(
		createTravelRequestMutation.isError
			? getApiErrorMessage(createTravelRequestMutation.error)
			: null,
	);

	async function handleSubmit(values: TravelRequestFormValues) {
		await createTravelRequestMutation.mutateAsync(values);
	}

	function handleCancel() {
		void goto(resolve(ROUTES.admin.travelRequests));
	}
</script>

<div class="space-y-6">
	<PageHeader
		title="Nueva solicitud de viaje"
		description="Registra una nueva solicitud de viaje para solicitar un anticipo."
	/>

	<div class="border-border bg-background rounded-md border p-6">
		<TravelRequestForm
			saving={createTravelRequestMutation.isPending}
			error={serverError}
			onsubmit={handleSubmit}
			onCancel={handleCancel}
		/>
	</div>
</div>
