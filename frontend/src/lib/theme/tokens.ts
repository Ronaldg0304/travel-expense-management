export const colorTokens = {
	primary: 'emerald',
	secondary: 'slate',
} as const;

export const semanticColors = [
	'success',
	'warning',
	'destructive',
	'info',
	'neutral',
] as const;
export type SemanticColor = (typeof semanticColors)[number];

export const spacing = {
	'0': '0',
	px: '1px',
	'0.5': '0.125rem',
	'1': '0.25rem',
	'1.5': '0.375rem',
	'2': '0.5rem',
	'2.5': '0.625rem',
	'3': '0.75rem',
	'3.5': '0.875rem',
	'4': '1rem',
	'5': '1.25rem',
	'6': '1.5rem',
	'7': '1.75rem',
	'8': '2rem',
	'9': '2.25rem',
	'10': '2.5rem',
	'11': '2.75rem',
	'12': '3rem',
	'14': '3.5rem',
	'16': '4rem',
	'20': '5rem',
	'24': '6rem',
	'28': '7rem',
	'32': '8rem',
	'36': '9rem',
	'40': '10rem',
	'44': '11rem',
	'48': '12rem',
	'52': '13rem',
	'56': '14rem',
	'60': '15rem',
	'64': '16rem',
	'72': '18rem',
	'80': '20rem',
	'96': '24rem',
} as const;

export type Spacing = keyof typeof spacing;

export const radius = {
	none: '0',
	sm: 'calc(var(--radius) * 0.6)',
	md: 'calc(var(--radius) * 0.8)',
	lg: 'var(--radius)',
	xl: 'calc(var(--radius) * 1.4)',
	'2xl': 'calc(var(--radius) * 1.8)',
	'3xl': 'calc(var(--radius) * 2.2)',
	'4xl': 'calc(var(--radius) * 2.6)',
	full: '9999px',
} as const;

export const elevation = {
	sm: 'shadow-elevation-sm',
	md: 'shadow-elevation-md',
	lg: 'shadow-elevation-lg',
} as const;

export type Elevation = keyof typeof elevation;

export const typography = {
	fontFamily: "'Inter Variable', 'Inter', ui-sans-serif, system-ui, sans-serif",
	scale: {
		'4xl': 'text-4xl',
		'3xl': 'text-3xl',
		'2xl': 'text-2xl',
		xl: 'text-xl',
		lg: 'text-lg',
		base: 'text-base',
		sm: 'text-sm',
		xs: 'text-xs',
	} as const,
} as const;

export const breakpoints = {
	sm: '640px',
	md: '768px',
	lg: '1024px',
	xl: '1280px',
	'2xl': '1536px',
} as const;
