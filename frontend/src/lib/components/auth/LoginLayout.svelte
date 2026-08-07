<script lang="ts">
	import type { Snippet } from 'svelte';
	import { cn } from '$lib/utils';

	interface Props {
		title?: string;
		subtitle?: string;
		children: Snippet;
		class?: string;
	}

	let {
		title = '',
		subtitle = '',
		children,
		class: className,
	}: Props = $props();
</script>

<div
	class="bg-muted/40 flex min-h-dvh flex-col items-center justify-center px-4 py-8"
>
	<div class={cn('w-full max-w-sm', className)}>
		<div class="mb-6 flex flex-col items-center text-center">
			<span
				class="bg-primary text-primary-foreground mb-3 flex size-12 items-center justify-center rounded-xl text-2xl font-bold"
				aria-hidden="true"
			>
				$
			</span>
			{#if title}
				<h1 class="text-2xl font-bold tracking-tight">{title}</h1>
			{/if}
			{#if subtitle}
				<p class="text-muted-foreground mt-1.5 text-sm">{subtitle}</p>
			{/if}
		</div>
		<div class="bg-background border-border rounded-xl border p-6 shadow-sm">
			{@render children()}
		</div>
	</div>
</div>
