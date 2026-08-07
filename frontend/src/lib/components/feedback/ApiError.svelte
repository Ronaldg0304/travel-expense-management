<script lang="ts">
	import { TriangleAlert } from '@lucide/svelte';
	import type { Component, Snippet } from 'svelte';
	import { getApiErrorMessage } from '$lib/api/api-error';
	import { Button } from '$lib/components/ui/button';
	import { cn } from '$lib/utils';

	interface Props {
		error?: unknown;
		title?: string;
		message?: string;
		showIcon?: boolean;
		retryLabel?: string;
		onRetry?: () => void | Promise<void>;
		action?: Snippet;
		icon?: Component<{ class?: string }>;
		class?: string;
	}

	let {
		error,
		title = 'Something went wrong',
		message,
		showIcon = true,
		retryLabel = 'Try again',
		onRetry,
		action,
		icon,
		class: className,
	}: Props = $props();

	const errorMessage = $derived(
		message ?? (error != null ? getApiErrorMessage(error) : undefined),
	);
</script>

<div
	class={cn(
		'flex flex-col items-center justify-center gap-2 py-10 text-center',
		className,
	)}
	role="alert"
>
	{#if showIcon}
		{@const Icon = icon ?? TriangleAlert}
		<div
			class="bg-destructive/10 flex size-12 items-center justify-center rounded-full"
			aria-hidden="true"
		>
			<Icon class="text-destructive size-6" />
		</div>
	{/if}
	<h3 class="text-foreground text-base font-semibold">{title}</h3>
	{#if errorMessage}
		<p class="text-muted-foreground max-w-sm text-sm">{errorMessage}</p>
	{/if}
	{#if action}
		<div class="mt-2">
			{@render action()}
		</div>
	{:else if onRetry}
		<div class="mt-2">
			<Button variant="outline" size="sm" onclick={onRetry}>
				{retryLabel}
			</Button>
		</div>
	{/if}
</div>
