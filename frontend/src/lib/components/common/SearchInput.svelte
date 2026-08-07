<script lang="ts">
	import { Search, X } from '@lucide/svelte';
	import { Button } from '$lib/components/ui/button';
	import { Input } from '$lib/components/ui/input';
	import { cn } from '$lib/utils';

	interface Props {
		value?: string;
		placeholder?: string;
		label?: string;
		onChange?: (value: string) => void;
		class?: string;
	}

	let {
		value = $bindable(''),
		placeholder = 'Search...',
		label = 'Search',
		onChange,
		class: className,
	}: Props = $props();
</script>

<div class={cn('relative', className)}>
	<Search
		class="text-muted-foreground pointer-events-none absolute top-1/2 left-3 size-4 -translate-y-1/2"
		aria-hidden="true"
	/>
	<Input
		bind:value
		type="search"
		class="pr-9 pl-9"
		{placeholder}
		aria-label={label}
		oninput={(event) => onChange?.(event.currentTarget.value)}
	/>
	{#if value}
		<Button
			variant="ghost"
			size="icon-sm"
			class="absolute top-1/2 right-1 h-6 w-6 -translate-y-1/2"
			onclick={() => (value = '')}
			aria-label="Clear search"
		>
			<X class="size-3.5" aria-hidden="true" />
		</Button>
	{/if}
</div>
