<script lang="ts">
	import { getNavigationForRoles } from '$lib/navigation';
	import { ROUTES } from '$lib/constants/routes';
	import { ROLE_LABELS } from '$lib/auth';
	import { user } from '$lib/stores';
	import { PageHeader } from '$lib/components/common';
	import { Button } from '$lib/components/ui/button';

	const sections = $derived(
		getNavigationForRoles($user).filter(
			(section) => section.items.length > 0,
		),
	);
	const modules = $derived(
		sections.flatMap((section) =>
			section.items
				.filter((item) => item.href !== ROUTES.dashboard.home && !item.disabled)
				.map((item) => ({ section: section.label ?? 'General', item })),
		),
	);
	const roleLabel = $derived(
		$user?.roles.map((role) => ROLE_LABELS[role]).join(', ') ?? '',
	);
	const displayName = $derived(
		$user ? `${$user.firstName} ${$user.lastName}`.trim() : '',
	);
</script>

<svelte:head>
	<title>Dashboard</title>
</svelte:head>

<div class="space-y-6">
	<PageHeader
		title={$user ? `Hola, ${displayName}` : 'Dashboard'}
		description="Bienvenido al sistema de gestión de gastos de viaje."
	/>

	{#if $user}
		<div class="rounded-lg border bg-card p-4 text-card-foreground">
			<dl class="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
				<div>
					<dt class="text-sm font-medium text-muted-foreground">Nombre</dt>
					<dd class="mt-1 text-sm font-semibold">{displayName}</dd>
				</div>
				<div>
					<dt class="text-sm font-medium text-muted-foreground">Correo</dt>
					<dd class="mt-1 text-sm font-semibold break-all">{$user.email}</dd>
				</div>
				<div>
					<dt class="text-sm font-medium text-muted-foreground">Rol</dt>
					<dd class="mt-1 text-sm font-semibold">{roleLabel}</dd>
				</div>
			</dl>
		</div>
	{/if}

	{#if modules.length > 0}
		<section class="space-y-3">
			<h2 class="text-lg font-semibold">Módulos disponibles</h2>
			<div class="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
				{#each modules as module (module.item.href)}
					<a
						href={module.item.href}
						class="flex flex-col gap-2 rounded-lg border bg-card p-4 text-card-foreground transition-colors hover:border-primary/50 hover:bg-muted"
					>
						<span class="text-xs font-medium uppercase tracking-wide text-muted-foreground">
							{module.section}
						</span>
						<span class="font-semibold">{module.item.label}</span>
					</a>
				{/each}
			</div>
		</section>
	{/if}

	{#if !$user}
		<p class="text-sm text-muted-foreground">
			No hay una sesión iniciada. Inicia sesión para ver tus módulos.
		</p>
	{/if}

	<Button href={ROUTES.auth.login} variant="outline">
		Ir a inicio de sesión
	</Button>
</div>
