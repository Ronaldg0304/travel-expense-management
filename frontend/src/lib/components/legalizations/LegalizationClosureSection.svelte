<script lang="ts">
	import { LoaderCircle, Lock } from '@lucide/svelte';
	import { Button } from '$lib/components/ui/button';
	import { RequestStatusBadge } from '$lib/components/travel-requests';
	import type { RequestStatus } from '$lib/models/travel-request';

	interface Props {
		status: RequestStatus;
		closing: boolean;
		onClose: () => void;
	}

	let { status, closing, onClose }: Props = $props();
</script>

<div class="border-border bg-background rounded-md border p-6">
	<div
		class="flex flex-col gap-2 sm:flex-row sm:items-center sm:justify-between"
	>
		<div>
			<h3 class="text-base font-semibold">Cierre de legalización</h3>
			<p class="text-muted-foreground text-sm">
				La legalización está lista para cerrarse. El sistema validará el
				estado antes de cerrarla.
			</p>
		</div>
		<RequestStatusBadge status={status} />
	</div>

	<div
		class="border-border mt-5 flex flex-col gap-3 border-t pt-4 sm:flex-row sm:items-center sm:justify-between"
	>
		<p class="text-muted-foreground text-sm">
			El cierre es definitivo y no se puede deshacer. Al cerrar, la solicitud
			pasa a estado Cerrada.
		</p>
		<Button onclick={onClose} disabled={closing}>
			{#if closing}
				<LoaderCircle class="size-4 animate-spin" aria-hidden="true" />
			{:else}
				<Lock aria-hidden="true" />
			{/if}
			Cerrar legalización
		</Button>
	</div>
</div>
