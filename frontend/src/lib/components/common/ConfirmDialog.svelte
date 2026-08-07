<script lang="ts">
	import { LoaderCircle, TriangleAlert } from '@lucide/svelte';
	import type { Snippet } from 'svelte';
	import { Button } from '$lib/components/ui/button';
	import * as Dialog from '$lib/components/ui/dialog';
	import { cn } from '$lib/utils';

	interface Props {
		open?: boolean;
		title: string;
		description?: string;
		confirmLabel?: string;
		cancelLabel?: string;
		variant?: 'default' | 'destructive';
		showIcon?: boolean;
		onConfirm: () => void | Promise<void>;
		onCancel?: () => void;
		children?: Snippet;
		class?: string;
	}

	let {
		open = $bindable(false),
		title,
		description,
		confirmLabel = 'Confirm',
		cancelLabel = 'Cancel',
		variant = 'destructive',
		showIcon = true,
		onConfirm,
		onCancel,
		children,
		class: className,
	}: Props = $props();

	let confirming = $state(false);

	async function handleConfirm() {
		if (confirming) return;
		confirming = true;
		try {
			await onConfirm();
			open = false;
		} finally {
			confirming = false;
		}
	}

	function handleCancel() {
		open = false;
		onCancel?.();
	}
</script>

<Dialog.Root bind:open>
	{#if children}
		<Dialog.Trigger>{@render children()}</Dialog.Trigger>
	{/if}
	<Dialog.Content class={className}>
		<Dialog.Header class="flex-row items-start gap-4 text-left">
			{#if showIcon}
				<div
					class={cn(
						'flex size-10 shrink-0 items-center justify-center rounded-full',
						variant === 'destructive' ? 'bg-destructive/10' : 'bg-primary/10',
					)}
				>
					<TriangleAlert
						class={cn(
							'size-5',
							variant === 'destructive' ? 'text-destructive' : 'text-primary',
						)}
						aria-hidden="true"
					/>
				</div>
			{/if}
			<div>
				<Dialog.Title>{title}</Dialog.Title>
				{#if description}
					<Dialog.Description class="text-muted-foreground mt-1.5 text-sm">
						{description}
					</Dialog.Description>
				{/if}
			</div>
		</Dialog.Header>
		<Dialog.Footer class="flex-row justify-end gap-2">
			<Button variant="outline" onclick={handleCancel} disabled={confirming}>
				{cancelLabel}
			</Button>
			<Button {variant} onclick={handleConfirm} disabled={confirming}>
				{#if confirming}
					<LoaderCircle class="size-4 animate-spin" aria-hidden="true" />
				{/if}
				{confirmLabel}
			</Button>
		</Dialog.Footer>
	</Dialog.Content>
</Dialog.Root>
