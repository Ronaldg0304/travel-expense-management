<script lang="ts">
	import { goto } from '$app/navigation';
	import { resolve } from '$app/paths';
	import { page } from '$app/state';
	import {
		createMutation,
		createQuery,
		useQueryClient,
	} from '@tanstack/svelte-query';
	import { ArrowLeft, Wallet } from '@lucide/svelte';
	import { getApiErrorMessage } from '$lib/api/api-error';
	import { ConfirmDialog, PageHeader } from '$lib/components/common';
	import { ApiError, LoadingState, toast } from '$lib/components/feedback';
	import { TravelRequestDetail } from '$lib/components/travel-requests';
	import { Button } from '$lib/components/ui/button';
	import { Input } from '$lib/components/ui/input';
	import { ROUTES } from '$lib/constants/routes';
	import type { RegisterDisbursementDto } from '$lib/dto/disbursement';
	import { toTravelRequest } from '$lib/mapper/travel-request.mapper';
	import { disbursementService, travelRequestService } from '$lib/services';

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

	const canDisburse = $derived(travelRequest?.status === 'APROBADA');

	const queryClient = useQueryClient();

	const disbursementMutation = createMutation(() => ({
		mutationFn: (payload: RegisterDisbursementDto) =>
			disbursementService.registerDisbursement(travelRequestId, payload),
		onSuccess: () => {
			void queryClient.invalidateQueries({
				queryKey: ['travel-request', travelRequestId],
			});
			void queryClient.invalidateQueries({ queryKey: ['travel-requests'] });
			resetForm();
			toast.success('Desembolso registrado correctamente');
		},
		onError: (error) => {
			toast.error(
				'No se pudo registrar el desembolso',
				getApiErrorMessage(error),
			);
		},
	}));

	let disbursedAmount = $state('');
	let adjustmentJustification = $state('');
	let formError = $state('');
	let confirmDialogOpen = $state(false);

	function resetForm() {
		disbursedAmount = '';
		adjustmentJustification = '';
		formError = '';
	}

	function validateForm(): boolean {
		const amount = disbursedAmount.trim();
		if (!amount) {
			formError = 'El valor desembolsado es obligatorio.';
			return false;
		}
		if (!/^\d+$/.test(amount) || Number(amount) <= 0) {
			formError = 'El valor desembolsado debe ser un número entero positivo.';
			return false;
		}
		formError = '';
		return true;
	}

	function openConfirmDialog() {
		if (!validateForm()) return;
		confirmDialogOpen = true;
	}

	async function confirmRegister() {
		const justification = adjustmentJustification.trim();
		const payload: RegisterDisbursementDto = {
			disbursedAmount: Number(disbursedAmount.trim()),
			adjustmentJustification: justification.length > 0 ? justification : null,
		};
		await disbursementMutation.mutateAsync(payload);
	}

	function handleBack() {
		void goto(resolve(ROUTES.finance.disbursements));
	}
</script>

{#snippet headerActions()}
	<div class="flex items-center gap-2">
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

		{#if canDisburse}
			<div class="border-border bg-background rounded-md border p-6">
				<div class="space-y-1">
					<h2 class="text-base font-semibold">Registrar desembolso</h2>
					<p class="text-muted-foreground text-sm">
						Registra el anticipo desembolsado para esta solicitud. Esta
						acción no se puede deshacer.
					</p>
				</div>

				<form
					onsubmit={(event) => {
						event.preventDefault();
						openConfirmDialog();
					}}
					novalidate
					class="mt-5 space-y-5"
				>
					{#if formError}
						<div
							role="alert"
							class="bg-destructive/10 text-destructive rounded-md px-3 py-2 text-sm"
						>
							{formError}
						</div>
					{/if}

					<div class="grid gap-4 sm:grid-cols-2">
						<div class="space-y-1.5">
							<label for="disbursed-amount" class="text-sm font-medium">
								Valor desembolsado
							</label>
							<Input
								id="disbursed-amount"
								type="number"
								min="1"
								step="1"
								placeholder="Ej. 500000"
								bind:value={disbursedAmount}
								oninput={() => (formError = '')}
								aria-invalid={formError ? 'true' : undefined}
							/>
							<p class="text-muted-foreground text-xs">
								Monto en pesos colombianos (sin decimales).
							</p>
						</div>

						<div class="space-y-1.5">
							<label for="adjustment-justification" class="text-sm font-medium">
								Justificación del ajuste
							</label>
							<textarea
								id="adjustment-justification"
								rows={4}
								maxlength={1000}
								placeholder="Opcional. Justifica la diferencia con el valor aprobado."
								bind:value={adjustmentJustification}
								class="border-input focus-visible:border-ring focus-visible:ring-ring/50 dark:bg-input/30 placeholder:text-muted-foreground w-full min-w-0 rounded-md border bg-transparent px-2.5 py-1 text-base shadow-xs transition-[color,box-shadow] outline-none focus-visible:ring-3 disabled:cursor-not-allowed disabled:opacity-50 md:text-sm"
							/>
							<p class="text-muted-foreground text-xs">
								Obligatoria si el valor desembolsado difiere del valor
								aprobado.
							</p>
						</div>
					</div>

					<div class="flex justify-end">
						<Button
							type="submit"
							disabled={disbursementMutation.isPending}
						>
							<Wallet aria-hidden="true" />
							Registrar desembolso
						</Button>
					</div>
				</form>
			</div>
		{/if}
	{/if}

	<ConfirmDialog
		bind:open={confirmDialogOpen}
		title="Registrar desembolso"
		description={`Se registrará un desembolso por ${Number(disbursedAmount).toLocaleString(
			'es-CO',
		)} COP para la solicitud ${travelRequest?.requestNumber ?? ''}. Esta acción no se puede deshacer.`}
		confirmLabel="Registrar"
		cancelLabel="Cancelar"
		variant="default"
		onConfirm={confirmRegister}
		onCancel={() => (confirmDialogOpen = false)}
	/>
</div>
