<script lang="ts">
	import { LoaderCircle, Power, PowerOff } from '@lucide/svelte';
	import { ApiError, EmptyState, LoadingState } from '$lib/components/feedback';
	import { StatusBadge } from '$lib/components/common';
	import { Button } from '$lib/components/ui/button';
	import * as Dialog from '$lib/components/ui/dialog';
	import type { DepartmentApproverSummary } from '$lib/models/department';
	import type { UserSummary } from '$lib/models/user';

	interface Props {
		open?: boolean;
		departmentName: string;
		approvers: DepartmentApproverSummary[];
		loading: boolean;
		error: unknown;
		activeUsers: UserSummary[];
		assigning: boolean;
		toggling: boolean;
		onAssign: (input: { userId: number; active: boolean }) => Promise<void>;
		onToggle: (approver: DepartmentApproverSummary) => Promise<void>;
		onRetry: () => void;
		onClose?: () => void;
	}

	let {
		open = $bindable(false),
		departmentName,
		approvers,
		loading,
		error,
		activeUsers,
		assigning,
		toggling,
		onAssign,
		onToggle,
		onRetry,
		onClose,
	}: Props = $props();

	const selectClass =
		'border-input focus-visible:border-ring focus-visible:ring-ring/50 h-9 w-full rounded-md border bg-transparent px-3 py-1 text-sm shadow-xs focus-visible:ring-3 focus-visible:outline-none disabled:pointer-events-none disabled:opacity-50 aria-invalid:border-destructive';

	let selectedUserId = $state('');
	let newApproverActive = $state(true);
	let formError = $state('');

	const assignedFullNames = $derived(
		new Set(approvers.map((approver) => approver.userFullName)),
	);

	const assignableUsers = $derived(
		activeUsers.filter(
			(user) =>
				!assignedFullNames.has(`${user.firstName} ${user.lastName}`),
		),
	);

	$effect(() => {
		if (open) {
			selectedUserId = '';
			newApproverActive = true;
			formError = '';
		} else {
			onClose?.();
		}
	});

	async function handleAssign() {
		if (assigning || toggling) return;
		formError = '';
		if (!selectedUserId) {
			formError = 'Selecciona un usuario.';
			return;
		}
		try {
			await onAssign({
				userId: Number(selectedUserId),
				active: newApproverActive,
			});
			selectedUserId = '';
			newApproverActive = true;
		} catch {
			// The page-level mutation surfaces the error via a toast.
		}
	}
</script>

<Dialog.Root bind:open>
	<Dialog.Content class="sm:max-w-lg">
		<Dialog.Header>
			<Dialog.Title>Aprobadores de {departmentName}</Dialog.Title>
			<Dialog.Description>
				Consulta y gestiona los aprobadores asignados a este departamento.
			</Dialog.Description>
		</Dialog.Header>

		{#if loading}
			<div class="border-border bg-background rounded-md border p-6">
				<LoadingState label="Cargando aprobadores…" />
			</div>
		{:else if error}
			<ApiError
				{error}
				title="No se pudieron cargar los aprobadores"
				onRetry={onRetry}
			/>
		{:else}
			<div class="space-y-5">
				{#if approvers.length === 0}
					<EmptyState
						title="Sin aprobadores asignados"
						description="Este departamento aún no tiene aprobadores asignados."
					/>
				{:else}
					<ul class="border-border divide-y rounded-md border">
						{#each approvers as approver (approver.id)}
							<li class="flex items-center justify-between gap-3 px-3 py-2.5">
								<div class="min-w-0">
									<p class="truncate text-sm font-medium">
										{approver.userFullName}
									</p>
								</div>
								<div class="flex shrink-0 items-center gap-2">
									{#if approver.active}
										<StatusBadge status="Activo" variant="success" />
									{:else}
										<StatusBadge status="Inactivo" variant="neutral" />
									{/if}
									<Button
										variant="ghost"
										size="sm"
										onclick={() => void onToggle(approver)}
										disabled={toggling || assigning}
									>
										{#if approver.active}
											<PowerOff class="text-destructive" aria-hidden="true" />
											Desactivar
										{:else}
											<Power class="text-success" aria-hidden="true" />
											Activar
										{/if}
									</Button>
								</div>
							</li>
						{/each}
					</ul>
				{/if}

				{#if assignableUsers.length === 0}
					<p class="text-muted-foreground text-xs">
						No hay usuarios activos disponibles para asignar como
						aprobadores.
					</p>
				{:else}
					<div class="border-border space-y-3 rounded-md border p-3">
						<p class="text-sm font-medium">Asignar aprobador</p>
						{#if formError}
							<p
								role="alert"
								class="bg-destructive/10 text-destructive rounded-md px-3 py-2 text-sm"
							>
								{formError}
							</p>
						{/if}
						<div class="space-y-1.5">
							<label for="approver-user" class="text-sm font-medium">
								Usuario
							</label>
							<select
								id="approver-user"
								class={selectClass}
								value={selectedUserId}
								onchange={(event) => {
									selectedUserId = (event.currentTarget as HTMLSelectElement)
										.value;
									formError = '';
								}}
							>
								<option value="">Selecciona un usuario</option>
								{#each assignableUsers as user (user.id)}
									<option value={user.id}>
										{user.firstName} {user.lastName} · {user.email}
									</option>
								{/each}
							</select>
						</div>
						<label class="flex items-start gap-2 text-sm font-medium">
							<input
								type="checkbox"
								bind:checked={newApproverActive}
								class="mt-0.5 size-4 rounded border"
							/>
							<span>
								Aprobador activo
								<span class="text-muted-foreground block text-xs font-normal">
									Un aprobador inactivo no podrá intervenir en las
									aprobaciones.
								</span>
							</span>
						</label>
						<Button
							onclick={handleAssign}
							disabled={assigning || toggling}
							class="w-full"
						>
							{#if assigning}
								<LoaderCircle class="size-4 animate-spin" aria-hidden="true" />
							{/if}
							{assigning ? 'Asignando…' : 'Asignar aprobador'}
						</Button>
					</div>
				{/if}
			</div>
		{/if}

		<Dialog.Footer class="flex-row justify-end gap-2">
			<Button
				variant="outline"
				onclick={() => (open = false)}
				disabled={assigning || toggling}
			>
				Cerrar
			</Button>
		</Dialog.Footer>
	</Dialog.Content>
</Dialog.Root>
