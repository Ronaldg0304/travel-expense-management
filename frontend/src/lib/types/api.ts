/**
 * Backend `ApiResponse` contract (mirrors
 * `com.demo.travel_expense_management.common.response.ApiResponse`).
 */
export interface ApiResponse<T> {
	success: boolean;
	message: string | null;
	data: T;
	timestamp: string;
}

/** Error payload produced by the backend `GlobalExceptionHandler`. */
export interface ApiErrorResponse {
	success: false;
	message: string | null;
	data: null;
	timestamp: string;
}
