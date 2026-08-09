<script lang="ts">
	import { goto } from '$app/navigation';
	import { resolve } from '$app/paths';
	import { page } from '$app/state';
	import {
		createMutation,
		createQuery,
		useQueryClient,
	} from '@tanstack/svelte-query';
	import { ArrowLeft, Send } from '@lucide/svelte';
	import { getApiErrorMessage } from '$lib/api/api-error';
	import { Button } from '$lib/components/ui/button';
	import { ConfirmDialog, PageHeader } from '$lib/components/common';
	import { ApiError, LoadingState, toast } from '$lib/components/feedback';
	import { TravelRequestDetail } from '$lib/components/travel-requests';
	import { ROUTES } from '$lib/constants/routes';
	import { toTravelRequest } from '$lib/mapper/travel-request.mapper';
	import { travelRequestService } from '$lib/services';
	import { user } from '$lib/stores';

	const travelRequestId = Number(page.params.id);

	const travelRequestQuery = createQuery(() => ({
		queryKey: ['travel-request', travelRequestId],
		queryFn: () => travelRequestService.getById(travelRequestId),
	}));

	const travelRequest = $derived(
		travelRequestQuery.data
			? toTravelRequest(travelRequestQuery.data)
			: undefined,
	);

	const isOwnRequest = $derived(
		travelRequest !== undefined &&
			travelRequest.applicantId === $user?.id,
	);

	const canSubmit = $derived(
		travelRequest?.status === 'BORRADOR' && isOwnRequest,
	);

	const queryClient = useQueryClient();

	const submitMutation = createMutation(() => ({
		mutationFn: (id: number) => travelRequestService.submitForApproval(id),
		onSuccess: () => {
			void queryClient.invalidateQueries({
				queryKey: ['travel-request', travelRequestId],
			});
			void queryClient.invalidateQueries({
				queryKey: ['travel-requests', 'my'],
			});
			toast.success('Solicitud enviada para aprobación');
		},
		onError: (error) => {
			toast.error('No se pudo enviar la solicitud', getApiErrorMessage(error));
		},
	}));

	let submitDialogOpen = $state(false);

	async function confirmSubmit() {
		await submitMutation.mutateAsync(travelRequestId);
	}

	function handleBack() {
		void goto(resolve(ROUTES.employee.myRequests));
	}
</script>

{#snippet headerActions()}
	<div class="flex items-center gap-2">
		{#if canSubmit}
			<Button
				onclick={() => (submitDialogOpen = true)}
				disabled={submitMutation.isPending}
			>
				<Send aria-hidden="true" />
				Enviar
			</Button>
		{/if}
		<Button variant="outline" onclick={handleBack}>
			<ArrowLeft aria-hidden="true" />
			Volver
		</Button>
	</div>
{/snippet}

<div class="space-y-6">
	<PageHeader
		title="Detalle de solicitud"
		description={travelRequest
			? `${travelRequest.requestNumber} · ${travelRequest.applicantFullName}`
			: 'Consulta la información de la solicitud de viaje.'}
		actions={headerActions}
	/>

	{#if travelRequestQuery.isPending}
		<LoadingState label="Cargando solicitud…" />
	{:else if travelRequestQuery.error}
		<ApiError
			error={travelRequestQuery.error}
			title="No se pudo cargar la solicitud"
			onRetry={() => {
				void travelRequestQuery.refetch();
			}}
		/>
	{:else if travelRequest && !isOwnRequest}
		<ApiError
			title="Solicitud no disponible"
			message="Solo puedes consultar las solicitudes que has creado."
		/>
	{:else if travelRequest}
		<TravelRequestDetail request={travelRequest} />
	{/if}

	<ConfirmDialog
		bind:open={submitDialogOpen}
		title="Enviar solicitud para aprobación"
		description="La solicitud dejará de ser un borrador y pasará a estado Enviada. Esta acción no se puede deshacer."
		confirmLabel="Enviar"
		cancelLabel="Cancelar"
		variant="default"
		onConfirm={confirmSubmit}
		onCancel={() => (submitDialogOpen = false)}
	/>
</div>
