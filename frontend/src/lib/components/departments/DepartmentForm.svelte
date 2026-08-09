<script lang="ts">
	import { LoaderCircle } from '@lucide/svelte';
	import { getApiErrorMessage } from '$lib/api/api-error';
	import { Button } from '$lib/components/ui/button';
	import { Input } from '$lib/components/ui/input';
	import type { DepartmentFormValues } from '$lib/models/department';
	import { cn } from '$lib/utils';
	import {
		applyDepartmentBackendFieldErrors,
		validateDepartmentForm,
		type DepartmentFormErrors,
	} from '$lib/validators/department.validator';

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
		initial?: Partial<DepartmentFormValues>;
		saving: boolean;
		error?: string | null;
		onsubmit: (values: DepartmentFormValues) => Promise<void>;
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

	const defaultValues: DepartmentFormValues = {
		code: '',
		name: '',
		active: true,
	};

	let values = $state<DepartmentFormValues>({ ...defaultValues, ...initial });
	let errors = $state<DepartmentFormErrors>({});
	let formError = $state('');

	$effect(() => {
		if (error) {
			const mapped = applyDepartmentBackendFieldErrors(error);
			formError = Object.keys(mapped).length === 0 ? error : '';
			for (const key of Object.keys(
				mapped,
			) as (keyof DepartmentFormValues)[]) {
				if (mapped[key]) errors[key] = mapped[key];
			}
		} else {
			formError = '';
		}
	});

	function clearFieldError(field: keyof DepartmentFormValues) {
		if (errors[field]) errors[field] = '';
	}

	async function handleSubmit() {
		if (saving) return;
		formError = '';
		const result = validateDepartmentForm(mode, values);
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
		<label for={`department-${name}`} class="text-sm font-medium">
			{label}
		</label>
		<Input
			id={`department-${name}`}
			type={options.type ?? 'text'}
			placeholder={options.placeholder}
			disabled={options.disabled}
			bind:value={values[name]}
			oninput={() => clearFieldError(name)}
			aria-invalid={errors[name] ? 'true' : undefined}
			aria-describedby={errors[name] ? `department-${name}-error` : undefined}
		/>
		{#if errors[name]}
			<p id={`department-${name}-error`} class="text-destructive text-sm">
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
			placeholder: 'Ej. TEC-001',
			disabled: mode === 'edit',
			hint:
				mode === 'edit'
					? 'El código no se puede modificar.'
					: 'Identificador único del departamento.',
		})}

		{@render textField('name', 'Nombre', {
			placeholder: 'Nombre del departamento',
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
					Departamento activo
					<span class="text-muted-foreground block text-xs font-normal">
						{mode === 'edit'
							? 'Desactiva el departamento para impedir su uso en nuevos registros.'
							: 'Un departamento activo puede asignarse a los usuarios.'}
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
				{saving ? 'Creando departamento…' : 'Crear departamento'}
			{:else}
				{saving ? 'Guardando cambios…' : 'Guardar cambios'}
			{/if}
		</Button>
	</div>
</form>
