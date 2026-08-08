import js from '@eslint/js';
import prettier from 'eslint-config-prettier';
import svelte from 'eslint-plugin-svelte';
import globals from 'globals';
import tseslint from 'typescript-eslint';

export default tseslint.config(
	{ ignores: ['node_modules', 'build', '.svelte-kit', '.vercel', '.netlify'] },
	js.configs.recommended,
	...tseslint.configs.recommended,
	...svelte.configs['flat/recommended'],
	prettier,
	{
		languageOptions: {
			globals: {
				...globals.browser,
				...globals.node,
			},
		},
	},
	{
		files: ['**/*.svelte'],
		languageOptions: {
			parserOptions: {
				parser: tseslint.parser,
			},
			globals: {
				...globals.browser,
			},
		},
	},
	{
		files: ['src/lib/components/ui/**/*.svelte'],
		rules: {
			'svelte/no-navigation-without-resolve': 'off',
		},
	},
	{
		// These modules route navigation through resolvePath(), which internally
		// calls $app/paths resolve(), so base-path handling is preserved.
		files: [
			'src/lib/api/error.interceptor.ts',
			'src/lib/auth/route-guard.ts',
			'src/lib/navigation/navigation.config.ts',
			'src/lib/components/layout/Breadcrumb.svelte',
			'src/lib/components/layout/Sidebar.svelte',
			'src/routes/(dashboard)/travel-requests/new/+page.svelte',
		],
		rules: {
			'svelte/no-navigation-without-resolve': 'off',
		},
	},
);
