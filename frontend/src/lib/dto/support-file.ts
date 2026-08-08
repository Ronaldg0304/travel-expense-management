/**
 * Legalization support files endpoints — mirrors backend `SupportFileResponse`.
 *
 * POST /api/v1/legalizations/{legalizationId}/support-files (multipart `file`)
 * GET  /api/v1/legalizations/{legalizationId}/support-files
 * GET  /api/v1/legalizations/{legalizationId}/support-files/{fileId}/download
 */
export interface SupportFileResponseDto {
	id: number;
	legalizationId: number;
	originalFileName: string;
	storedFileName: string;
	mimeType: string;
	fileSize: number;
	version: number;
	active: boolean;
	uploadedAt: string;
}
