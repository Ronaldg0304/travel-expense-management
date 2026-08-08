import type { AxiosProgressEvent } from 'axios';
import { api, request } from '$lib/api/axios';
import type { SupportFileResponseDto } from '$lib/dto/support-file';

class SupportFileService {
	private readonly resourcePath = '/v1/legalizations';

	list(legalizationId: number): Promise<SupportFileResponseDto[]> {
		return request<SupportFileResponseDto[]>({
			method: 'GET',
			url: `${this.resourcePath}/${legalizationId}/support-files`,
		});
	}

	upload(
		legalizationId: number,
		file: File,
		onProgress?: (percent: number) => void,
	): Promise<SupportFileResponseDto> {
		const formData = new FormData();
		formData.append('file', file);
		return request<SupportFileResponseDto>({
			method: 'POST',
			url: `${this.resourcePath}/${legalizationId}/support-files`,
			data: formData,
			headers: { 'Content-Type': null },
			timeout: 60_000,
			onUploadProgress: (event: AxiosProgressEvent) => {
				if (onProgress && event.total && event.total > 0) {
					onProgress(Math.round((event.loaded / event.total) * 100));
				}
			},
		});
	}

	async download(legalizationId: number, fileId: number): Promise<Blob> {
		const response = await api.get<Blob>(
			`${this.resourcePath}/${legalizationId}/support-files/${fileId}/download`,
			{ responseType: 'blob' },
		);
		return response.data;
	}
}

export const supportFileService = new SupportFileService();
