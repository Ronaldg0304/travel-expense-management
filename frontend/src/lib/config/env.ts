import { env as publicEnv } from '$env/dynamic/public';
import { z } from 'zod';

const envSchema = z.object({
	PUBLIC_API_BASE_URL: z.string().min(1).default('/api'),
});

const parsed = envSchema.safeParse({
	PUBLIC_API_BASE_URL: publicEnv.PUBLIC_API_BASE_URL,
});

if (!parsed.success) {
	throw new Error(`Invalid environment variables: ${parsed.error.message}`);
}

export const env = parsed.data;
