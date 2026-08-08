<script lang="ts">
	import { SearchInput } from '$lib/components/common';
	import type { UserStatusFilter } from '$lib/models/user';
	import { cn } from '$lib/utils';

	const filterOptions: { value: UserStatusFilter; label: string }[] = [
		{ value: 'all', label: 'Todos los estados' },
		{ value: 'active', label: 'Activos' },
		{ value: 'inactive', label: 'Inactivos' },
	];

	interface Props {
		search?: string;
		activeFilter?: UserStatusFilter;
		class?: string;
	}

	let {
		search = $bindable(''),
		activeFilter = $bindable('all'),
		class: className,
	}: Props = $props();
</script>

<div class={cn('flex flex-col gap-3 sm:flex-row sm:items-center', className)}>
	<SearchInput
		bind:value={search}
		placeholder="Buscar por nombre, apellido o correo…"
		label="Buscar usuarios"
		class="w-full sm:max-w-xs"
	/>
	<select
		class="border-border bg-background h-9 w-full rounded-md border px-3 text-sm sm:w-auto"
		bind:value={activeFilter}
		aria-label="Filtrar por estado"
	>
		{#each filterOptions as option (option.value)}
			<option value={option.value}>{option.label}</option>
		{/each}
	</select>
</div>
