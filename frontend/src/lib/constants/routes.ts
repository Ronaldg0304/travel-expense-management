export const ROUTES = {
	auth: {
		login: '/login',
	},
	dashboard: {
		home: '/dashboard',
	},
	admin: {
		users: '/users',
		usersNew: '/users/new',
		departments: '/departamentos',
		costCenters: '/centros-de-costo',
		costCentersNew: '/centros-de-costo/new',
		expenseTypes: '/tipos-de-gasto',
		expenseTypesNew: '/tipos-de-gasto/new',
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
