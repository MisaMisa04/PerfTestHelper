const AppContext = React.createContext()

const AppComponent = () => {
    const [activeScenario, setActiveScenario] = React.useState(-1)
    const [activeOperation, setActiveOperation] = React.useState(-1)
    const [userInfo, setUserInfo] = React.useState({})

    React.useEffect(() => {
        console.log(userInfo)
    }, [])

    return <AppContext.Provider value={{
        scenarioCtx: { activeScenario, setActiveScenario },
        operationCtx: { activeOperation, setActiveOperation },
        userCtx: { userInfo, setUserInfo }
    }} >
        <header className="header">
            <HeaderComponent />
        </header>
        <main className="main">
            {userInfo.username ? (
                <div className="main-container">
                    <ScenariosComponent />
                    <div className="scenario-container">
                        <OperationsComponent />
                        <OperationFormComponent />
                    </div>
                </div>
            ) : (
                <div className="emptyBlock"><p>Войдите или зарегистрируйтесь</p></div>
            )}
        </main>
        <FooterComponent />
    </AppContext.Provider>
}

$(() => {
    ReactDOM.render(<AppComponent />, document.querySelector('.app'))
})