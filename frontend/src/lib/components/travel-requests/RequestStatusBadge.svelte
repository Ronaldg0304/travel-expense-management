<script lang="ts">
	import { StatusBadge } from '$lib/components/common';
	import {
		REQUEST_STATUS_LABELS,
		type RequestStatus,
	} from '$lib/models/travel-request';
	import type { SemanticColor } from '$lib/theme/tokens';

	const variantByStatus: Record<RequestStatus, SemanticColor> = {
		BORRADOR: 'neutral',
		ENVIADA: 'info',
		DEVUELTA: 'warning',
		APROBADA: 'success',
		RECHAZADA: 'destructive',
		DESEMBOLSADA: 'info',
		LEGALIZADA: 'success',
		LEGALIZACION_DEVUELTA: 'warning',
		VALIDADA: 'success',
		PENDIENTE_DEVOLUCION: 'warning',
		PENDIENTE_REEMBOLSO: 'warning',
		CERRADA: 'neutral',
	};

	let {
		status,
		class: className,
	}: {
		status: RequestStatus;
		class?: string;
	} = $props();
</script>

<StatusBadge
	status={REQUEST_STATUS_LABELS[status]}
	variant={variantByStatus[status]}
	class={className}
/>
