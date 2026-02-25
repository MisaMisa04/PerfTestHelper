const AppContext = React.createContext()

const MainComponent = () => {

    const { scenarioCtx, operationCtx } = React.useContext(AppContext)

    React.useEffect(() => {
        const prevState = JSON.parse(localStorage.getItem('AppState'))
        if (prevState) {
            console.log(prevState)
            scenarioCtx.setActiveScenario(prevState.activeScenario)
            operationCtx.setActiveOperation(prevState.activeOperation)
            localStorage.removeItem('AppState')
        }
    }, [])

    return <main class="main">
        <div class="main-container">
            <ScenariosComponent />
            <div class="scenario-container">
                <OperationsComponent />
                <OperationFormComponent />
            </div>
        </div>
    </main>
}