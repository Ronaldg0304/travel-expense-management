<script lang="ts">
	import { CircleCheck, CircleX } from '@lucide/svelte';
	import { Badge } from '$lib/components/ui/badge';
	import { cn } from '$lib/utils';

	let {
		active,
		class: className,
	}: {
		active: boolean;
		class?: string;
	} = $props();
</script>

{#if active}
	<Badge
		variant="secondary"
		data-icon="inline-start"
		class={cn('text-success', className)}
	>
		<CircleCheck aria-hidden="true" />
		Activo
	</Badge>
{:else}
	<Badge
		variant="secondary"
		data-icon="inline-start"
		class={cn('text-muted-foreground', className)}
	>
		<CircleX aria-hidden="true" />
		Inactivo
	</Badge>
{/if}
