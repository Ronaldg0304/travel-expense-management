<script lang="ts">
	import { LoaderCircle } from '@lucide/svelte';
	import { getApiErrorMessage } from '$lib/api/api-error';
	import { Button } from '$lib/components/ui/button';
	import { Input } from '$lib/components/ui/input';
	import type { TravelRequestFormValues } from '$lib/models/travel-request';
	import { cn } from '$lib/utils';
	import {
		applyTravelRequestBackendFieldErrors,
		validateTravelRequestForm,
		type TravelRequestFormErrors,
	} from '$lib/validators/travel-request.validator';

	type FieldKey = keyof TravelRequestFormValues;

	interface FieldOptions {
		type?: 'text' | 'date' | 'number';
		placeholder?: string;
		hint?: string;
	}

	interface Props {
		saving: boolean;
		error?: string | null;
		onsubmit: (values: TravelRequestFormValues) => Promise<void>;
		onCancel: () => void;
		class?: string;
	}

	let {
		saving,
		error = null,
		onsubmit,
		onCancel,
		class: className,
	}: Props = $props();

	const defaultValues: TravelRequestFormValues = {
		travelPurpose: '',
		destination: '',
		departureDate: '',
		returnDate: '',
		requestedAmount: '',
	};

	let values = $state<TravelRequestFormValues>({ ...defaultValues });
	let errors = $state<TravelRequestFormErrors>({});
	let formError = $state('');

	$effect(() => {
		if (error) {
			const mapped = applyTravelRequestBackendFieldErrors(error);
			formError = Object.keys(mapped).length === 0 ? error : '';
			for (const key of Object.keys(mapped) as FieldKey[]) {
				if (mapped[key]) errors[key] = mapped[key];
			}
		} else {
			formError = '';
		}
	});

	function clearFieldError(field: FieldKey) {
		if (errors[field]) errors[field] = '';
	}

	async function handleSubmit() {
		if (saving) return;
		formError = '';
		const result = validateTravelRequestForm(values);
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

{#snippet field(name: FieldKey, label: string, options: FieldOptions)}
	<div class="space-y-1.5">
		<label for={`travel-request-${name}`} class="text-sm font-medium">
			{label}
		</label>
		<Input
			id={`travel-request-${name}`}
			type={options.type ?? 'text'}
			placeholder={options.placeholder}
			bind:value={values[name]}
			oninput={() => clearFieldError(name)}
			aria-invalid={errors[name] ? 'true' : undefined}
			aria-describedby={
				errors[name] ? `travel-request-${name}-error` : undefined
			}
		/>
		{#if errors[name]}
			<p id={`travel-request-${name}-error`} class="text-destructive text-sm">
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
		<div class="sm:col-span-2">
			{@render field('travelPurpose', 'Motivo del viaje', {
				placeholder: 'Ej. Visita a clientes en la regional norte',
				hint: 'Describe el motivo del viaje.',
			})}
		</div>

		{@render field('destination', 'Destino', {
			placeholder: 'Ej. Medellín',
		})}

		{@render field('requestedAmount', 'Valor solicitado', {
			type: 'number',
			placeholder: 'Ej. 500000',
			hint: 'Monto en pesos colombianos (sin decimales).',
		})}

		{@render field('departureDate', 'Fecha de salida', { type: 'date' })}

		{@render field('returnDate', 'Fecha de regreso', { type: 'date' })}
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
			{saving ? 'Creando solicitud…' : 'Crear solicitud'}
		</Button>
	</div>
</form>
