const AppContext = React.createContext()

const AppComponent = () => {
    const [activeScenario, setActiveScenario] = React.useState(-1)
    const [activeOperation, setActiveOperation] = React.useState(-1)
    const [userInfo, setUserInfo] = React.useState({})

    React.useEffect(() => {
        console.log(userInfo)
    }, [userInfo])

    return <AppContext.Provider value={{
        scenarioCtx: { activeScenario, setActiveScenario },
        operationCtx: { activeOperation, setActiveOperation },
        userCtx: { userInfo, setUserInfo }
    }} >
        <header className="header">
            <HeaderComponent />
        </header>
        <main className="main">
            <div class="main-container">
                <ScenariosComponent />
                <div class="scenario-container">
                    <OperationsComponent />
                    <OperationFormComponent />
                </div>
            </div>
        </main>
        <footer className="footer"><p>Developed and produced by V.Koshkin. No rights reserved</p>
            <a href="https://www.flaticon.com/ru/free-icons/-" title="пользовательский интерфейс иконки">Пользовательский интерфейс иконки от Irfansusanto20 - Flaticon</a>
            <a href="https://www.flaticon.com/ru/free-icons/" title="удалить иконки">Удалить иконки от khulqi Rosyid - Flaticon</a>
        </footer>

    </AppContext.Provider>
}

$(() => {
    ReactDOM.render(<AppComponent />, document.querySelector('.app'))
})