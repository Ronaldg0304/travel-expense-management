import { resolve } from '$app/paths';
import type { ResolvedPathname } from '$app/types';

export function resolvePath(route: string): ResolvedPathname {
	return (resolve as unknown as (route: string) => ResolvedPathname)(route);
}
