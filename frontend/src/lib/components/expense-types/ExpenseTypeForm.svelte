<script lang="ts">
	import { LoaderCircle } from '@lucide/svelte';
	import { getApiErrorMessage } from '$lib/api/api-error';
	import { Button } from '$lib/components/ui/button';
	import { Input } from '$lib/components/ui/input';
	import type { ExpenseTypeFormValues } from '$lib/models/expense-type';
	import { cn } from '$lib/utils';
	import {
		applyExpenseTypeBackendFieldErrors,
		validateExpenseTypeForm,
		type ExpenseTypeFormErrors,
	} from '$lib/validators/expense-type.validator';

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
		initial?: Partial<ExpenseTypeFormValues>;
		saving: boolean;
		error?: string | null;
		onsubmit: (values: ExpenseTypeFormValues) => Promise<void>;
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

	const defaultValues: ExpenseTypeFormValues = {
		code: '',
		name: '',
		active: true,
	};

	let values = $state<ExpenseTypeFormValues>({ ...defaultValues, ...initial });
	let errors = $state<ExpenseTypeFormErrors>({});
	let formError = $state('');

	$effect(() => {
		if (error) {
			const mapped = applyExpenseTypeBackendFieldErrors(error);
			formError = Object.keys(mapped).length === 0 ? error : '';
			for (const key of Object.keys(
				mapped,
			) as (keyof ExpenseTypeFormValues)[]) {
				if (mapped[key]) errors[key] = mapped[key];
			}
		} else {
			formError = '';
		}
	});

	function clearFieldError(field: keyof ExpenseTypeFormValues) {
		if (errors[field]) errors[field] = '';
	}

	async function handleSubmit() {
		if (saving) return;
		formError = '';
		const result = validateExpenseTypeForm(mode, values);
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
		<label for={`expense-type-${name}`} class="text-sm font-medium">
			{label}
		</label>
		<Input
			id={`expense-type-${name}`}
			type={options.type ?? 'text'}
			placeholder={options.placeholder}
			disabled={options.disabled}
			bind:value={values[name]}
			oninput={() => clearFieldError(name)}
			aria-invalid={errors[name] ? 'true' : undefined}
			aria-describedby={errors[name] ? `expense-type-${name}-error` : undefined}
		/>
		{#if errors[name]}
			<p id={`expense-type-${name}-error`} class="text-destructive text-sm">
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
			placeholder: 'Ej. GT-001',
			disabled: mode === 'edit',
			hint:
				mode === 'edit'
					? 'El código no se puede modificar.'
					: 'Identificador único del tipo de gasto.',
		})}

		{@render textField('name', 'Nombre', {
			placeholder: 'Nombre del tipo de gasto',
		})}

		<div class="space-y-1.5 sm:col-span-2">
			<label class="flex items-start gap-2 text-sm font-medium">
				<input
					type="checkbox"
					bind:checked={values.active}
					onchange={() => clearFieldError('active')}
					class="mt-0.5 size-4 rounded border"
					aria-invalid={errors.active ? 'true' : undefined}
				/>
				<span>
					Tipo de gasto activo
					<span class="text-muted-foreground block text-xs font-normal">
						{mode === 'edit'
							? 'Desactiva el tipo de gasto para impedir su uso en nuevas legalizaciones.'
							: 'Un tipo de gasto activo puede asociarse a las legalizaciones.'}
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
				{saving ? 'Creando tipo de gasto…' : 'Crear tipo de gasto'}
			{:else}
				{saving ? 'Guardando cambios…' : 'Guardar cambios'}
			{/if}
		</Button>
	</div>
</form>
