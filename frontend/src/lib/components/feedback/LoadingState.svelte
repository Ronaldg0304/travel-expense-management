<script lang="ts">
	import { LoadingSpinner } from '$lib/components/common';
	import { cn } from '$lib/utils';

	interface Props {
		label?: string;
		class?: string;
	}

	let { label = 'Loading...', class: className }: Props = $props();
</script>

<div
	class={cn('flex flex-col items-center justify-center gap-2 py-12', className)}
	role="status"
>
	<LoadingSpinner class="size-6" {label} />
	<p class="text-muted-foreground text-sm">{label}</p>
</div>
