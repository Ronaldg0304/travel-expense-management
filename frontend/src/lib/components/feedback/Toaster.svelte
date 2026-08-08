<script lang="ts">
	import { CircleAlert, CircleCheck, Info, X } from '@lucide/svelte';
	import type { Component } from 'svelte';
	import { Button } from '$lib/components/ui/button';
	import { dismissToast, toasts, type ToastType } from './toast.store';
	import { cn } from '$lib/utils';

	const iconByType: Record<ToastType, Component<Record<string, unknown>>> = {
		success: CircleCheck,
		error: CircleAlert,
		info: Info,
	};

	const iconClassByType: Record<ToastType, string> = {
		success: 'text-success',
		error: 'text-destructive',
		info: 'text-info',
	};
</script>

{#if $toasts.length > 0}
	<div
		class="pointer-events-none fixed right-4 bottom-4 z-50 flex w-full max-w-sm flex-col gap-2"
		role="status"
		aria-live="polite"
	>
		{#each $toasts as toast (toast.id)}
			{@const Icon = iconByType[toast.type]}
			<div
				class="border-border bg-background pointer-events-auto flex items-start gap-3 rounded-md border p-3 shadow-lg"
			>
				<Icon
					class={cn('mt-0.5 size-5 shrink-0', iconClassByType[toast.type])}
					aria-hidden="true"
				/>
				<div class="min-w-0 flex-1">
					<p class="text-foreground text-sm font-medium">{toast.title}</p>
					{#if toast.description}
						<p class="text-muted-foreground mt-0.5 text-sm">
							{toast.description}
						</p>
					{/if}
				</div>
				<Button
					variant="ghost"
					size="icon-xs"
					class="shrink-0"
					onclick={() => dismissToast(toast.id)}
					aria-label="Cerrar notificación"
				>
					<X class="size-4" aria-hidden="true" />
				</Button>
			</div>
		{/each}
	</div>
{/if}
