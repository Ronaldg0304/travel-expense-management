<script lang="ts">
	import { page } from '$app/state';
	import { QueryClientProvider } from '@tanstack/svelte-query';
	import { authStatus } from '$lib/auth';
	import { applyRouteGuard } from '$lib/auth/route-guard';
	import { Toaster } from '$lib/components/feedback';
	import { queryClient } from '$lib/config/queryClient';
	import { initTheme } from '$lib/theme';
	import '$styles/app.css';

	initTheme();

	let { children } = $props();

	$effect(() => {
		applyRouteGuard($authStatus, page.url.pathname);
	});
</script>

<QueryClientProvider client={queryClient}>
	{@render children()}
</QueryClientProvider>

<Toaster />
