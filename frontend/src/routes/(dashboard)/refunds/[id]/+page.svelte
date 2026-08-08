<script lang="ts">
	import { goto } from '$app/navigation';
	import { resolve } from '$app/paths';
	import { page } from '$app/state';
	import {
		createMutation,
		createQuery,
		useQueryClient,
	} from '@tanstack/svelte-query';
	import { ArrowLeft, LoaderCircle, Undo2 } from '@lucide/svelte';
	import { getApiErrorMessage, normalizeApiError } from '$lib/api/api-error';
	import { hasAnyRole } from '$lib/auth/permission';
	import { ConfirmDialog, PageHeader } from '$lib/components/common';
	import { ApiError, LoadingState, toast } from '$lib/components/feedback';
	import { LegalizationDetail } from '$lib/components/legalizations';
	import { TravelRequestDetail } from '$lib/components/travel-requests';
	import { Button } from '$lib/components/ui/button';
	import { Input } from '$lib/components/ui/input';
	import { ROUTES } from '$lib/constants/routes';
	import type { RegisterRefundDto } from '$lib/dto/refund';
	import { toLegalization } from '$lib/mapper/legalization.mapper';
	import { toSettlementAnalysis } from '$lib/mapper/settlement.mapper';
	import { toTravelRequest } from '$lib/mapper/travel-request.mapper';
	import {
		legalizationService,
		refundService,
		travelRequestService,
	} from '$lib/services';
	import { user } from '$lib/stores/user.store';
	import { formatCurrency } from '$lib/utils';

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

	const legalizationQuery = createQuery(() => ({
		queryKey: ['legalization', 'by-travel-request', travelRequestId],
		queryFn: () => legalizationService.findByTravelRequest(travelRequestId),
		retry: false,
	}));

	const legalization = $derived(
		legalizationQuery.data
			? toLegalization(legalizationQuery.data)
			: undefined,
	);

	const legalizationNotFound = $derived(
		legalizationQuery.error
			? normalizeApiError(legalizationQuery.error).status === 404
			: false,
	);

	const canRegisterRefund = $derived(
		hasAnyRole($user, ['FINANCIERA', 'ADMINISTRADOR']) &&
			legalization?.status === 'PENDIENTE_DEVOLUCION',
	);

	const settlementAnalysisQuery = createQuery(() => ({
		queryKey: ['legalization', 'settlement-analysis', legalization?.id],
		queryFn: () => {
			if (legalization?.id == null) {
				throw new Error('No hay una legalización cargada');
			}
			return legalizationService.getSettlementAnalysis(legalization.id);
		},
		enabled: canRegisterRefund,
	}));

	const analysis = $derived(
		settlementAnalysisQuery.data
			? toSettlementAnalysis(settlementAnalysisQuery.data)
			: null,
	);

	const queryClient = useQueryClient();

	const refundMutation = createMutation(() => ({
		mutationFn: (payload: RegisterRefundDto) => {
			if (legalization?.id == null) {
				throw new Error('No hay una legalización cargada');
			}
			return refundService.registerRefund(legalization.id, payload);
		},
		onSuccess: () => {
			void queryClient.invalidateQueries({
				queryKey: ['legalization', 'by-travel-request', travelRequestId],
			});
			void queryClient.invalidateQueries({
				queryKey: ['travel-request', travelRequestId],
			});
			void queryClient.invalidateQueries({ queryKey: ['travel-requests'] });
			resetForm();
			toast.success('Devolución registrada correctamente');
		},
		onError: (error) => {
			toast.error(
				'No se pudo registrar la devolución',
				getApiErrorMessage(error),
			);
		},
	}));

	let companyAccount = $state('');
	let refundReference = $state('');
	let refundDate = $state(todayLocalDate());
	let comments = $state('');
	let formError = $state('');
	let confirmDialogOpen = $state(false);

	function todayLocalDate(): string {
		const now = new Date();
		const year = now.getFullYear();
		const month = String(now.getMonth() + 1).padStart(2, '0');
		const day = String(now.getDate()).padStart(2, '0');
		return `${year}-${month}-${day}`;
	}

	function resetForm() {
		companyAccount = '';
		refundReference = '';
		refundDate = todayLocalDate();
		comments = '';
		formError = '';
	}

	function validateForm(): boolean {
		if (!companyAccount.trim()) {
			formError = 'La cuenta de la empresa es obligatoria.';
			return false;
		}
		if (!refundReference.trim()) {
			formError = 'La referencia de la devolución es obligatoria.';
			return false;
		}
		if (!refundDate) {
			formError = 'La fecha de la devolución es obligatoria.';
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
		const payload: RegisterRefundDto = {
			companyAccount: companyAccount.trim(),
			refundReference: refundReference.trim(),
			refundDate,
			comments: comments.trim().length > 0 ? comments.trim() : null,
		};
		await refundMutation.mutateAsync(payload);
	}

	const refundDescription = $derived(
		analysis
			? `Se registrará una devolución por ${formatCurrency(
					analysis.difference,
				)} para la solicitud ${analysis.requestNumber}. Esta acción no se puede deshacer.`
			: '',
	);

	function handleBack() {
		void goto(resolve(ROUTES.finance.refunds));
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
		title="Detalle de devolución"
		description={travelRequest
			? `${travelRequest.requestNumber} · ${travelRequest.applicantFullName}`
			: 'Consulta la información de la devolución.'}
		actions={headerActions}
	/>

	{#if travelRequestQuery.isPending || legalizationQuery.isPending}
		<LoadingState label="Cargando devolución…" />
	{:else if travelRequestQuery.error}
		<ApiError
			error={travelRequestQuery.error}
			title="No se pudo cargar la solicitud"
			onRetry={() => {
				void travelRequestQuery.refetch();
			}}
		/>
	{:else if legalizationQuery.error && !legalizationNotFound}
		<ApiError
			error={legalizationQuery.error}
			title="No se pudo cargar la legalización"
			onRetry={() => {
				void legalizationQuery.refetch();
			}}
		/>
	{:else if travelRequest}
		<TravelRequestDetail request={travelRequest} />

		{#if legalization}
			<LegalizationDetail legalization={legalization} />

			{#if canRegisterRefund}
				<div class="border-border bg-background rounded-md border p-6">
					<div class="space-y-1">
						<h2 class="text-base font-semibold">Registrar devolución</h2>
						<p class="text-muted-foreground text-sm">
							El monto a devolver es calculado por el sistema según la
							liquidación financiera de la legalización.
						</p>
					</div>

					{#if settlementAnalysisQuery.isPending}
						<div class="mt-5">
							<LoadingState label="Calculando devolución…" />
						</div>
					{:else if settlementAnalysisQuery.error}
						<div class="mt-5">
							<ApiError
								error={settlementAnalysisQuery.error}
								title="No se pudo calcular la devolución"
								onRetry={() => {
									void settlementAnalysisQuery.refetch();
								}}
							/>
						</div>
					{:else if analysis}
						<dl class="mt-5 grid gap-x-6 gap-y-4 sm:grid-cols-3">
							<div>
								<dt
									class="text-muted-foreground text-xs font-medium uppercase"
								>
									Monto desembolsado
								</dt>
								<dd class="mt-1 text-sm font-medium">
									{formatCurrency(analysis.disbursedAmount)}
								</dd>
							</div>
							<div>
								<dt
									class="text-muted-foreground text-xs font-medium uppercase"
								>
									Total legalizado
								</dt>
								<dd class="mt-1 text-sm font-medium">
									{formatCurrency(analysis.totalExpenses)}
								</dd>
							</div>
							<div>
								<dt class="text-muted-foreground text-xs font-medium uppercase">
									Monto a devolver
								</dt>
								<dd class="mt-1 text-sm font-semibold">
									{formatCurrency(analysis.difference)}
								</dd>
							</div>
						</dl>

						<form
							onsubmit={(event) => {
								event.preventDefault();
								openConfirmDialog();
							}}
							novalidate
							class="border-border mt-5 space-y-5 border-t pt-5"
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
									<label
										for="refund-company-account"
										class="text-sm font-medium"
									>
										Cuenta de la empresa
									</label>
									<Input
										id="refund-company-account"
										placeholder="Cuenta a la que se consigna la devolución"
										bind:value={companyAccount}
										oninput={() => (formError = '')}
										aria-invalid={formError ? 'true' : undefined}
									/>
									<p class="text-muted-foreground text-xs">
										Cuenta de la empresa a la que el empleado devuelve el
										excedente.
									</p>
								</div>

								<div class="space-y-1.5">
									<label
										for="refund-reference"
										class="text-sm font-medium"
									>
										Referencia
									</label>
									<Input
										id="refund-reference"
										maxlength={50}
										placeholder="Referencia del comprobante de la devolución"
										bind:value={refundReference}
										oninput={() => (formError = '')}
										aria-invalid={formError ? 'true' : undefined}
									/>
									<p class="text-muted-foreground text-xs">
										Referencia del comprobante bancario de la devolución.
									</p>
								</div>

								<div class="space-y-1.5">
									<label for="refund-date" class="text-sm font-medium">
										Fecha de la devolución
									</label>
									<Input
										id="refund-date"
										type="date"
										bind:value={refundDate}
										oninput={() => (formError = '')}
										aria-invalid={formError ? 'true' : undefined}
									/>
								</div>
							</div>

							<div class="space-y-1.5">
								<label for="refund-comments" class="text-sm font-medium">
									Observaciones
								</label>
								<textarea
									id="refund-comments"
									rows={4}
									maxlength={1000}
									placeholder="Opcional. Comentarios sobre la devolución."
									bind:value={comments}
									class="border-input focus-visible:border-ring focus-visible:ring-ring/50 dark:bg-input/30 placeholder:text-muted-foreground w-full min-w-0 rounded-md border bg-transparent px-2.5 py-1 text-base shadow-xs transition-[color,box-shadow] outline-none focus-visible:ring-3 disabled:cursor-not-allowed disabled:opacity-50 md:text-sm"
								/>
							</div>

							<div class="flex justify-end">
								<Button
									type="submit"
									disabled={refundMutation.isPending}
								>
									{#if refundMutation.isPending}
										<LoaderCircle
											class="size-4 animate-spin"
											aria-hidden="true"
										/>
									{:else}
										<Undo2 aria-hidden="true" />
									{/if}
									Registrar devolución
								</Button>
							</div>
						</form>
					{/if}
				</div>
			{/if}
		{/if}
	{/if}

	<ConfirmDialog
		bind:open={confirmDialogOpen}
		title="Registrar devolución"
		description={refundDescription}
		confirmLabel="Registrar"
		cancelLabel="Cancelar"
		variant="default"
		onConfirm={confirmRegister}
		onCancel={() => (confirmDialogOpen = false)}
	/>
</div>
