<script lang="ts">
	import { goto } from '$app/navigation';
	import { resolve } from '$app/paths';
	import { hasRole } from '$lib/auth/permission';
	import { user } from '$lib/stores';

	let { children } = $props();

	$effect(() => {
		if ($user && !hasRole($user, 'ADMINISTRADOR')) {
			void goto(resolve('/(dashboard)'), { replaceState: true });
		}
	});
</script>

{@render children()}
