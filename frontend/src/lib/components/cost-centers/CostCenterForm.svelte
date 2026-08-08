<script lang="ts">
	import { LoaderCircle } from '@lucide/svelte';
	import { getApiErrorMessage } from '$lib/api/api-error';
	import { Button } from '$lib/components/ui/button';
	import { Input } from '$lib/components/ui/input';
	import {
		COST_CENTER_TYPES,
		COST_CENTER_TYPE_LABELS,
		type CostCenterFormValues,
	} from '$lib/models/cost-center';
	import { cn } from '$lib/utils';
	import {
		applyCostCenterBackendFieldErrors,
		validateCostCenterForm,
		type CostCenterFormErrors,
	} from '$lib/validators/cost-center.validator';

	type Mode = 'create' | 'edit';

	type TextFieldKey = 'code' | 'name';

	interface TextFieldOptions {
		type?: string;
		placeholder?: string;
		disabled?: boolean;
		hint?: string;
	}

	interface Props {
		mode: Mode;
		initial?: Partial<CostCenterFormValues>;
		saving: boolean;
		error?: string | null;
		onsubmit: (values: CostCenterFormValues) => Promise<void>;
		onCancel: () => void;
		class?: string;
	}

	let {
		mode,
		initial,
		saving,
		error = null,
		onsubmit,
		onCancel,
		class: className,
	}: Props = $props();

	const defaultValues: CostCenterFormValues = {
		code: '',
		name: '',
		type: 'DEPARTMENT',
		active: true,
	};

	let values = $state<CostCenterFormValues>({ ...defaultValues, ...initial });
	let errors = $state<CostCenterFormErrors>({});
	let formError = $state('');

	const selectClass =
		'border-input focus-visible:border-ring focus-visible:ring-ring/50 h-9 w-full rounded-md border bg-transparent px-3 py-1 text-sm shadow-xs focus-visible:ring-3 focus-visible:outline-none disabled:pointer-events-none disabled:opacity-50 aria-invalid:border-destructive';

	$effect(() => {
		if (error) {
			const mapped = applyCostCenterBackendFieldErrors(error);
			formError = Object.keys(mapped).length === 0 ? error : '';
			for (const key of Object.keys(mapped) as (keyof CostCenterFormValues)[]) {
				if (mapped[key]) errors[key] = mapped[key];
			}
		} else {
			formError = '';
		}
	});

	function clearFieldError(field: keyof CostCenterFormValues) {
		if (errors[field]) errors[field] = '';
	}

	async function handleSubmit() {
		if (saving) return;
		formError = '';
		const result = validateCostCenterForm(mode, values);
		if (!result.success) {
			errors = result.errors;
			return;
		}
		errors = {};
		try {
			await onsubmit({ ...values });
		} catch (err) {
			formError = getApiErrorMessage(err);
		}
	}
</script>

{#snippet textField(
	name: TextFieldKey,
	label: string,
	options: TextFieldOptions,
)}
	<div class="space-y-1.5">
		<label for={`cost-center-${name}`} class="text-sm font-medium">
			{label}
		</label>
		<Input
			id={`cost-center-${name}`}
			type={options.type ?? 'text'}
			placeholder={options.placeholder}
			disabled={options.disabled}
			bind:value={values[name]}
			oninput={() => clearFieldError(name)}
			aria-invalid={errors[name] ? 'true' : undefined}
			aria-describedby={errors[name] ? `cost-center-${name}-error` : undefined}
		/>
		{#if errors[name]}
			<p id={`cost-center-${name}-error`} class="text-destructive text-sm">
				{errors[name]}
			</p>
		{:else if options.hint}
			<p class="text-muted-foreground text-xs">{options.hint}</p>
		{/if}
	</div>
{/snippet}

<form
	onsubmit={(event) => {
		event.preventDefault();
		void handleSubmit();
	}}
	novalidate
	class={cn('space-y-5', className)}
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
		{@render textField('code', 'Código', {
			placeholder: 'Ej. CC-001',
			disabled: mode === 'edit',
			hint:
				mode === 'edit'
					? 'El código no se puede modificar.'
					: 'Identificador único del centro de costo.',
		})}

		{@render textField('name', 'Nombre', {
			placeholder: 'Nombre del centro de costo',
		})}

		<div class="space-y-1.5">
			<label for="cost-center-type" class="text-sm font-medium">Tipo</label>
			<select
				id="cost-center-type"
				class={selectClass}
				bind:value={values.type}
				onchange={() => clearFieldError('type')}
				aria-invalid={errors.type ? 'true' : undefined}
				aria-describedby={errors.type ? 'cost-center-type-error' : undefined}
			>
				{#each COST_CENTER_TYPES as type (type)}
					<option value={type}>{COST_CENTER_TYPE_LABELS[type]}</option>
				{/each}
			</select>
			{#if errors.type}
				<p id="cost-center-type-error" class="text-destructive text-sm">
					{errors.type}
				</p>
			{/if}
		</div>

		<div class="space-y-1.5">
			<label class="flex items-start gap-2 text-sm font-medium">
				<input
					type="checkbox"
					bind:checked={values.active}
					onchange={() => clearFieldError('active')}
					class="mt-0.5 size-4 rounded border"
					aria-invalid={errors.active ? 'true' : undefined}
				/>
				<span>
					Centro de costo activo
					<span class="text-muted-foreground block text-xs font-normal">
						{mode === 'edit'
							? 'Desactiva el centro de costo para impedir su uso en nuevas solicitudes.'
							: 'Un centro de costo activo puede asociarse a las solicitudes de viaje.'}
					</span>
				</span>
			</label>
			{#if errors.active}
				<p class="text-destructive text-sm">{errors.active}</p>
			{/if}
		</div>
	</div>

	<div
		class="border-border flex flex-col-reverse justify-end gap-2 border-t pt-4 sm:flex-row"
	>
		<Button
			type="button"
			variant="outline"
			onclick={onCancel}
			disabled={saving}
		>
			Cancelar
		</Button>
		<Button type="submit" disabled={saving}>
			{#if saving}
				<LoaderCircle class="size-4 animate-spin" aria-hidden="true" />
			{/if}
			{#if mode === 'create'}
				{saving ? 'Creando centro de costo…' : 'Crear centro de costo'}
			{:else}
				{saving ? 'Guardando cambios…' : 'Guardar cambios'}
			{/if}
		</Button>
	</div>
</form>
