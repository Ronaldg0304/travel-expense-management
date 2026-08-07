<script lang="ts">
	import type { Snippet } from 'svelte';
	import { cn } from '$lib/utils';
	import ContentLayout from './ContentLayout.svelte';
	import PageContainer from './PageContainer.svelte';
	import Sidebar, { type NavSection } from './Sidebar.svelte';
	import Topbar from './Topbar.svelte';
	import type { BreadcrumbItem } from './Breadcrumb.svelte';

	type ContainerSize = 'sm' | 'default' | 'lg' | 'full';

	interface Props {
		sections?: NavSection[];
		breadcrumbs?: BreadcrumbItem[];
		topbarTitle?: string;
		brand?: Snippet;
		sidebarFooter?: Snippet;
		topbarActions?: Snippet;
		containerSize?: ContainerSize;
		showThemeToggle?: boolean;
		children: Snippet;
		class?: string;
	}

	let {
		sections = [],
		breadcrumbs = [],
		topbarTitle = '',
		brand,
		sidebarFooter,
		topbarActions,
		containerSize = 'default',
		showThemeToggle = true,
		children,
		class: className,
	}: Props = $props();

	let sidebarOpen = $state(false);
	let sidebarCollapsed = $state(false);

	function closeSidebar() {
		sidebarOpen = false;
	}
</script>

<div
	class={cn(
		'bg-background text-foreground flex h-dvh w-full overflow-hidden',
		className,
	)}
>
	<Sidebar
		{sections}
		{brand}
		footer={sidebarFooter}
		collapsed={sidebarCollapsed}
		open={sidebarOpen}
		onNavigate={closeSidebar}
		onOverlayClick={closeSidebar}
	/>
	<div class="flex min-w-0 flex-1 flex-col">
		<Topbar
			title={topbarTitle}
			{breadcrumbs}
			actions={topbarActions}
			collapsed={sidebarCollapsed}
			{showThemeToggle}
			onMenuClick={() => (sidebarOpen = true)}
			onToggleCollapse={() => (sidebarCollapsed = !sidebarCollapsed)}
		/>
		<ContentLayout>
			<PageContainer size={containerSize}>
				{@render children()}
			</PageContainer>
		</ContentLayout>
	</div>
</div>
