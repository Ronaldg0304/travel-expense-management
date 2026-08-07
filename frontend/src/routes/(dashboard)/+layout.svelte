<script lang="ts">
	import { page } from '$app/state';
	import { AppShell, UserMenu } from '$lib/components/layout';
	import {
		findActiveNavigationItem,
		getNavigationForRoles,
	} from '$lib/navigation';
	import { user } from '$lib/stores';

	const sections = $derived(getNavigationForRoles($user));
	const activeItem = $derived(
		findActiveNavigationItem(sections, page.url.pathname),
	);
	const topbarTitle = $derived(activeItem?.label ?? '');
	const breadcrumbs = $derived(activeItem ? [{ label: activeItem.label }] : []);

	let { children } = $props();
</script>

<AppShell {sections} {breadcrumbs} {topbarTitle} {brand} {topbarActions}>
	{@render children()}
</AppShell>

{#snippet brand()}
	<span class="flex min-w-0 items-center gap-2">
		<span
			class="text-primary flex size-8 shrink-0 items-center justify-center rounded-lg bg-current/10 text-xl font-bold"
		>
			$
		</span>
		<span class="truncate text-sm font-semibold">Gestión de Gastos</span>
	</span>
{/snippet}

{#snippet topbarActions()}
	<UserMenu />
{/snippet}
