<script lang="ts">
	import {
		Menu,
		Monitor,
		Moon,
		PanelLeftClose,
		PanelLeftOpen,
		Sun,
	} from '@lucide/svelte';
	import type { Snippet } from 'svelte';
	import { Button } from '$lib/components/ui/button';
	import { cycleTheme, themePreference } from '$lib/theme';
	import { cn } from '$lib/utils';
	import Breadcrumb, { type BreadcrumbItem } from './Breadcrumb.svelte';

	interface Props {
		title?: string;
		breadcrumbs?: BreadcrumbItem[];
		actions?: Snippet;
		collapsed?: boolean;
		showThemeToggle?: boolean;
		onMenuClick?: () => void;
		onToggleCollapse?: () => void;
		class?: string;
	}

	let {
		title = '',
		breadcrumbs = [],
		actions,
		collapsed = false,
		showThemeToggle = true,
		onMenuClick,
		onToggleCollapse,
		class: className,
	}: Props = $props();
</script>

<header
	class={cn(
		'border-border bg-background/80 sticky top-0 z-30 flex h-14 shrink-0 items-center gap-2 border-b px-4 backdrop-blur-md sm:px-6',
		className,
	)}
>
	{#if onMenuClick}
		<Button
			variant="ghost"
			size="icon-sm"
			onclick={onMenuClick}
			class="lg:hidden"
			aria-label="Open navigation menu"
		>
			<Menu aria-hidden="true" />
		</Button>
	{/if}
	{#if onToggleCollapse}
		<Button
			variant="ghost"
			size="icon-sm"
			onclick={onToggleCollapse}
			class="hidden lg:inline-flex"
			aria-label={collapsed ? 'Expand sidebar' : 'Collapse sidebar'}
		>
			{#if collapsed}
				<PanelLeftOpen aria-hidden="true" />
			{:else}
				<PanelLeftClose aria-hidden="true" />
			{/if}
		</Button>
	{/if}
	{#if breadcrumbs.length > 0}
		<Breadcrumb items={breadcrumbs} class="hidden min-w-0 md:flex" />
	{:else if title}
		<h1 class="truncate text-sm font-semibold sm:text-base">{title}</h1>
	{/if}
	<div class="ml-auto flex items-center gap-2">
		{@render actions?.()}
		{#if showThemeToggle}
			<Button
				variant="ghost"
				size="icon-sm"
				onclick={cycleTheme}
				aria-label="Toggle color theme"
			>
				{#if $themePreference === 'dark'}
					<Moon aria-hidden="true" />
				{:else if $themePreference === 'system'}
					<Monitor aria-hidden="true" />
				{:else}
					<Sun aria-hidden="true" />
				{/if}
			</Button>
		{/if}
	</div>
</header>
