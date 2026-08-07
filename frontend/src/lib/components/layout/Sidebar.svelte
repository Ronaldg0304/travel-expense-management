<script lang="ts">
	import { page } from '$app/state';
	import { resolve } from '$app/paths';
	import type { Component, Snippet } from 'svelte';
	import { Separator } from '$lib/components/ui/separator';
	import {
		Tooltip,
		TooltipContent,
		TooltipTrigger,
	} from '$lib/components/ui/tooltip';
	import { cn } from '$lib/utils';

	export interface NavItem {
		label: string;
		href: string;
		icon?: Component<{ class?: string }>;
		badge?: string;
		disabled?: boolean;
	}

	export interface NavSection {
		label?: string;
		items: NavItem[];
	}

	interface Props {
		sections?: NavSection[];
		brand?: Snippet;
		footer?: Snippet;
		collapsed?: boolean;
		open?: boolean;
		onNavigate?: () => void;
		onOverlayClick?: () => void;
		class?: string;
	}

	let {
		sections = [],
		brand,
		footer,
		collapsed = false,
		open = false,
		onNavigate = () => {},
		onOverlayClick = () => {},
		class: className,
	}: Props = $props();

	const path = $derived(page.url.pathname);

	let isDesktop = $state(false);

	$effect(() => {
		const media = window.matchMedia('(min-width: 64rem)');
		isDesktop = media.matches;
		const onChange = () => (isDesktop = media.matches);
		media.addEventListener('change', onChange);
		return () => media.removeEventListener('change', onChange);
	});

	function isActive(item: NavItem): boolean {
		const href = resolve(item.href as Parameters<typeof resolve>[0]);
		if (href === '/') return path === '/';
		return path === href || path.startsWith(`${href}/`);
	}

	const isHidden = $derived(!isDesktop && !open);
	const isCollapsed = $derived(collapsed && isDesktop);
</script>

<div class="relative z-50 lg:z-auto">
	{#if open}
		<div
			class="bg-foreground/40 fixed inset-0 z-40 backdrop-blur-sm lg:hidden"
			onclick={onOverlayClick}
			aria-hidden="true"
		></div>
	{/if}

	<aside
		class={cn(
			'border-sidebar-border bg-sidebar text-sidebar-foreground fixed inset-y-0 left-0 z-50 flex w-64 flex-col border-r transition-[transform,width] duration-200 ease-out lg:sticky lg:top-0 lg:h-dvh lg:translate-x-0',
			open ? 'translate-x-0' : '-translate-x-full',
			isCollapsed ? 'lg:w-16' : 'lg:w-64',
			className,
		)}
		aria-label="Sidebar"
		inert={isHidden}
	>
		{#if brand}
			<div
				class={cn(
					'border-sidebar-border flex h-14 shrink-0 items-center border-b px-4',
					isCollapsed && 'lg:justify-center lg:px-0',
				)}
			>
				{@render brand()}
			</div>
		{/if}

		<nav class="flex-1 overflow-y-auto px-3 py-4">
			{#each sections as section, i (section.label ?? i)}
				{#if section.label}
					<p
						class={cn(
							'text-muted-foreground mb-1 px-3 py-1.5 text-xs font-semibold tracking-wider uppercase',
							isCollapsed && 'lg:hidden',
						)}
					>
						{section.label}
					</p>
				{/if}
				<ul class="space-y-1">
					{#each section.items as item (item.href)}
						<li>
							{#if isCollapsed}
								<Tooltip>
									<TooltipTrigger>{@render itemLink(item)}</TooltipTrigger>
									<TooltipContent side="right">
										{item.label}
									</TooltipContent>
								</Tooltip>
							{:else}
								{@render itemLink(item)}
							{/if}
						</li>
					{/each}
				</ul>
				{#if i < sections.length - 1}
					<div class="my-3">
						<Separator />
					</div>
				{/if}
			{/each}
		</nav>

		{#if footer}
			<div class="border-sidebar-border shrink-0 border-t p-3">
				{@render footer()}
			</div>
		{/if}
	</aside>
</div>

{#snippet itemLink(item: NavItem)}
	<a
		href={resolve(item.href as Parameters<typeof resolve>[0])}
		onclick={onNavigate}
		class={cn(
			'group focus-visible:ring-ring flex items-center gap-2 rounded-md px-3 py-2 text-sm font-medium transition-colors focus-visible:ring-2 focus-visible:outline-none',
			isCollapsed && 'lg:justify-center lg:px-0',
			isActive(item)
				? 'bg-sidebar-accent text-sidebar-accent-foreground'
				: 'text-sidebar-foreground hover:bg-sidebar-accent/60 hover:text-sidebar-accent-foreground',
			item.disabled && 'pointer-events-none opacity-50',
		)}
		aria-current={isActive(item) ? 'page' : undefined}
		aria-disabled={item.disabled || undefined}
	>
		{#if item.icon}
			{@const Icon = item.icon}
			<span aria-hidden="true">
				<Icon class="size-4 shrink-0" />
			</span>
		{/if}
		<span class={cn('flex-1 truncate', isCollapsed && 'lg:hidden')}>
			{item.label}
		</span>
		{#if item.badge}
			<span
				class={cn(
					'bg-primary text-primary-foreground rounded-full px-1.5 py-0.5 text-[10px] font-semibold',
					isCollapsed && 'lg:hidden',
				)}
			>
				{item.badge}
			</span>
		{/if}
	</a>
{/snippet}
