<script lang="ts">
	import type { Snippet } from 'svelte';
	import { cn } from '$lib/utils';
	import PageTitle from './PageTitle.svelte';

	interface Props {
		title: string;
		description?: string;
		actions?: Snippet;
		level?: 1 | 2 | 3;
		class?: string;
	}

	let {
		title,
		description,
		actions,
		level = 1,
		class: className,
	}: Props = $props();
</script>

<header
	class={cn(
		'flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between',
		className,
	)}
>
	<div class="space-y-1">
		<PageTitle {level}>{title}</PageTitle>
		{#if description}
			<p class="text-muted-foreground text-sm">{description}</p>
		{/if}
	</div>
	{#if actions}
		<div class="flex flex-wrap items-center gap-2">
			{@render actions()}
		</div>
	{/if}
</header>
