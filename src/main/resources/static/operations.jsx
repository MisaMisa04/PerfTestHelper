const OperationsComponent = () => {

    const { scenarioCtx, operationCtx } = React.useContext(AppContext)

    const [operations, setOperations] = React.useState([])

    React.useEffect(() => {
        if (scenarioCtx.activeScenario > 0) {
            $.ajax({
                method: 'GET',
                url: `${PROTOCOL}://${HOST}:${PORT}${REQUESTS.getOperations}?scenarioId=${scenarioCtx.activeScenario}`,
                contentType: 'application/json',
                dataType: 'json',
                headers: {
                    'Authorization': `Bearer ${Cookies.get('jwt')}`
                }
            })
                .done((response) => {
                    setOperations(response)
                })
                .fail((jqXHR, textStatus, error) => {
                    console.error(error)
                    console.log('Статус:', textStatus)
                    console.log('Ответ сервера:', jqXHR.responseText)
                    console.log('Статус код:', jqXHR.status)
                    console.log('Заголовки:', jqXHR.getAllResponseHeaders())
                });
        }
    }, [scenarioCtx.activeScenario])

    const HandleNewOperationOnClick = ((e) => {
        e.preventDefault();
        const newName = prompt('Enter new operation name')
        if (newName) {
            $.ajax({
                method: 'POST',
                url: `${PROTOCOL}://${HOST}:${PORT}${REQUESTS.postNewOperation}?newOperationName=${newName}&scenarioId=${scenarioCtx.activeScenario}`,
                contentType: 'application/json',
                dataType: 'json',
                headers: {
                    'Authorization': `Bearer ${Cookies.get('jwt')}`
                }
            })
                .done((response) => {
                    setOperations([...operations, response])
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

    if (scenarioCtx.activeScenario > 0)
        return (
            <div class="main-operations">
                <h4 class="main-operations-header">Список операций</h4>
                {operations.map(operation =>
                    <OperationComponent key={operation.id} operationData={operation} />
                )}
                <button class="main-operation-new" onClick={HandleNewOperationOnClick}>Новая операция</button>
            </div>
        )
    else
        return (
            <div className="emptyBlock"><p>Выберите сценарий для просмотра операций</p></div>
        )
}


const OperationComponent = (props) => {
    const { scenarioCtx, operationCtx } = React.useContext(AppContext)

    const [operationData, setOperationData] = React.useState(props.operationData)

    const [markDelete, setMarkDelete] = React.useState(false)

    const HandleOnClickOperation = (e) => {
        operationCtx.setActiveOperation(operationData.id)
    }

    const HandleEditOperationOnClick = (e) => {
        e.preventDefault();
        const newName = prompt('Enter new operation name')
        if (newName) {
            $.ajax({
                method: 'POST',
                url: `${PROTOCOL}://${HOST}:${PORT}${REQUESTS.postRenameOperation}`,
                contentType: 'application/json',
                dataType: 'json',
                data: JSON.stringify({
                    id: operationData.id,
                    name: newName
                }),
                headers: {
                    'Authorization': `Bearer ${Cookies.get('jwt')}`
                }
            })
                .done((response) => {
                    setOperationData(response)
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

    const HandleDeleteOperationOnClick = (e) => {
        e.preventDefault();
        if (confirm('Are you sure?')) {
            $.ajax({
                method: 'DELETE',
                url: `${PROTOCOL}://${HOST}:${PORT}${REQUESTS.deleteOperation}?operationId=${operationData.id}`,
                contentType: 'application/json',
                dataType: 'json',
                headers: {
                    'Authorization': `Bearer ${Cookies.get('jwt')}`
                }
            })
                .done((response) => {
                    if (response.success)
                        if (operationData.id === operationCtx.activeOperation)
                            operationCtx.setActiveOperation(-1)
                    setMarkDelete(true)
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

    return (!markDelete &&
        <div class="main-operation"
            className={`main-operation ${operationCtx.activeOperation == operationData.id ? 'active' : ""}`}
            data-operation-id={operationData.id}>
            <p onClick={HandleOnClickOperation}>{operationData.name}</p>
            <button onClick={HandleEditOperationOnClick}>Редачить</button>
            <button onClick={HandleDeleteOperationOnClick}>Удалить</button>
        </div>)
}