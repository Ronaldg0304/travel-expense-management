<script lang="ts">
	import { Eye, EyeOff, LoaderCircle, Lock, Mail } from '@lucide/svelte';
	import { authService } from '$lib/auth';
	import { Button } from '$lib/components/ui/button';
	import { Input } from '$lib/components/ui/input';
	import { cn } from '$lib/utils';

	interface Props {
		class?: string;
	}

	let { class: className }: Props = $props();

	let email = $state('');
	let password = $state('');
	let showPassword = $state(false);
	let submitting = $state(false);

	let emailError = $state('');
	let passwordError = $state('');
	let formError = $state('');

	const EMAIL_PATTERN = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

	function validate(): boolean {
		emailError = '';
		passwordError = '';
		let valid = true;

		if (!email.trim()) {
			emailError = 'El correo electrónico es obligatorio.';
			valid = false;
		} else if (!EMAIL_PATTERN.test(email.trim())) {
			emailError = 'Introduce un correo electrónico válido.';
			valid = false;
		}

		if (!password) {
			passwordError = 'La contraseña es obligatoria.';
			valid = false;
		}

		return valid;
	}

	async function handleSubmit() {
		if (submitting) return;
		formError = '';
		if (!validate()) return;

		submitting = true;
		try {
			await authService.login({ email: email.trim(), password });
		} catch (error) {
			formError =
				error instanceof Error
					? error.message
					: 'No se pudo iniciar sesión. Inténtalo de nuevo.';
		} finally {
			submitting = false;
		}
	}
</script>

<form
	onsubmit={(event) => {
		event.preventDefault();
		void handleSubmit();
	}}
	novalidate
	class={cn('space-y-4', className)}
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
		<label for="login-email" class="text-sm font-medium">
			Correo electrónico
		</label>
		<div class="relative">
			<span
				class="text-muted-foreground absolute top-1/2 left-3 -translate-y-1/2"
				aria-hidden="true"
			>
				<Mail class="size-4" />
			</span>
			<Input
				id="login-email"
				type="email"
				autocomplete="email"
				placeholder="nombre@empresa.com"
				bind:value={email}
				class="ps-9"
				aria-invalid={emailError ? 'true' : undefined}
				aria-describedby={emailError ? 'login-email-error' : undefined}
			/>
		</div>
		{#if emailError}
			<p id="login-email-error" class="text-destructive text-sm">
				{emailError}
			</p>
		{/if}
	</div>

	<div class="space-y-1.5">
		<label for="login-password" class="text-sm font-medium"> Contraseña </label>
		<div class="relative">
			<span
				class="text-muted-foreground absolute top-1/2 left-3 -translate-y-1/2"
				aria-hidden="true"
			>
				<Lock class="size-4" />
			</span>
			<Input
				id="login-password"
				type={showPassword ? 'text' : 'password'}
				autocomplete="current-password"
				placeholder="••••••••"
				bind:value={password}
				class="ps-9 pe-10"
				aria-invalid={passwordError ? 'true' : undefined}
				aria-describedby={passwordError ? 'login-password-error' : undefined}
			/>
			<button
				type="button"
				onclick={() => (showPassword = !showPassword)}
				class="text-muted-foreground hover:text-foreground focus-visible:ring-ring absolute top-1/2 right-1.5 flex size-7 -translate-y-1/2 items-center justify-center rounded-md focus-visible:ring-2 focus-visible:outline-none"
				aria-label={showPassword ? 'Ocultar contraseña' : 'Mostrar contraseña'}
			>
				{#if showPassword}
					<EyeOff class="size-4" aria-hidden="true" />
				{:else}
					<Eye class="size-4" aria-hidden="true" />
				{/if}
			</button>
		</div>
		{#if passwordError}
			<p id="login-password-error" class="text-destructive text-sm">
				{passwordError}
			</p>
		{/if}
	</div>

	<Button type="submit" class="w-full" disabled={submitting}>
		{#if submitting}
			<LoaderCircle class="size-4 animate-spin" aria-hidden="true" />
			<span>Iniciando sesión…</span>
		{:else}
			<span>Iniciar sesión</span>
		{/if}
	</Button>
</form>
