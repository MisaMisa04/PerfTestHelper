const PROTOCOL = 'http'
const HOST = '127.0.0.1'
const PORT = '8080'
const REQUESTS = {
    getScenarios: "/api/v1/scenarios",
    getOperations: "/api/v1/operations",
    getOperationData: "/api/v1/operation",
    postOperationData: "/api/v1/operation",
    postNewOperation: "/api/v1/operations/new",
    postNewScenario: "/api/v1/scenarios/new",
    postRenameOperation: "/api/v1/rename_operation",
    postRenameScenario: "/api/v1/rename_scenario",
    deleteOperation: "/api/v1/operation",
    deleteScenario: "/api/v1/scenario",
    login: "/api/v1/login",
    register: "/api/v1/register",
    getUsername: "/api/v1/getUsername"
}