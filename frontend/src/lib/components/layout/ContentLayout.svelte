<script lang="ts">
	import type { Snippet } from 'svelte';
	import { cn } from '$lib/utils';

	interface Props {
		children: Snippet;
		class?: string;
	}

	let { children, class: className }: Props = $props();
</script>

<div class={cn('min-h-0 flex-1 overflow-y-auto', className)}>
	{@render children()}
</div>
