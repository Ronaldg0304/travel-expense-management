<script lang="ts">
	import { FilePlus2, LoaderCircle, Plus, Trash2 } from '@lucide/svelte';
	import type { CostCenterSummaryResponseDto } from '$lib/dto/cost-center';
	import type { ExpenseTypeSummaryResponseDto } from '$lib/dto/expense-type';
	import type { CreateLegalizationDto } from '$lib/dto/legalization';
	import { Button } from '$lib/components/ui/button';
	import { Input } from '$lib/components/ui/input';
	import { formatCurrency } from '$lib/utils';

	interface ExpenseFormRow {
		key: number;
		expenseTypeId: string;
		expenseDate: string;
		description: string;
		amount: string;
	}

	interface Props {
		travelRequestId: number;
		costCenters: CostCenterSummaryResponseDto[];
		expenseTypes: ExpenseTypeSummaryResponseDto[];
		submitting: boolean;
		onSubmit: (payload: CreateLegalizationDto) => void;
	}

	let {
		travelRequestId,
		costCenters,
		expenseTypes,
		submitting,
		onSubmit,
	}: Props = $props();

	const selectClass =
		'border-input focus-visible:border-ring focus-visible:ring-ring/50 dark:bg-input/30 placeholder:text-muted-foreground h-9 w-full min-w-0 rounded-md border bg-transparent px-2.5 py-1 text-base shadow-xs transition-[color,box-shadow] outline-none focus-visible:ring-3 disabled:cursor-not-allowed disabled:opacity-50 md:text-sm';

	let nextRowKey = 1;

	let costCenterId = $state('');
	let expenseRows = $state<ExpenseFormRow[]>([emptyRow()]);
	let formError = $state('');

	function todayLocalDate(): string {
		const now = new Date();
		const year = now.getFullYear();
		const month = String(now.getMonth() + 1).padStart(2, '0');
		const day = String(now.getDate()).padStart(2, '0');
		return `${year}-${month}-${day}`;
	}

	function emptyRow(): ExpenseFormRow {
		return {
			key: nextRowKey++,
			expenseTypeId: '',
			expenseDate: '',
			description: '',
			amount: '',
		};
	}

	const total = $derived(
		expenseRows.reduce(
			(sum, row) =>
				row.amount.trim() && /^\d+$/.test(row.amount.trim())
					? sum + Number(row.amount.trim())
					: sum,
			0,
		),
	);

	function addExpenseRow() {
		expenseRows.push(emptyRow());
	}

	function removeExpenseRow(index: number) {
		if (expenseRows.length <= 1) return;
		expenseRows.splice(index, 1);
	}

	function validateForm(): boolean {
		if (!costCenterId) {
			formError = 'Debes seleccionar un centro de costo.';
			return false;
		}
		for (const [index, row] of expenseRows.entries()) {
			const n = index + 1;
			if (!row.expenseTypeId) {
				formError = `Debes seleccionar el tipo de gasto del gasto ${n}.`;
				return false;
			}
			if (!row.expenseDate) {
				formError = `Debes indicar la fecha del gasto ${n}.`;
				return false;
			}
			if (row.expenseDate > todayLocalDate()) {
				formError = `La fecha del gasto ${n} no puede ser futura.`;
				return false;
			}
			if (!row.description.trim()) {
				formError = `Debes indicar la descripción del gasto ${n}.`;
				return false;
			}
			if (row.description.trim().length > 500) {
				formError = `La descripción del gasto ${n} no puede superar los 500 caracteres.`;
				return false;
			}
			const amount = row.amount.trim();
			if (!/^\d+$/.test(amount) || Number(amount) <= 0) {
				formError = `El valor del gasto ${n} debe ser un número entero positivo.`;
				return false;
			}
		}
		formError = '';
		return true;
	}

	function buildPayload(): CreateLegalizationDto {
		return {
			travelRequestId,
			costCenterId: Number(costCenterId),
			expenses: expenseRows.map((row) => ({
				expenseTypeId: Number(row.expenseTypeId),
				expenseDate: row.expenseDate,
				description: row.description.trim(),
				amount: Number(row.amount.trim()),
			})),
		};
	}

	function handleSubmit(event: SubmitEvent) {
		event.preventDefault();
		if (!validateForm()) return;
		onSubmit(buildPayload());
	}
