<script lang="ts">
	import { Badge } from '$lib/components/ui/badge';
	import type { SemanticColor } from '$lib/theme/tokens';
	import { cn } from '$lib/utils';

	type StatusVariant = SemanticColor | 'default' | 'secondary' | 'outline';

	interface Props {
		status: string;
		variant?: StatusVariant;
		dot?: boolean;
		class?: string;
	}

	let {
		status,
		variant = 'default',
		dot = false,
		class: className,
	}: Props = $props();

	const variantClasses: Record<StatusVariant, string> = {
		default: 'bg-primary text-primary-foreground',
		secondary: 'bg-secondary text-secondary-foreground',
		outline: 'border-border bg-background text-foreground',
		success: 'bg-success text-success-foreground',
		warning: 'bg-warning text-warning-foreground',
		destructive: 'bg-destructive text-destructive-foreground',
		info: 'bg-info text-info-foreground',
		neutral: 'bg-neutral text-neutral-foreground',
	};

	const dotClasses: Record<StatusVariant, string> = {
		default: 'bg-primary-foreground',
		secondary: 'bg-secondary-foreground',
		outline: 'bg-foreground/70',
		success: 'bg-success-foreground',
		warning: 'bg-warning-foreground',
		destructive: 'bg-destructive-foreground',
		info: 'bg-info-foreground',
		neutral: 'bg-neutral-foreground',
	};
</script>

<Badge class={cn('gap-1.5 font-medium', variantClasses[variant], className)}>
	{#if dot}
		<span
			class={cn('size-1.5 shrink-0 rounded-full', dotClasses[variant])}
			aria-hidden="true"
		></span>
	{/if}
	{status}
</Badge>
