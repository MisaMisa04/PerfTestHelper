const ScenariosComponent = () => {

    const { userCtx } = React.useContext(AppContext)

    const [scenarios, setScenarios] = React.useState([])
    React.useEffect(() => {
        if (Cookies.get('jwt'))
            $.ajax({
                method: 'GET',
                url: `${PROTOCOL}://${HOST}:${PORT}${REQUESTS.getScenarios}`,
                contentType: 'application/json',
                dataType: 'json',
                headers: {
                    'Authorization': `Bearer ${Cookies.get('jwt')}`
                }
            })
                .done((response) => {
                    setScenarios(response)
                })
                .fail((jqXHR, textStatus, error) => {
                    console.error(error)
                    console.log('Статус:', textStatus)
                    console.log('Ответ сервера:', jqXHR.responseText)
                    console.log('Статус код:', jqXHR.status)
                    console.log('Заголовки:', jqXHR.getAllResponseHeaders())
                });
    }, [userCtx.userInfo]);

    const HandleNewScenarioOnClick = ((e) => {
        e.preventDefault();
        const newName = prompt('Enter new scenario name')
        if (newName) {
            $.ajax({
                method: 'POST',
                url: `${PROTOCOL}://${HOST}:${PORT}${REQUESTS.postNewScenario}?newScenarioName=${newName}`,
                contentType: 'application/json',
                dataType: 'json',
                headers: {
                    'Authorization': `Bearer ${Cookies.get('jwt')}`
                }
            })
                .done((response) => {
                    setScenarios([...scenarios, response])
                })
                .fail((jqXHR, textStatus, error) => {
                    console.error(error)
                    console.log('Статус:', textStatus)
                    console.log('Ответ сервера:', jqXHR.responseText)
                    console.log('Статус код:', jqXHR.status)
                    console.log('Заголовки:', jqXHR.getAllResponseHeaders())
                });
        }
    })

    return <div class="scenarios-container">
        <h4 class="main-scenarios-header">Список сценариев</h4>
        <button class="main-scenario-new" onClick={HandleNewScenarioOnClick}>Новый сценарий</button>
        {scenarios.map(scenario =>
            <ScenarioComponent key={scenario.id} scenarioData={scenario} />
        )}
    </div>
}

const ScenarioComponent = (props) => {

    const { scenarioCtx, operationCtx } = React.useContext(AppContext)

    const [scenarioData, setScenarioData] = React.useState(props.scenarioData)

    const [markDelete, setMarkDelete] = React.useState(false)

    const HandleOnClickScenario = (e) => {
        if($(e.target).hasClass('scenario-name'))
            scenarioCtx.setActiveScenario($(e.target).parent('.main-scenario').data("scenarioId"))
        else
            scenarioCtx.setActiveScenario($(e.target).data("scenarioId"))
        operationCtx.setActiveOperation(-1)
    }

    const HandleEditScenarioOnClick = (e) => {
        e.preventDefault();
        const newName = prompt('Enter new scenario name')
        if (newName) {
            $.ajax({
                method: 'POST',
                url: `${PROTOCOL}://${HOST}:${PORT}${REQUESTS.postRenameScenario}`,
                contentType: 'application/json',
                dataType: 'json',
                data: JSON.stringify({
                    id: scenarioData.id,
                    name: newName
                }),
                headers: {
                    'Authorization': `Bearer ${Cookies.get('jwt')}`
                }
            })
                .done((response) => {
                    setScenarioData({
                        id: scenarioData.id,
                        name: newName
                    })
                })
                .fail((jqXHR, textStatus, error) => {
                    console.error(error)
                    console.log('Статус:', textStatus)
                    console.log('Ответ сервера:', jqXHR.responseText)
                    console.log('Статус код:', jqXHR.status)
                    console.log('Заголовки:', jqXHR.getAllResponseHeaders())
                });
        }
    }

    const HandleDeleteScenarioOnClick = (e) => {
        e.preventDefault();
        if (confirm('Are you sure?')) {
            $.ajax({
                method: 'DELETE',
                url: `${PROTOCOL}://${HOST}:${PORT}${REQUESTS.deleteScenario}?scenarioId=${scenarioData.id}`,
                contentType: 'application/json',
                dataType: 'json',
                headers: {
                    'Authorization': `Bearer ${Cookies.get('jwt')}`
                }
            })
                .done((response) => {
                    if (response.success) {
                        if (scenarioData.id === scenarioCtx.activeScenario)
                            scenarioCtx.setActiveScenario(-1)
                        setMarkDelete(true)
                    }

                })
                .fail((jqXHR, textStatus, error) => {
                    console.error(error)
                    console.log('Статус:', textStatus)
                    console.log('Ответ сервера:', jqXHR.responseText)
                    console.log('Статус код:', jqXHR.status)
                    console.log('Заголовки:', jqXHR.getAllResponseHeaders())
                });

        }
    }

    const HandleMakeJsonOnClick = (e) => {
        e.preventDefault();
            $.ajax({
                method: 'GET',
                url: `${PROTOCOL}://${HOST}:${PORT}${REQUESTS.getScenarioJSON}?scenarioId=${scenarioData.id}`,
                contentType: 'application/json',
                dataType: 'json',
                headers: {
                    'Authorization': `Bearer ${Cookies.get('jwt')}`
                }
            })
                .done((response) => {
                    // Создаём Blob из текста
                    const blob = new Blob([JSON.stringify(response)], { type: 'application/json' });

                    // Создаём ссылку для скачивания
                    const url = window.URL.createObjectURL(blob);
                    const a = document.createElement('a');
                    a.style.display = 'none';
                    a.href = url;
                    a.download = `scenario-${scenarioData.id}.json`; // имя файла

                    // Добавляем ссылку в документ, кликаем и удаляем
                    document.body.appendChild(a);
                    a.click();
                    window.URL.revokeObjectURL(url);
                    document.body.removeChild(a);
                            })
                .fail((jqXHR, textStatus, error) => {
                    console.error(error)
                    console.log('Статус:', textStatus)
                    console.log('Ответ сервера:', jqXHR.responseText)
                    console.log('Статус код:', jqXHR.status)
                    console.log('Заголовки:', jqXHR.getAllResponseHeaders())
                    alert(JSON.parse(jqXHR.responseText).commentary)
                });

    }

    return (!markDelete && <div onClick={HandleOnClickScenario}
        className={`main-scenario ${scenarioCtx.activeScenario == scenarioData.id ? 'active' : ""}`}
        data-scenario-id={scenarioData.id}>
        <p className="scenario-name" >{scenarioData.name}</p>
        <div className="main-btn-container">
            <button className="main-btn" title="Выкачать как JSON" onClick={HandleMakeJsonOnClick}><img src="./ico/bracket-curly.png" alt="Выкачать как JSON" /></button>
            <button className="main-btn" title="Редактировать сценарий" onClick={HandleEditScenarioOnClick}><img src="./ico/free-icon-edit-tools-9801073.png" alt="Редактировать" /></button>
            <button className="main-btn" title="Удалить сценарий" onClick={HandleDeleteScenarioOnClick}><img src="./ico/free-icon-garbage-10221510.png" alt="Удалить" /></button>
        </div>
    </div>)
}