</script>

<div class="border-border bg-background rounded-md border p-6">
	<div class="space-y-1">
		<h2 class="text-base font-semibold">Registrar legalización</h2>
		<p class="text-muted-foreground text-sm">
			Registra los gastos y el centro de costo para esta solicitud. Esta
			acción no se puede deshacer.
		</p>
	</div>

	<form onsubmit={handleSubmit} novalidate class="mt-5 space-y-5">
		{#if formError}
			<div
				role="alert"
				class="bg-destructive/10 text-destructive rounded-md px-3 py-2 text-sm"
			>
				{formError}
			</div>
		{/if}

		<div class="space-y-1.5">
			<label for="legalization-cost-center" class="text-sm font-medium">
				Centro de costo
			</label>
			<select
				id="legalization-cost-center"
				bind:value={costCenterId}
				onchange={() => (formError = '')}
				class={selectClass}
			>
				<option value="">Selecciona un centro de costo</option>
				{#each costCenters as costCenter (costCenter.id)}
					<option value={costCenter.id}>{costCenter.name}</option>
				{/each}
			</select>
			<p class="text-muted-foreground text-xs">
				Centro de costo activo para la asignación contable.
			</p>
		</div>

		<div class="flex items-center justify-between">
			<h3 class="text-sm font-semibold">Gastos</h3>
			<Button type="button" variant="outline" size="sm" onclick={addExpenseRow}>
				<Plus aria-hidden="true" />
				Agregar gasto
			</Button>
		</div>

		{#each expenseRows as row, index (row.key)}
			<div class="border-border rounded-md border p-4">
				<div class="grid gap-4 sm:grid-cols-2">
					<div class="space-y-1.5">
						<label
							for={`expense-type-${row.key}`}
							class="text-sm font-medium"
						>
							Tipo de gasto
						</label>
						<select
							id={`expense-type-${row.key}`}
							bind:value={row.expenseTypeId}
							onchange={() => (formError = '')}
							class={selectClass}
						>
							<option value="">Selecciona el tipo de gasto</option>
							{#each expenseTypes as expenseType (expenseType.id)}
								<option value={expenseType.id}>
									{expenseType.name}
								</option>
							{/each}
						</select>
					</div>

					<div class="space-y-1.5">
						<label for={`expense-date-${row.key}`} class="text-sm font-medium">
							Fecha
						</label>
						<Input
							id={`expense-date-${row.key}`}
							type="date"
							bind:value={row.expenseDate}
							oninput={() => (formError = '')}
						/>
					</div>

					<div class="space-y-1.5 sm:col-span-2">
						<label
							for={`expense-description-${row.key}`}
							class="text-sm font-medium"
						>
							Descripción
						</label>
						<Input
							id={`expense-description-${row.key}`}
							maxlength={500}
							placeholder="Describe el gasto"
							bind:value={row.description}
							oninput={() => (formError = '')}
						/>
					</div>

					<div class="flex items-end gap-2">
						<div class="flex-1 space-y-1.5">
							<label
								for={`expense-amount-${row.key}`}
								class="text-sm font-medium"
							>
								Valor
							</label>
							<Input
								id={`expense-amount-${row.key}`}
								type="number"
								min="1"
								step="1"
								placeholder="Ej. 120000"
								bind:value={row.amount}
								oninput={() => (formError = '')}
							/>
						</div>
						<Button
							type="button"
							variant="ghost"
							size="icon-sm"
							aria-label={`Quitar gasto ${index + 1}`}
							disabled={expenseRows.length <= 1}
							onclick={() => removeExpenseRow(index)}
						>
							<Trash2 aria-hidden="true" />
						</Button>
					</div>
				</div>
			</div>
		{/each}

		<div
			class="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between"
		>
			<p class="text-sm">
				<span class="text-muted-foreground">Total: </span>
				<span class="font-semibold">{formatCurrency(total)}</span>
			</p>
			<Button type="submit" disabled={submitting}>
				{#if submitting}
					<LoaderCircle class="size-4 animate-spin" aria-hidden="true" />
				{:else}
					<FilePlus2 aria-hidden="true" />
				{/if}
				Registrar legalización
			</Button>
		</div>
	</form>
</div>
