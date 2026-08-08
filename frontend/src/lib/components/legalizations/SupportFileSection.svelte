<script lang="ts">
	import { Download, Paperclip, Upload } from '@lucide/svelte';
	import { ApiError, EmptyState, LoadingState } from '$lib/components/feedback';
	import { Button } from '$lib/components/ui/button';
	import type { SupportFile } from '$lib/models/support-file';
	import { formatDate, formatFileSize } from '$lib/utils';

	interface Props {
		files: SupportFile[];
		isPending: boolean;
		error?: unknown;
		onRetry?: () => void;
		uploading: boolean;
		uploadProgress: number;
		onUpload: (file: File) => void;
		onDownload: (file: SupportFile) => void;
	}

	let {
		files,
		isPending,
		error,
		onRetry,
		uploading,
		uploadProgress,
		onUpload,
		onDownload,
	}: Props = $props();

	let fileInput = $state<HTMLInputElement | null>(null);

	const progress = $derived(Math.min(100, Math.max(0, uploadProgress)));

	function handleFileChange(event: Event) {
		const input = event.currentTarget as HTMLInputElement;
		const file = input.files?.[0];
		if (!file) return;
		onUpload(file);
		input.value = '';
	}
</script>

<div class="border-border bg-background rounded-md border p-6">
	<div
		class="flex flex-col gap-2 sm:flex-row sm:items-center sm:justify-between"
	>
		<div>
			<h3 class="text-base font-semibold">Archivos de soporte</h3>
			<p class="text-muted-foreground text-sm">
				Adjunta los documentos que respaldan la legalización (PDF, JPEG o
				PNG, máx. 10 MB).
			</p>
		</div>
		{#if !uploading}
			<input
				bind:this={fileInput}
				class="sr-only"
				type="file"
				accept="application/pdf,image/jpeg,image/png"
				onchange={handleFileChange}
			/>
			<Button
				variant="outline"
				size="sm"
				onclick={() => fileInput?.click()}
			>
				<Upload aria-hidden="true" />
				Subir archivo
			</Button>
		{/if}
	</div>

	{#if uploading}
		<div class="mt-5 space-y-2">
			<div class="flex items-center justify-between text-sm">
				<span class="text-muted-foreground">Subiendo archivo…</span>
				<span class="font-medium">{progress}%</span>
			</div>
			<div
				class="bg-muted h-2 w-full overflow-hidden rounded-full"
				role="progressbar"
				aria-valuemin={0}
				aria-valuemax={100}
				aria-valuenow={progress}
			>
				<div
					class="bg-primary h-full rounded-full transition-all"
					style="width: {progress}%"
				></div>
			</div>
		</div>
	{/if}

	<div class="mt-5">
		{#if isPending}
			<LoadingState label="Cargando archivos…" />
		{:else if error}
			<ApiError
				error={error}
				title="No se pudieron cargar los archivos"
				onRetry={onRetry}
			/>
		{:else if files.length === 0}
			<EmptyState
				title="Sin archivos de soporte"
				description="Aún no hay documentos adjuntos para esta legalización."
				icon={Paperclip}
			/>
		{:else}
			<ul class="divide-border divide-y">
				{#each files as file (file.id)}
					<li
						class="flex items-center justify-between gap-4 py-3"
					>
						<div class="flex min-w-0 items-center gap-3">
							<Paperclip
								class="text-muted-foreground size-4 shrink-0"
								aria-hidden="true"
							/>
							<div class="min-w-0">
								<p class="truncate text-sm font-medium">
									{file.originalFileName}
								</p>
								<p class="text-muted-foreground text-xs">
									{formatFileSize(file.fileSize)} · {formatDate(file.uploadedAt)}
									{#if file.version > 1}
										· v{file.version}
									{/if}
								</p>
							</div>
						</div>
						<Button
							variant="ghost"
							size="sm"
							aria-label={`Descargar ${file.originalFileName}`}
							onclick={() => onDownload(file)}
						>
							<Download aria-hidden="true" />
							Descargar
						</Button>
					</li>
				{/each}
			</ul>
		{/if}
	</div>
</div>
