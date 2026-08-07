import { browser } from '$app/environment';
import { get, writable } from 'svelte/store';
import { STORAGE_KEYS } from '$lib/constants/storage';

export type ThemePreference = 'light' | 'dark' | 'system';
export type ResolvedTheme = 'light' | 'dark';

const SYSTEM_DARK_QUERY = '(prefers-color-scheme: dark)';
const PREFERENCES: ThemePreference[] = ['light', 'dark', 'system'];

function isPreference(value: unknown): value is ThemePreference {
	return (
		typeof value === 'string' && PREFERENCES.includes(value as ThemePreference)
	);
}

function readPreference(): ThemePreference {
	if (!browser) return 'light';
	const stored = localStorage.getItem(STORAGE_KEYS.THEME);
	return isPreference(stored) ? stored : 'light';
}

function systemTheme(): ResolvedTheme {
	return window.matchMedia(SYSTEM_DARK_QUERY).matches ? 'dark' : 'light';
}

function applyTheme(theme: ResolvedTheme): void {
	const root = document.documentElement;
	root.classList.toggle('dark', theme === 'dark');
	root.style.colorScheme = theme;
}

function initialResolvedTheme(): ResolvedTheme {
	if (!browser) return 'light';
	const preference = readPreference();
	return preference === 'system' ? systemTheme() : preference;
}

export const themePreference = writable<ThemePreference>(readPreference());
export const resolvedTheme = writable<ResolvedTheme>(initialResolvedTheme());

export function setTheme(preference: ThemePreference): void {
	themePreference.set(preference);
	if (browser) {
		localStorage.setItem(STORAGE_KEYS.THEME, preference);
	}
	const resolved = preference === 'system' ? systemTheme() : preference;
	resolvedTheme.set(resolved);
	applyTheme(resolved);
}

export function cycleTheme(): void {
	const current = get(themePreference);
	const next =
		PREFERENCES[(PREFERENCES.indexOf(current) + 1) % PREFERENCES.length];
	setTheme(next);
}

export function initTheme(): void {
	if (!browser) return;
	applyTheme(get(resolvedTheme));
	const media = window.matchMedia(SYSTEM_DARK_QUERY);
	media.addEventListener('change', () => {
		if (get(themePreference) === 'system') {
			const resolved = systemTheme();
			resolvedTheme.set(resolved);
			applyTheme(resolved);
		}
	});
}
