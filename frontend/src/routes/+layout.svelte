<script lang="ts">
	import { QueryClientProvider } from '@tanstack/svelte-query';
	import { queryClient } from '$lib/config/queryClient';
	import { initTheme } from '$lib/theme';
	import '$styles/app.css';

	initTheme();

	let { children } = $props();
</script>

<QueryClientProvider client={queryClient}>
	{@render children()}
</QueryClientProvider>
