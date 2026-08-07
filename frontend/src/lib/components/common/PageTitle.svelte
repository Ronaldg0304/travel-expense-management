<script lang="ts">
	import type { Snippet } from 'svelte';
	import { cn } from '$lib/utils';

	interface Props {
		level?: 1 | 2 | 3 | 4 | 5 | 6;
		class?: string;
		children: Snippet;
	}

	let { level = 1, class: className, children }: Props = $props();

	const styles = {
		1: 'text-3xl font-bold tracking-tight',
		2: 'text-2xl font-bold tracking-tight',
		3: 'text-xl font-semibold',
		4: 'text-lg font-semibold',
		5: 'text-base font-semibold',
		6: 'text-sm font-semibold',
	} as const;

	const tag = $derived(`h${level}`);
	const headingClass = $derived(cn(styles[level], className));
</script>

<svelte:element this={tag} class={headingClass}>
	{@render children()}
</svelte:element>
