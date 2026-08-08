import type { SupportFileResponseDto } from '$lib/dto/support-file';
import type { SupportFile } from '$lib/models/support-file';

export function toSupportFile(dto: SupportFileResponseDto): SupportFile {
	return {
		id: dto.id,
		legalizationId: dto.legalizationId,
		originalFileName: dto.originalFileName,
		mimeType: dto.mimeType,
		fileSize: dto.fileSize,
		version: dto.version,
		uploadedAt: dto.uploadedAt,
	};
}
