<script lang="ts">
	import { goto } from '$app/navigation';
	import { resolve } from '$app/paths';
	import { page } from '$app/state';
	import {
		createMutation,
		createQuery,
		useQueryClient,
	} from '@tanstack/svelte-query';
	import { ArrowLeft, Check, LoaderCircle, X } from '@lucide/svelte';
	import { getApiErrorMessage } from '$lib/api/api-error';
	import { PageHeader } from '$lib/components/common';
	import { ApiError, LoadingState, toast } from '$lib/components/feedback';
	import { TravelRequestDetail } from '$lib/components/travel-requests';
	import { Button } from '$lib/components/ui/button';
	import * as Dialog from '$lib/components/ui/dialog';
	import { Input } from '$lib/components/ui/input';
	import { ROUTES } from '$lib/constants/routes';
	import { toTravelRequest } from '$lib/mapper/travel-request.mapper';
	import { travelApprovalService, travelRequestService } from '$lib/services';

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

	const canDecide = $derived(travelRequest?.status === 'ENVIADA');

	const queryClient = useQueryClient();

	const approveMutation = createMutation(() => ({
		mutationFn: (approvedAmount: number) =>
			travelApprovalService.approve(travelRequestId, approvedAmount),
		onSuccess: () => {
			void queryClient.invalidateQueries({
				queryKey: ['travel-request', travelRequestId],
			});
			void queryClient.invalidateQueries({ queryKey: ['travel-requests'] });
			toast.success('Solicitud aprobada correctamente');
		},
		onError: (error) => {
			toast.error('No se pudo aprobar la solicitud', getApiErrorMessage(error));
		},
	}));

	const rejectMutation = createMutation(() => ({
		mutationFn: (comments: string) =>
			travelApprovalService.reject(travelRequestId, comments),
		onSuccess: () => {
			void queryClient.invalidateQueries({
				queryKey: ['travel-request', travelRequestId],
			});
			void queryClient.invalidateQueries({ queryKey: ['travel-requests'] });
			toast.success('Solicitud rechazada');
		},
		onError: (error) => {
			toast.error('No se pudo rechazar la solicitud', getApiErrorMessage(error));
		},
	}));

	const deciding = $derived(
		approveMutation.isPending || rejectMutation.isPending,
	);

	let approveDialogOpen = $state(false);
	let rejectDialogOpen = $state(false);
	let approvedAmount = $state('');
	let rejectComments = $state('');
	let approveError = $state('');
	let rejectError = $state('');

	function openApproveDialog() {
		approvedAmount = '';
		approveError = '';
		approveDialogOpen = true;
	}

	function openRejectDialog() {
		rejectComments = '';
		rejectError = '';
		rejectDialogOpen = true;
	}

	async function confirmApprove() {
		const amount = approvedAmount.trim();
		if (!amount) {
			approveError = 'El valor aprobado es obligatorio.';
			return;
		}
		if (!/^\d+$/.test(amount) || Number(amount) <= 0) {
			approveError = 'El valor aprobado debe ser un número entero positivo.';
			return;
		}
		approveError = '';
		await approveMutation.mutateAsync(Number(amount));
		approveDialogOpen = false;
	}

	async function confirmReject() {
		const comments = rejectComments.trim();
		if (!comments) {
			rejectError = 'El motivo del rechazo es obligatorio.';
			return;
		}
		if (comments.length > 1000) {
			rejectError = 'El motivo del rechazo no puede superar los 1000 caracteres.';
			return;
		}
		rejectError = '';
		await rejectMutation.mutateAsync(comments);
		rejectDialogOpen = false;
	}

	function handleBack() {
		void goto(resolve(ROUTES.approver.approvals));
	}
</script>

