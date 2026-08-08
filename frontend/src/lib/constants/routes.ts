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
		accountsNew: '/cuentas/new',
		travelRequests: '/solicitudes-de-viaje',
		travelRequestsNew: '/travel-requests/new',
		audit: '/auditoria',
	},
	employee: {
		myRequests: '/mis-solicitudes',
		newRequest: '/solicitud/nueva',
	},
	approver: {
		pendingRequests: '/solicitudes-pendientes',
		approvalHistory: '/historial-aprobaciones',
		approvals: '/approvals',
	},
	finance: {
		disbursements: '/disbursements',
		settlements: '/legalizations',
		reimbursements: '/reembolsos',
		refunds: '/devoluciones',
	},
} as const;
