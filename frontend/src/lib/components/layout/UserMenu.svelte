<script lang="ts">
	import { ChevronDown, LogOut } from '@lucide/svelte';
	import { authService, ROLE_LABELS } from '$lib/auth';
	import {
		DropdownMenu,
		DropdownMenuContent,
		DropdownMenuItem,
		DropdownMenuLabel,
		DropdownMenuSeparator,
		DropdownMenuTrigger,
	} from '$lib/components/ui/dropdown-menu';
	import { buttonVariants } from '$lib/components/ui/button';
	import { user } from '$lib/stores';
	import { cn } from '$lib/utils';

	interface Props {
		class?: string;
	}

	let { class: className }: Props = $props();

	const initials = $derived.by(() => {
		if (!$user) return '';
		const first = $user.firstName[0] ?? '';
		const last = $user.lastName[0] ?? '';
		return `${first}${last}`.toUpperCase();
	});

	const roleLabels = $derived(
		($user?.roles ?? []).map((role) => ROLE_LABELS[role]),
	);

	async function handleLogout() {
		await authService.logout();
	}
</script>

{#if $user}
	<DropdownMenu>
		<DropdownMenuTrigger
			class={cn(
				buttonVariants({ variant: 'ghost' }),
				'focus-visible:ring-ring h-9 gap-2 rounded-full px-2 outline-none focus-visible:ring-2',
				className,
			)}
			aria-label="User menu"
		>
			<span
				class="bg-primary text-primary-foreground flex size-7 shrink-0 items-center justify-center rounded-full text-xs font-semibold"
				aria-hidden="true"
			>
				{initials}
			</span>
			<span class="hidden max-w-40 truncate text-sm font-medium sm:inline-flex">
				{$user.firstName}
				{$user.lastName}
			</span>
			<span class="text-muted-foreground hidden sm:block" aria-hidden="true">
				<ChevronDown class="size-4" />
			</span>
		</DropdownMenuTrigger>
		<DropdownMenuContent align="end" class="w-60">
			<DropdownMenuLabel class="flex flex-col gap-0.5 py-2">
				<span class="text-sm font-semibold">
					{$user.firstName}
					{$user.lastName}
				</span>
				<span class="text-muted-foreground truncate text-xs">
					{$user.email}
				</span>
			</DropdownMenuLabel>
			{#if roleLabels.length > 0}
				<div class="flex flex-wrap gap-1 px-2 pb-2">
					{#each roleLabels as label (label)}
						<span
							class="bg-secondary text-secondary-foreground rounded-full px-2 py-0.5 text-[10px] font-medium"
						>
							{label}
						</span>
					{/each}
				</div>
			{/if}
			<DropdownMenuSeparator />
			<DropdownMenuItem onselect={handleLogout} variant="destructive">
				<LogOut aria-hidden="true" />
				<span>Cerrar sesión</span>
			</DropdownMenuItem>
		</DropdownMenuContent>
	</DropdownMenu>
{/if}