{#snippet headerActions()}
	<div class="flex items-center gap-2">
		{#if canDecide}
			<Button
				variant="outline"
				onclick={openRejectDialog}
				disabled={deciding}
			>
				<X aria-hidden="true" />
				Rechazar
			</Button>
			<Button
				onclick={openApproveDialog}
				disabled={deciding}
			>
				<Check aria-hidden="true" />
				Aprobar
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
	{:else if travelRequest}
		<TravelRequestDetail request={travelRequest} />
	{/if}

	<Dialog.Root bind:open={approveDialogOpen}>
		<Dialog.Content>
			<Dialog.Header>
				<Dialog.Title>Aprobar solicitud</Dialog.Title>
				<Dialog.Description>
					Confirma el valor aprobado para la solicitud. Esta acción no se
					puede deshacer.
				</Dialog.Description>
			</Dialog.Header>

			<div class="space-y-1.5">
				<label for="approved-amount" class="text-sm font-medium">
					Valor aprobado
				</label>
				<Input
					id="approved-amount"
					type="number"
					min="1"
					step="1"
					placeholder="Ej. 500000"
					bind:value={approvedAmount}
					oninput={() => (approveError = '')}
					aria-invalid={approveError ? 'true' : undefined}
					aria-describedby={approveError ? 'approved-amount-error' : undefined}
				/>
				{#if approveError}
					<p id="approved-amount-error" class="text-destructive text-sm">
						{approveError}
					</p>
				{/if}
			</div>

			<Dialog.Footer>
				<Button
					variant="outline"
					onclick={() => (approveDialogOpen = false)}
					disabled={approveMutation.isPending}
				>
					Cancelar
				</Button>
				<Button onclick={confirmApprove} disabled={approveMutation.isPending}>
					{#if approveMutation.isPending}
						<LoaderCircle class="size-4 animate-spin" aria-hidden="true" />
					{/if}
					Aprobar
				</Button>
			</Dialog.Footer>
		</Dialog.Content>
	</Dialog.Root>

	<Dialog.Root bind:open={rejectDialogOpen}>
		<Dialog.Content>
			<Dialog.Header>
				<Dialog.Title>Rechazar solicitud</Dialog.Title>
				<Dialog.Description>
					Indica el motivo del rechazo. Esta acción no se puede deshacer.
				</Dialog.Description>
			</Dialog.Header>

			<div class="space-y-1.5">
				<label for="reject-comments" class="text-sm font-medium">
					Motivo del rechazo
				</label>
				<textarea
					id="reject-comments"
					rows={4}
					maxlength={1000}
					placeholder="Indica el motivo del rechazo"
					bind:value={rejectComments}
					oninput={() => (rejectError = '')}
					aria-invalid={rejectError ? 'true' : undefined}
					aria-describedby={rejectError ? 'reject-comments-error' : undefined}
					class="border-input focus-visible:border-ring focus-visible:ring-ring/50 aria-invalid:border-destructive aria-invalid:ring-destructive/20 dark:bg-input/30 dark:aria-invalid:border-destructive/50 dark:aria-invalid:ring-destructive/40 placeholder:text-muted-foreground w-full min-w-0 rounded-md border bg-transparent px-2.5 py-1 text-base shadow-xs transition-[color,box-shadow] outline-none focus-visible:ring-3 disabled:cursor-not-allowed disabled:opacity-50 aria-invalid:ring-3 md:text-sm"
				/>
				{#if rejectError}
					<p id="reject-comments-error" class="text-destructive text-sm">
						{rejectError}
					</p>
				{/if}
			</div>

			<Dialog.Footer>
				<Button
					variant="outline"
					onclick={() => (rejectDialogOpen = false)}
					disabled={rejectMutation.isPending}
				>
					Cancelar
				</Button>
				<Button
					variant="destructive"
					onclick={confirmReject}
					disabled={rejectMutation.isPending}
				>
					{#if rejectMutation.isPending}
						<LoaderCircle class="size-4 animate-spin" aria-hidden="true" />
					{/if}
					Rechazar
				</Button>
			</Dialog.Footer>
		</Dialog.Content>
	</Dialog.Root>
</div>
