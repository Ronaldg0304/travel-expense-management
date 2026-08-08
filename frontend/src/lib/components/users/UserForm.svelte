<script lang="ts">
	import { LoaderCircle } from '@lucide/svelte';
	import { ROLE_LABELS, USER_ROLES } from '$lib/auth/auth.types';
	import { getApiErrorMessage } from '$lib/api/api-error';
	import { Button } from '$lib/components/ui/button';
	import { Input } from '$lib/components/ui/input';
	import {
		DOCUMENT_TYPES,
		DOCUMENT_TYPE_LABELS,
		type DepartmentOption,
		type UserFormValues,
	} from '$lib/models/user';
	import { cn } from '$lib/utils';
	import {
		applyBackendFieldErrors,
		validateUserForm,
		type UserFormErrors,
	} from '$lib/validators/user.validator';

	type Mode = 'create' | 'edit';

	type StringTextFieldKey =
		| 'firstName'
		| 'lastName'
		| 'email'
		| 'documentNumber'
		| 'phone'
		| 'position';

	interface TextFieldOptions {
		type?: string;
		placeholder?: string;
		disabled?: boolean;
		hint?: string;
	}

	interface Props {
		mode: Mode;
		initial?: Partial<UserFormValues>;
		departments: DepartmentOption[];
		saving: boolean;
		error?: string | null;
		onsubmit: (values: UserFormValues) => Promise<void>;
		onCancel: () => void;
		class?: string;
	}

	let {
		mode,
		initial,
		departments,
		saving,
		error = null,
		onsubmit,
		onCancel,
		class: className,
	}: Props = $props();

	const defaultValues: UserFormValues = {
		firstName: '',
		lastName: '',
		email: '',
		documentType: 'CC',
		documentNumber: '',
		phone: '',
		position: '',
		departmentId: null,
		role: 'EMPLEADO',
		active: true,
	};

	let values = $state<UserFormValues>({ ...defaultValues, ...initial });
	let errors = $state<UserFormErrors>({});
	let formError = $state('');

	const departmentValue = $derived(values.departmentId?.toString() ?? '');

	const selectClass =
		'border-input focus-visible:border-ring focus-visible:ring-ring/50 h-9 w-full rounded-md border bg-transparent px-3 py-1 text-sm shadow-xs focus-visible:ring-3 focus-visible:outline-none disabled:pointer-events-none disabled:opacity-50 aria-invalid:border-destructive';

	$effect(() => {
		if (error) {
			const mapped = applyBackendFieldErrors(error);
			formError = Object.keys(mapped).length === 0 ? error : '';
			for (const key of Object.keys(mapped) as (keyof UserFormValues)[]) {
				if (mapped[key]) errors[key] = mapped[key];
			}
		} else {
			formError = '';
		}
	});

	function clearFieldError(field: keyof UserFormValues) {
		if (errors[field]) errors[field] = '';
	}

	function handleDepartmentChange(event: Event) {
		const raw = (event.currentTarget as HTMLSelectElement).value;
		values.departmentId = raw ? Number(raw) : null;
		clearFieldError('departmentId');
	}

	async function handleSubmit() {
		if (saving) return;
		formError = '';
		const result = validateUserForm(mode, values);
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
	name: StringTextFieldKey,
	label: string,
	options: TextFieldOptions,
)}
	<div class="space-y-1.5">
		<label for={`user-${name}`} class="text-sm font-medium">
			{label}
		</label>
		<Input
			id={`user-${name}`}
			type={options.type ?? 'text'}
			placeholder={options.placeholder}
			disabled={options.disabled}
			bind:value={values[name]}
			oninput={() => clearFieldError(name)}
			aria-invalid={errors[name] ? 'true' : undefined}
			aria-describedby={errors[name] ? `user-${name}-error` : undefined}
		/>
		{#if errors[name]}
			<p id={`user-${name}-error`} class="text-destructive text-sm">
				{errors[name]}
			</p>
		{:else if options.hint}
			<p class="text-muted-foreground text-xs">{options.hint}</p>
		{/if}
	</div>
{/snippet}

{#snippet selectField(
	name: keyof UserFormValues,
	label: string,
	disabled: boolean | undefined,
)}
	<div class="space-y-1.5">
		<label for={`user-${name}`} class="text-sm font-medium">
			{label}
		</label>
		<select
			id={`user-${name}`}
			class={selectClass}
			{disabled}
			onchange={() => clearFieldError(name)}
			aria-invalid={errors[name] ? 'true' : undefined}
			aria-describedby={errors[name] ? `user-${name}-error` : undefined}
		>
			{#if name === 'documentType'}
				{#each DOCUMENT_TYPES as type (type)}
					<option value={type}>{DOCUMENT_TYPE_LABELS[type]}</option>
				{/each}
			{:else if name === 'role'}
				{#each USER_ROLES as role (role)}
					<option value={role}>{ROLE_LABELS[role]}</option>
				{/each}
			{/if}
		</select>
		{#if errors[name]}
			<p id={`user-${name}-error`} class="text-destructive text-sm">
				{errors[name]}
			</p>
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
		{@render textField('firstName', 'Nombre', { placeholder: 'Nombre' })}

		{@render textField('lastName', 'Apellido', { placeholder: 'Apellido' })}

		<div class="sm:col-span-2">
			{@render textField('email', 'Correo electrónico', {
				type: 'email',
				placeholder: 'nombre@empresa.com',
				disabled: mode === 'edit',
				hint:
					mode === 'edit'
						? 'El correo no se puede modificar.'
						: 'Con él iniciará sesión el usuario.',
			})}
		</div>

		{@render selectField('documentType', 'Tipo de documento', mode === 'edit')}

		{@render textField('documentNumber', 'Número de documento', {
			placeholder: 'Número de documento',
			disabled: mode === 'edit',
		})}

		{@render textField('phone', 'Teléfono', { placeholder: 'Teléfono' })}

		{@render textField('position', 'Cargo', { placeholder: 'Cargo' })}

		<div class="space-y-1.5">
			<label for="user-departmentId" class="text-sm font-medium">
				Departamento
			</label>
			<select
				id="user-departmentId"
				class={selectClass}
				value={departmentValue}
				onchange={handleDepartmentChange}
				aria-invalid={errors.departmentId ? 'true' : undefined}
				aria-describedby={errors.departmentId
					? 'user-departmentId-error'
					: undefined}
			>
				<option value="">Selecciona un departamento</option>
				{#each departments as department (department.id)}
					<option value={department.id} disabled={!department.active}>
						{department.name}
					</option>
				{/each}
			</select>
			{#if errors.departmentId}
				<p id="user-departmentId-error" class="text-destructive text-sm">
					{errors.departmentId}
				</p>
			{/if}
		</div>

		<div class="space-y-1.5">
			<label for="user-role" class="text-sm font-medium">Rol</label>
			<select
				id="user-role"
				class={selectClass}
				bind:value={values.role}
				onchange={() => clearFieldError('role')}
				aria-invalid={errors.role ? 'true' : undefined}
				aria-describedby={errors.role ? 'user-role-error' : undefined}
			>
				{#each USER_ROLES as role (role)}
					<option value={role}>{ROLE_LABELS[role]}</option>
				{/each}
			</select>
			{#if errors.role}
				<p id="user-role-error" class="text-destructive text-sm">
					{errors.role}
				</p>
			{/if}
		</div>

		{#if mode === 'edit'}
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
						Usuario activo
						<span class="text-muted-foreground block text-xs font-normal">
							Desactiva el usuario para impedir que inicie sesión.
						</span>
					</span>
				</label>
				{#if errors.active}
					<p class="text-destructive text-sm">{errors.active}</p>
				{/if}
			</div>
		{/if}
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
				{saving ? 'Creando usuario…' : 'Crear usuario'}
			{:else}
				{saving ? 'Guardando cambios…' : 'Guardar cambios'}
			{/if}
		</Button>
	</div>
</form>
