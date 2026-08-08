<script lang="ts">
	import { goto } from '$app/navigation';
	import { resolve } from '$app/paths';
	import { page } from '$app/state';
	import {
		createMutation,
		createQuery,
		useQueryClient,
	} from '@tanstack/svelte-query';
	import { ArrowLeft, Landmark, LoaderCircle } from '@lucide/svelte';
	import { getApiErrorMessage, normalizeApiError } from '$lib/api/api-error';
	import { hasAnyRole } from '$lib/auth/permission';
	import { ConfirmDialog, PageHeader } from '$lib/components/common';
	import { ApiError, LoadingState, toast } from '$lib/components/feedback';
	import { LegalizationDetail } from '$lib/components/legalizations';
	import { TravelRequestDetail } from '$lib/components/travel-requests';
	import { Button } from '$lib/components/ui/button';
	import { Input } from '$lib/components/ui/input';
	import { ROUTES } from '$lib/constants/routes';
	import type { AccountSummaryResponseDto } from '$lib/dto/account';
	import type { RegisterReimbursementDto } from '$lib/dto/reimbursement';
	import { toLegalization } from '$lib/mapper/legalization.mapper';
	import { toSettlementAnalysis } from '$lib/mapper/settlement.mapper';
	import { toTravelRequest } from '$lib/mapper/travel-request.mapper';
	import { ACCOUNT_TYPE_LABELS } from '$lib/models/account';
	import {
		accountService,
		legalizationService,
		reimbursementService,
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

	const canRegisterReimbursement = $derived(
		hasAnyRole($user, ['FINANCIERA', 'ADMINISTRADOR']) &&
			legalization?.status === 'PENDIENTE_REEMBOLSO',
	);

	const settlementAnalysisQuery = createQuery(() => ({
		queryKey: ['legalization', 'settlement-analysis', legalization?.id],
		queryFn: () => {
			if (legalization?.id == null) {
				throw new Error('No hay una legalización cargada');
			}
			return legalizationService.getSettlementAnalysis(legalization.id);
		},
		enabled: canRegisterReimbursement,
	}));

	const analysis = $derived(
		settlementAnalysisQuery.data
			? toSettlementAnalysis(settlementAnalysisQuery.data)
			: null,
	);

	const accountsQuery = createQuery(() => ({
		queryKey: ['accounts', 'by-user', travelRequest?.applicantId],
		queryFn: () => {
			if (travelRequest?.applicantId == null) {
				throw new Error('Solicitud no cargada');
			}
			return accountService.getAccountsByUser(travelRequest.applicantId);
		},
		enabled: canRegisterReimbursement,
	}));

	const accounts = $derived(
		(accountsQuery.data ?? []).filter((account) => account.active),
	);

	const queryClient = useQueryClient();

	const reimbursementMutation = createMutation(() => ({
		mutationFn: (payload: RegisterReimbursementDto) => {
			if (legalization?.id == null) {
				throw new Error('No hay una legalización cargada');
			}
			return reimbursementService.registerReimbursement(
				legalization.id,
				payload,
			);
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
			toast.success('Reembolso registrado correctamente');
		},
		onError: (error) => {
			toast.error(
				'No se pudo registrar el reembolso',
				getApiErrorMessage(error),
			);
		},
	}));

	let accountId = $state('');
	let paymentReference = $state('');
	let paymentDate = $state(todayLocalDate());
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
		accountId = '';
		paymentReference = '';
		paymentDate = todayLocalDate();
		comments = '';
		formError = '';
	}

	function accountLabel(account: AccountSummaryResponseDto): string {
		return `${account.bankName} · ${ACCOUNT_TYPE_LABELS[account.accountType]} · ${account.accountNumber}`;
	}

	function validateForm(): boolean {
		if (!accountId) {
			formError = 'Debes seleccionar la cuenta del empleado.';
			return false;
		}
		if (!paymentReference.trim()) {
			formError = 'La referencia del pago es obligatoria.';
			return false;
		}
		if (!paymentDate) {
			formError = 'La fecha del pago es obligatoria.';
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
		const payload: RegisterReimbursementDto = {
			accountId: Number(accountId),
			paymentReference: paymentReference.trim(),
			paymentDate,
			comments: comments.trim().length > 0 ? comments.trim() : null,
		};
		await reimbursementMutation.mutateAsync(payload);
	}

	const reimbursementDescription = $derived(
		analysis
			? `Se registrará un reembolso por ${formatCurrency(
					Math.abs(analysis.difference),
				)} para la solicitud ${analysis.requestNumber}. Esta acción no se puede deshacer.`
			: '',
	);

	function handleBack() {
		void goto(resolve(ROUTES.finance.reimbursements));
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
		title="Detalle de reembolso"
		description={travelRequest
			? `${travelRequest.requestNumber} · ${travelRequest.applicantFullName}`
			: 'Consulta la información del reembolso.'}
		actions={headerActions}
	/>

	{#if travelRequestQuery.isPending || legalizationQuery.isPending}
		<LoadingState label="Cargando reembolso…" />
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

			{#if canRegisterReimbursement}
				<div class="border-border bg-background rounded-md border p-6">
					<div class="space-y-1">
						<h2 class="text-base font-semibold">Registrar reembolso</h2>
						<p class="text-muted-foreground text-sm">
							El monto a reembolsar es calculado por el sistema según la
							liquidación financiera de la legalización.
						</p>
					</div>

					{#if settlementAnalysisQuery.isPending || accountsQuery.isPending}
						<div class="mt-5">
							<LoadingState label="Calculando reembolso…" />
						</div>
					{:else if settlementAnalysisQuery.error || accountsQuery.error}
						<div class="mt-5">
							<ApiError
								error={
									settlementAnalysisQuery.error ?? accountsQuery.error
								}
								title="No se pudo calcular el reembolso"
								onRetry={() => {
									void settlementAnalysisQuery.refetch();
									void accountsQuery.refetch();
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
									Monto a reembolsar
								</dt>
								<dd class="mt-1 text-sm font-semibold">
									{formatCurrency(Math.abs(analysis.difference))}
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

							<div class="space-y-1.5">
								<label for="reimbursement-account" class="text-sm font-medium">
									Cuenta del empleado
								</label>
								<select
									id="reimbursement-account"
									bind:value={accountId}
									onchange={() => (formError = '')}
									class="border-input focus-visible:border-ring focus-visible:ring-ring/50 dark:bg-input/30 placeholder:text-muted-foreground h-9 w-full min-w-0 rounded-md border bg-transparent px-2.5 py-1 text-base shadow-xs transition-[color,box-shadow] outline-none focus-visible:ring-3 disabled:cursor-not-allowed disabled:opacity-50 md:text-sm"
								>
									<option value="">Selecciona la cuenta del empleado</option>
									{#each accounts as account (account.id)}
										<option value={account.id}>
											{accountLabel(account)}
										</option>
									{/each}
								</select>
								<p class="text-muted-foreground text-xs">
									Cuenta activa del solicitante a la que se consigna el
									reembolso.
								</p>
								{#if accounts.length === 0}
									<p class="text-destructive text-xs">
										El solicitante no tiene cuentas activas registradas.
									</p>
								{/if}
							</div>

							<div class="grid gap-4 sm:grid-cols-2">
								<div class="space-y-1.5">
									<label
										for="reimbursement-reference"
										class="text-sm font-medium"
									>
										Referencia del pago
									</label>
									<Input
										id="reimbursement-reference"
										maxlength={50}
										placeholder="Referencia del comprobante del pago"
										bind:value={paymentReference}
										oninput={() => (formError = '')}
										aria-invalid={formError ? 'true' : undefined}
									/>
									<p class="text-muted-foreground text-xs">
										Referencia del comprobante bancario del reembolso.
									</p>
								</div>

								<div class="space-y-1.5">
									<label
										for="reimbursement-payment-date"
										class="text-sm font-medium"
									>
										Fecha del pago
									</label>
									<Input
										id="reimbursement-payment-date"
										type="date"
										bind:value={paymentDate}
										oninput={() => (formError = '')}
										aria-invalid={formError ? 'true' : undefined}
									/>
								</div>
							</div>

							<div class="space-y-1.5">
								<label
									for="reimbursement-comments"
									class="text-sm font-medium"
								>
									Observaciones
								</label>
								<textarea
									id="reimbursement-comments"
									rows={4}
									maxlength={1000}
									placeholder="Opcional. Comentarios sobre el reembolso."
									bind:value={comments}
									class="border-input focus-visible:border-ring focus-visible:ring-ring/50 dark:bg-input/30 placeholder:text-muted-foreground w-full min-w-0 rounded-md border bg-transparent px-2.5 py-1 text-base shadow-xs transition-[color,box-shadow] outline-none focus-visible:ring-3 disabled:cursor-not-allowed disabled:opacity-50 md:text-sm"
								/>
							</div>

							<div class="flex justify-end">
								<Button
									type="submit"
									disabled={reimbursementMutation.isPending}
								>
									{#if reimbursementMutation.isPending}
										<LoaderCircle
											class="size-4 animate-spin"
											aria-hidden="true"
										/>
									{:else}
										<Landmark aria-hidden="true" />
									{/if}
									Registrar reembolso
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
		title="Registrar reembolso"
		description={reimbursementDescription}
		confirmLabel="Registrar"
		cancelLabel="Cancelar"
		variant="default"
		onConfirm={confirmRegister}
		onCancel={() => (confirmDialogOpen = false)}
	/>
</div>
