<script lang="ts">
	import type { Snippet } from 'svelte';
	import { cn } from '$lib/utils';

	type ContainerSize = 'sm' | 'default' | 'lg' | 'full';

	interface Props {
		size?: ContainerSize;
		children: Snippet;
		class?: string;
	}

	let { size = 'default', children, class: className }: Props = $props();

	const maxWidth: Record<ContainerSize, string> = {
		sm: 'max-w-3xl',
		default: 'max-w-6xl',
		lg: 'max-w-7xl',
		full: 'max-w-none',
	};
</script>

<div
	class={cn(
		'mx-auto w-full px-4 py-6 sm:px-6 lg:px-8',
		maxWidth[size],
		className,
	)}
>
	{@render children()}
</div>
