<script lang="ts">
	import type { Component, Snippet } from 'svelte';
	import { cn } from '$lib/utils';

	interface Props {
		title: string;
		description?: string;
		icon?: Component<{ class?: string }>;
		action?: Snippet;
		class?: string;
	}

	let { title, description, icon, action, class: className }: Props = $props();
</script>

<div
	class={cn(
		'flex flex-col items-center justify-center gap-2 py-12 text-center',
		className,
	)}
>
	{#if icon}
		{@const Icon = icon}
		<div
			class="bg-muted flex size-12 items-center justify-center rounded-full"
			aria-hidden="true"
		>
			<Icon class="text-muted-foreground size-6" />
		</div>
	{/if}
	<h3 class="text-foreground text-base font-semibold">{title}</h3>
	{#if description}
		<p class="text-muted-foreground max-w-sm text-sm">{description}</p>
	{/if}
	{#if action}
		<div class="mt-2">
			{@render action()}
		</div>
	{/if}
</div>
