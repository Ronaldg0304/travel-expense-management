<script lang="ts">
	import { LoaderCircle } from '@lucide/svelte';
	import { getApiErrorMessage } from '$lib/api/api-error';
	import { Button } from '$lib/components/ui/button';
	import { Input } from '$lib/components/ui/input';
	import {
		ACCOUNT_TYPES,
		ACCOUNT_TYPE_LABELS,
		type AccountFormValues,
	} from '$lib/models/account';
	import type { UserSummary } from '$lib/models/user';
	import { cn } from '$lib/utils';
	import {
		applyAccountBackendFieldErrors,
		validateAccountForm,
		type AccountFormErrors,
	} from '$lib/validators/account.validator';

	type Mode = 'create' | 'edit';

	type TextFieldKey = 'bankName' | 'accountNumber' | 'accountHolderName';

	interface TextFieldOptions {
		type?: string;
		placeholder?: string;
		disabled?: boolean;
		hint?: string;
	}

	interface Props {
		mode: Mode;
		initial?: Partial<AccountFormValues>;
		users?: UserSummary[];
		saving: boolean;
		error?: string | null;
		onsubmit: (values: AccountFormValues) => Promise<void>;
		onCancel: () => void;
		class?: string;
	}

	let {
		mode,
		initial,
		users = [],
		saving,
		error = null,
		onsubmit,
		onCancel,
		class: className,
	}: Props = $props();

	const defaultValues: AccountFormValues = {
		userId: null,
		accountType: 'AHORROS',
		bankName: '',
		accountNumber: '',
		accountHolderName: '',
		active: true,
	};

	let values = $state<AccountFormValues>({ ...defaultValues, ...initial });
	let errors = $state<AccountFormErrors>({});
	let formError = $state('');

	const userIdValue = $derived(values.userId?.toString() ?? '');

	const selectClass =
		'border-input focus-visible:border-ring focus-visible:ring-ring/50 h-9 w-full rounded-md border bg-transparent px-3 py-1 text-sm shadow-xs focus-visible:ring-3 focus-visible:outline-none disabled:pointer-events-none disabled:opacity-50 aria-invalid:border-destructive';

	$effect(() => {
		if (error) {
			const mapped = applyAccountBackendFieldErrors(error);
			formError = Object.keys(mapped).length === 0 ? error : '';
			for (const key of Object.keys(
				mapped,
			) as (keyof AccountFormValues)[]) {
				if (mapped[key]) errors[key] = mapped[key];
			}
		} else {
			formError = '';
		}
	});

	function clearFieldError(field: keyof AccountFormValues) {
		if (errors[field]) errors[field] = '';
	}

	function handleUserChange(event: Event) {
		const raw = (event.currentTarget as HTMLSelectElement).value;
		values.userId = raw ? Number(raw) : null;
		clearFieldError('userId');
	}

	async function handleSubmit() {
		if (saving) return;
		formError = '';
		const result = validateAccountForm(mode, values);
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
		<label for={`account-${name}`} class="text-sm font-medium">
			{label}
		</label>
		<Input
			id={`account-${name}`}
			type={options.type ?? 'text'}
			placeholder={options.placeholder}
			disabled={options.disabled}
			bind:value={values[name]}
			oninput={() => clearFieldError(name)}
			aria-invalid={errors[name] ? 'true' : undefined}
			aria-describedby={errors[name] ? `account-${name}-error` : undefined}
		/>
		{#if errors[name]}
			<p id={`account-${name}-error`} class="text-destructive text-sm">
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
		{#if mode === 'create'}
			<div class="space-y-1.5">
				<label for="account-userId" class="text-sm font-medium">
					Titular de la cuenta
				</label>
				<select
					id="account-userId"
					class={selectClass}
					value={userIdValue}
					onchange={handleUserChange}
					aria-invalid={errors.userId ? 'true' : undefined}
					aria-describedby={errors.userId
						? 'account-userId-error'
						: undefined}
				>
					<option value="">Selecciona un usuario</option>
					{#each users as user (user.id)}
						<option value={user.id}>
							{user.firstName} {user.lastName}
						</option>
					{/each}
				</select>
				{#if errors.userId}
					<p id="account-userId-error" class="text-destructive text-sm">
						{errors.userId}
					</p>
				{/if}
			</div>
		{/if}

		<div class="space-y-1.5">
			<label for="account-accountType" class="text-sm font-medium">
				Tipo de cuenta
			</label>
			<select
				id="account-accountType"
				class={selectClass}
				bind:value={values.accountType}
				disabled={mode === 'edit'}
				onchange={() => clearFieldError('accountType')}
				aria-invalid={errors.accountType ? 'true' : undefined}
				aria-describedby={errors.accountType
					? 'account-accountType-error'
					: undefined}
			>
				{#each ACCOUNT_TYPES as type (type)}
					<option value={type}>{ACCOUNT_TYPE_LABELS[type]}</option>
				{/each}
			</select>
			{#if errors.accountType}
				<p
					id="account-accountType-error"
					class="text-destructive text-sm"
				>
					{errors.accountType}
				</p>
			{:else if mode === 'edit'}
				<p class="text-muted-foreground text-xs">
					El tipo de cuenta no se puede modificar.
				</p>
			{/if}
		</div>

		{@render textField('bankName', 'Banco', {
			placeholder: 'Nombre del banco',
		})}

		{@render textField('accountNumber', 'Número de cuenta', {
			placeholder: 'Número de cuenta',
			disabled: mode === 'edit',
			hint:
				mode === 'edit'
					? 'El número de cuenta no se puede modificar.'
					: undefined,
		})}

		<div class="sm:col-span-2">
			{@render textField('accountHolderName', 'Titular', {
				placeholder: 'Nombre del titular de la cuenta',
			})}
		</div>

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
					Cuenta activa
					<span class="text-muted-foreground block text-xs font-normal">
						{mode === 'edit'
							? 'Desactiva la cuenta para impedir su uso en desembolsos.'
							: 'Una cuenta activa puede usarse para los desembolsos.'}
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
				{saving ? 'Creando cuenta…' : 'Crear cuenta'}
			{:else}
				{saving ? 'Guardando cambios…' : 'Guardar cambios'}
			{/if}
		</Button>
	</div>
</form>
