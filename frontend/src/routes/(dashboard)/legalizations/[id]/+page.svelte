<script lang="ts">
	import { goto } from '$app/navigation';
	import { resolve } from '$app/paths';
	import { page } from '$app/state';
	import {
		createMutation,
		createQuery,
		useQueryClient,
	} from '@tanstack/svelte-query';
	import { ArrowLeft } from '@lucide/svelte';
	import { getApiErrorMessage, normalizeApiError } from '$lib/api/api-error';
	import { hasAnyRole } from '$lib/auth/permission';
	import { ConfirmDialog, PageHeader } from '$lib/components/common';
	import { ApiError, LoadingState, toast } from '$lib/components/feedback';
	import {
		LegalizationDetail,
		LegalizationForm,
		LegalizationValidationSection,
		SupportFileSection,
	} from '$lib/components/legalizations';
	import { TravelRequestDetail } from '$lib/components/travel-requests';
	import { Button } from '$lib/components/ui/button';
	import { ROUTES } from '$lib/constants/routes';
	import type { CreateLegalizationDto } from '$lib/dto/legalization';
	import { toLegalization } from '$lib/mapper/legalization.mapper';
	import { toSettlementAnalysis } from '$lib/mapper/settlement.mapper';
	import { toSupportFile } from '$lib/mapper/support-file.mapper';
	import { toTravelRequest } from '$lib/mapper/travel-request.mapper';
	import type { SettlementAnalysis } from '$lib/models/settlement';
	import type { SupportFile } from '$lib/models/support-file';
	import {
		costCenterService,
		expenseTypeService,
		legalizationService,
		supportFileService,
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

	const canCreate = $derived(
		travelRequest?.status === 'DESEMBOLSADA' && legalizationNotFound,
	);

	const costCentersQuery = createQuery(() => ({
		queryKey: ['cost-centers', { page: 0, size: 200 }],
		queryFn: () => costCenterService.getCostCenters({ page: 0, size: 200 }),
		enabled: canCreate,
	}));

	const expenseTypesQuery = createQuery(() => ({
		queryKey: ['expense-types', { page: 0, size: 200 }],
		queryFn: () => expenseTypeService.getExpenseTypes({ page: 0, size: 200 }),
		enabled: canCreate,
	}));

	const costCenters = $derived(
		(costCentersQuery.data?.content ?? []).filter(
			(costCenter) => costCenter.active,
		),
	);

	const expenseTypes = $derived(
		(expenseTypesQuery.data?.content ?? []).filter(
			(expenseType) => expenseType.active,
		),
	);

	const queryClient = useQueryClient();

	const currentLegalizationId = $derived(legalization?.id);

	const supportFilesQuery = createQuery(() => ({
		queryKey: ['legalization', 'support-files', travelRequestId],
		queryFn: () => {
			if (currentLegalizationId == null) {
				throw new Error('No hay una legalización cargada');
			}
			return supportFileService.list(currentLegalizationId);
		},
		enabled: currentLegalizationId != null,
	}));

	const supportFiles = $derived(
		(supportFilesQuery.data ?? []).map(toSupportFile),
	);

	let uploadProgress = $state(0);

	const uploadMutation = createMutation(() => ({
		mutationFn: (file: File) => {
			if (currentLegalizationId == null) {
				throw new Error('No hay una legalización cargada');
			}
			return supportFileService.upload(currentLegalizationId, file, (percent) => {
				uploadProgress = percent;
			});
		},
		onSuccess: () => {
			void queryClient.invalidateQueries({
				queryKey: ['legalization', 'support-files', travelRequestId],
			});
			toast.success('Archivo subido correctamente');
		},
		onError: (error) => {
			toast.error('No se pudo subir el archivo', getApiErrorMessage(error));
		},
		onSettled: () => {
			uploadProgress = 0;
		},
	}));

	function handleUpload(file: File) {
		uploadMutation.mutate(file);
	}

	async function handleDownload(file: SupportFile) {
		if (currentLegalizationId == null) return;
		try {
			const blob = await supportFileService.download(
				currentLegalizationId,
				file.id,
			);
			const url = URL.createObjectURL(blob);
			const link = document.createElement('a');
			link.href = url;
			link.download = file.originalFileName;
			document.body.appendChild(link);
			link.click();
			link.remove();
			URL.revokeObjectURL(url);
		} catch (error) {
			toast.error(
				'No se pudo descargar el archivo',
				getApiErrorMessage(error),
			);
		}
	}

	const createMutation = createMutation(() => ({
		mutationFn: (payload: CreateLegalizationDto) =>
			legalizationService.create(payload),
		onSuccess: () => {
			void queryClient.invalidateQueries({
				queryKey: ['legalization', 'by-travel-request', travelRequestId],
			});
			void queryClient.invalidateQueries({
				queryKey: ['travel-request', travelRequestId],
			});
			void queryClient.invalidateQueries({ queryKey: ['travel-requests'] });
			toast.success('Legalización registrada correctamente');
		},
		onError: (error) => {
			toast.error(
				'No se pudo registrar la legalización',
				getApiErrorMessage(error),
			);
		},
	}));

	const canValidate = $derived(
		hasAnyRole($user, ['FINANCIERA', 'ADMINISTRADOR']) &&
			legalization?.status === 'LEGALIZADA',
	);

	const settlementAnalysisQuery = createQuery(() => ({
		queryKey: ['legalization', 'settlement-analysis', currentLegalizationId],
		queryFn: () => {
			if (currentLegalizationId == null) {
				throw new Error('No hay una legalización cargada');
			}
			return legalizationService.getSettlementAnalysis(currentLegalizationId);
		},
		enabled: canValidate,
	}));

	const analysis = $derived(
		settlementAnalysisQuery.data
			? toSettlementAnalysis(settlementAnalysisQuery.data)
			: null,
	);

	let validationDialogOpen = $state(false);

	const validationDescription = $derived(
		analysis
			? `La legalización de la solicitud ${analysis.requestNumber} será validada. El sistema calculará la liquidación (monto desembolsado ${formatCurrency(analysis.disbursedAmount)}, gastos legalizados ${formatCurrency(analysis.totalExpenses)}) y actualizará el estado según el resultado. Esta acción no se puede deshacer.`
			: '',
	);

	const validateMutation = createMutation(() => ({
		mutationFn: () => {
			if (currentLegalizationId == null) {
				throw new Error('No hay una legalización cargada');
			}
			return legalizationService.validate(currentLegalizationId);
		},
		onSuccess: () => {
			void queryClient.invalidateQueries({
				queryKey: ['legalization', 'by-travel-request', travelRequestId],
			});
			void queryClient.invalidateQueries({
				queryKey: ['travel-request', travelRequestId],
			});
			void queryClient.invalidateQueries({ queryKey: ['travel-requests'] });
			toast.success('Legalización validada correctamente');
		},
		onError: (error) => {
			toast.error(
				'No se pudo validar la legalización',
				getApiErrorMessage(error),
			);
		},
	}));

	async function confirmValidate() {
		if (currentLegalizationId == null) return;
		await validateMutation.mutateAsync();
		validationDialogOpen = false;
	}

	let pendingPayload = $state<CreateLegalizationDto | null>(null);
	let confirmDialogOpen = $state(false);

	const pendingSummary = $derived.by(() => {
		const payload = pendingPayload;
		if (!payload) return null;
		const costCenter = costCenters.find(
			(costCenter) => costCenter.id === payload.costCenterId,
		);
		const total = payload.expenses.reduce(
			(sum, expense) => sum + expense.amount,
			0,
		);
		return {
			costCenterName: costCenter?.name ?? '—',
			expenseCount: payload.expenses.length,
			total,
		};
	});

	const confirmDescription = $derived(
		pendingSummary && travelRequest
			? `Se registrará la legalización de la solicitud ${travelRequest.requestNumber} con centro de costo ${pendingSummary.costCenterName} y ${pendingSummary.expenseCount} ${pendingSummary.expenseCount === 1 ? 'gasto' : 'gastos'} por ${formatCurrency(pendingSummary.total)}. Esta acción no se puede deshacer.`
			: '',
	);

	function handleRequestCreate(payload: CreateLegalizationDto) {
		pendingPayload = payload;
		confirmDialogOpen = true;
	}

	async function confirmCreate() {
		if (!pendingPayload) return;
		await createMutation.mutateAsync(pendingPayload);
		pendingPayload = null;
	}

	function handleBack() {
		void goto(resolve(ROUTES.finance.settlements));
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

	{#if travelRequestQuery.isPending || legalizationQuery.isPending}
		<LoadingState label="Cargando solicitud…" />
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

		{#if canCreate}
			{#if costCentersQuery.isPending || expenseTypesQuery.isPending}
				<LoadingState label="Cargando catálogos…" />
			{:else if costCentersQuery.error || expenseTypesQuery.error}
				<ApiError
					error={costCentersQuery.error ?? expenseTypesQuery.error}
					title="No se pudieron cargar los catálogos"
					onRetry={() => {
						void costCentersQuery.refetch();
						void expenseTypesQuery.refetch();
					}}
				/>
			{:else}
				<LegalizationForm
					{travelRequestId}
					{costCenters}
					{expenseTypes}
					submitting={createMutation.isPending}
					onSubmit={handleRequestCreate}
				/>
			{/if}
		{:else if legalization}
			<LegalizationDetail legalization={legalization} />

			{#if canValidate}
				<LegalizationValidationSection
					analysis={analysis}
					isPending={settlementAnalysisQuery.isPending}
					error={settlementAnalysisQuery.error}
					onRetry={() => void settlementAnalysisQuery.refetch()}
					status={legalization.status}
					validating={validateMutation.isPending}
					onValidate={() => (validationDialogOpen = true)}
				/>
			{/if}

			<SupportFileSection
				files={supportFiles}
				isPending={supportFilesQuery.isPending}
				error={supportFilesQuery.error}
				onRetry={() => void supportFilesQuery.refetch()}
				uploading={uploadMutation.isPending}
				uploadProgress={uploadProgress}
				onUpload={handleUpload}
				onDownload={handleDownload}
			/>
		{/if}
	{/if}

	<ConfirmDialog
		bind:open={confirmDialogOpen}
		title="Registrar legalización"
		description={confirmDescription}
		confirmLabel="Registrar"
		cancelLabel="Cancelar"
		variant="default"
		onConfirm={confirmCreate}
		onCancel={() => (confirmDialogOpen = false)}
	/>

	<ConfirmDialog
		bind:open={validationDialogOpen}
		title="Validar legalización"
		description={validationDescription}
		confirmLabel="Validar"
		cancelLabel="Cancelar"
		variant="default"
		onConfirm={confirmValidate}
		onCancel={() => (validationDialogOpen = false)}
	/>
</div>
