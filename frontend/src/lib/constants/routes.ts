export const ROUTES = {
	auth: {
		login: '/login',
	},
	dashboard: {
		home: '/dashboard',
	},
	admin: {
		users: '/usuarios',
		departments: '/departamentos',
		costCenters: '/centros-de-costo',
		expenseTypes: '/tipos-de-gasto',
		accounts: '/cuentas',
		travelRequests: '/solicitudes-de-viaje',
		audit: '/auditoria',
	},
	employee: {
		myRequests: '/mis-solicitudes',
		newRequest: '/solicitud/nueva',
	},
	approver: {
		pendingRequests: '/solicitudes-pendientes',
		approvalHistory: '/historial-aprobaciones',
	},
	finance: {
		disbursements: '/desembolsos',
		settlements: '/legalizaciones',
		reimbursements: '/reembolsos',
		refunds: '/devoluciones',
	},
} as const;
