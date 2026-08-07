<script lang="ts">
	import { resolve } from '$app/paths';
	import { ChevronRight, Home } from '@lucide/svelte';

	export interface BreadcrumbItem {
		label: string;
		href?: string;
	}

	interface Props {
		items: BreadcrumbItem[];
		homeHref?: string;
		class?: string;
	}

	let { items, homeHref, class: className }: Props = $props();
</script>

<nav aria-label="Breadcrumb" class={className}>
	<ol class="flex flex-wrap items-center gap-1 text-sm">
		{#if homeHref}
			<li class="flex items-center gap-1">
				<a
					href={resolve(homeHref as Parameters<typeof resolve>[0])}
					class="text-muted-foreground hover:text-foreground focus-visible:ring-ring flex items-center rounded-sm transition-colors focus-visible:ring-2 focus-visible:outline-none"
					aria-label="Home"
				>
					<Home class="size-4" aria-hidden="true" />
				</a>
				<ChevronRight
					class="text-muted-foreground size-4 shrink-0"
					aria-hidden="true"
				/>
			</li>
		{/if}
		{#each items as item, i (item.label)}
			<li class="flex items-center gap-1">
				{#if item.href && i < items.length - 1}
					<a
						href={resolve(item.href as Parameters<typeof resolve>[0])}
						class="text-muted-foreground hover:text-foreground focus-visible:ring-ring truncate rounded-sm transition-colors focus-visible:ring-2 focus-visible:outline-none"
					>
						{item.label}
					</a>
				{:else}
					<span
						class="text-foreground truncate font-medium"
						aria-current={i === items.length - 1 ? 'page' : undefined}
					>
						{item.label}
					</span>
				{/if}
				{#if i < items.length - 1}
					<ChevronRight
						class="text-muted-foreground size-4 shrink-0"
						aria-hidden="true"
					/>
				{/if}
			</li>
		{/each}
	</ol>
</nav>
