<script lang="ts">
	import { LoaderCircle } from '@lucide/svelte';
	import { cn } from '$lib/utils';

	interface Props {
		label?: string;
		class?: string;
	}

	let { label = 'Loading', class: className }: Props = $props();
</script>

<span
	class={cn('inline-flex items-center justify-center', className)}
	role="status"
>
	<LoaderCircle class="size-5 animate-spin" aria-hidden="true" />
	<span class="sr-only">{label}</span>
</span>
