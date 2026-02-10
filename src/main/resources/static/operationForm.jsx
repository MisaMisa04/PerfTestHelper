const MODE = { view: 0, edit: 1 }
const METHOD = { auto: 0, ctt: 1, threads: 2 }

const OperationFormComponent = () => {

    const { scenarioCtx, operationCtx } = React.useContext(AppContext)

    const [mode, setMode] = React.useState(MODE.view)

    const [operationData, setOperationData] = React.useState({})

    React.useEffect(() => {
        setMode(MODE.view)
        if (operationCtx.activeOperation > 0) {
            $.ajax({
                method: 'GET',
                url: `${PROTOCOL}://${HOST}:${PORT}${REQUESTS.getOperationData}?scenarioId=${scenarioCtx.activeScenario}&operationId=${operationCtx.activeOperation}`,
                contentType: 'application/json',
                dataType: 'json',
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
    }, [scenarioCtx.activeScenario, operationCtx.activeOperation])

    if (operationCtx.activeOperation > 0)
        return (
            <form class="operation-container">
                {mode === MODE.view && (<OperationInfoTableComponent operationData={operationData} setMode={setMode} />)}
                {mode === MODE.edit && (<OperationUpdateComponent operationData={operationData} setOperationData={setOperationData} setMode={setMode} />)}
            </form>
        )
    else if (scenarioCtx.activeScenario > 0)
        return (
            <div className="emptyBlock"><p>Выберите операцию для просмотра параметров</p></div>
        )
}

const OperationInfoTableComponent = (props) => {

    const HandleEditPressed = (e) => {
        e.preventDefault();
        props.setMode(MODE.edit)
    }

    return ([<h4 class="operation-card-header">Информация об операции</h4>, <button onClick={HandleEditPressed}>Редачить</button>,
    <table class="operation-info-table">
        <tbody>
            <tr>
                <td>RPS</td>
                <td>{props.operationData.rps}</td>
            </tr>
            <tr>
                <td>SLA</td>
                <td>{props.operationData.sla}</td>
            </tr>
            <tr>
                <td>CTT</td>
                <td>{props.operationData.ctt}</td>
            </tr>
            <tr>
                <td>Threads</td>
                <td>{props.operationData.threadsAmount}</td>
            </tr>
            <tr>
                <td>Распределена по генераторам</td>
                <td>
                    <input type="checkbox" name="" id="" checked={props.operationData.isDistributed} />
                </td>
            </tr>
            {props.operationData.isDistributed && (<tr>
                <td>Кол-во генераторов нагрузки для операции</td>
                <td>{props.operationData.gensAmount}</td>
            </tr>)}
        </tbody>
    </table>]
    )
}

const OperationUpdateComponent = (props) => {

    const { scenarioCtx, operationCtx } = React.useContext(AppContext)

    const [oldOperationData, setOldOperationData] = React.useState(props.operationData)

    React.useEffect(() => {
        setOldOperationData(props.operationData)
        console.log(props.operationData)
    }, [])

    const HandleCancelEditOnClick = (e) => {
        e.preventDefault()
        props.setOperationData(oldOperationData)
        props.setMode(MODE.view)
    }

    const HandleRpsChanged = (e) => {
        const newValue = e.target.value;
        if (/^\d*$/.test(newValue) || newValue === '') {
            props.setOperationData({ ...props.operationData, rps: newValue });
        }
    }

    const HandleSLAChanged = (e) => {
        const newValue = e.target.value;
        if (/^\d+\.?\d*$/.test(newValue) || newValue === '') {
            props.setOperationData({ ...props.operationData, sla: newValue });
        }
    }

    const HandleFormSubmit = (e) => {
        e.preventDefault()
        console.log(props.operationData)
        if (props.operationData.sla === null && props.operationData.calculateMethod == METHOD.auto) {
            alert('Для автоматического расчёта надо знать SLA')
            return
        }
        $.ajax({
            method: 'POST',
            url: `${PROTOCOL}://${HOST}:${PORT}${REQUESTS.postOperationData}?scenarioId=${scenarioCtx.activeScenario}&operationId=${operationCtx.activeOperation}`,
            contentType: 'application/json',
            dataType: 'json',
            data: JSON.stringify(props.operationData),
            headers: {
                'Authorization': `Bearer ${Cookies.get('jwt')}`
            }
        })
            .done((response) => {
                props.setMode(MODE.view)
                props.setOperationData(response)
            })
            .fail((jqXHR, textStatus, error) => {
                console.error(error)
                console.log('Статус:', textStatus)
                console.log('Ответ сервера:', jqXHR.responseText)
                console.log('Статус код:', jqXHR.status)
                console.log('Заголовки:', jqXHR.getAllResponseHeaders())
            });
    }

    return [<h4 class="operation-update-header">Обновление операции</h4>,
    <div class="operation-update-param">
        <label for="operation-rps-val">RPS: </label>
        <input type="text" name="operation-rps-val" id="" value={props.operationData.rps} onChange={HandleRpsChanged} />
    </div>,
    <div class="operation-update-param">
        <label for="operation-sla-val">SLA: </label>
        <input type="text" name="operation-sla-val" id="" value={props.operationData.sla} onChange={HandleSLAChanged} />
    </div>,
    <CalcMethodOptionsComponent operationData={props.operationData} setOperationData={props.setOperationData} />,
    <DistributedTestComponent operationData={props.operationData} setOperationData={props.setOperationData} />,
    <input type="submit" className="submit-calc-operation-btn" value="Рассчитать" onClick={HandleFormSubmit} />,
    <button onClick={HandleCancelEditOnClick}>Отменить редактирование</button>]
}

// this component was initially written by YandexGPT
const CalcMethodOptionsComponent = (props) => {

    // получение и простановка выбранного метода - через общий словарь operationData в пропсах

    const HandleCalcMethodOptionOnClick = (e) => {
        const method = e.target.getAttribute('data-calc-method');
        if (method) {
            props.setOperationData({ ...props.operationData, calculateMethod: method })
        }
    };

    const HandleCTTChange = (e) => {
        const newValue = e.target.value;
        if (/^\d+\.?\d*$/.test(newValue) || newValue === '') {
            props.setOperationData({ ...props.operationData, ctt: newValue });
        }
    }

    const HandleThreadsAmountChange = (e) => {
        const newValue = e.target.value;
        if (/^\d*$/.test(newValue) || newValue === '') {
            props.setOperationData({ ...props.operationData, threadsAmount: newValue });
        }
    }

    return (
        <div className="calc-method-options">
            <legend>Метод расчёта параметров</legend>

            <div onClick={HandleCalcMethodOptionOnClick} className="calc-method-option">
                <input
                    className="calcBy-Radio"
                    type="radio"
                    name="calcByRadio"
                    id="calcBy-Radio-Auto"
                    data-calc-method="0"
                    checked={props.operationData.calculateMethod == METHOD.auto}
                />
                <label onClick={(e) => e.stopPropagation()} htmlFor="calcBy-Radio-Auto">
                    Автоматически
                </label>
            </div>

            <div onClick={HandleCalcMethodOptionOnClick} className="calc-method-option">
                <input
                    className="calcBy-Radio"
                    type="radio"
                    name="calcByRadio"
                    id="calcBy-Radio-CTT"
                    data-calc-method="1"
                    checked={props.operationData.calculateMethod == METHOD.ctt}
                />
                <label onClick={(e) => e.stopPropagation()} htmlFor="calcBy-Radio-CTT">
                    По значению CTT
                </label>
                {props.operationData.calculateMethod == METHOD.ctt && (
                    <input
                        onClick={(e) => e.stopPropagation()}
                        onChange={HandleCTTChange}
                        className="calcBy-Text"
                        type="text"
                        name="calcByText"
                        id=""
                        value={props.operationData.ctt}
                    />
                )}
            </div>

            <div onClick={HandleCalcMethodOptionOnClick} className="calc-method-option">
                <input
                    className="calcBy-Radio"
                    type="radio"
                    name="calcByRadio"
                    id="calcBy-Radio-Threads"
                    data-calc-method="2"
                    checked={props.operationData.calculateMethod == METHOD.threads}
                />
                <label onClick={(e) => e.stopPropagation()} htmlFor="calcBy-Radio-Threads">
                    По количеству тредов
                </label>
                {props.operationData.calculateMethod == METHOD.threads && (
                    <input
                        onClick={(e) => e.stopPropagation()}
                        onChange={HandleThreadsAmountChange}
                        className="calcBy-Text"
                        type="text"
                        name="calcByText"
                        id=""
                        value={props.operationData.threadsAmount}
                    />
                )}
            </div>
        </div>
    );
}

// this component was initially written by YandexGPT
const DistributedTestComponent = (props) => {
    // Состояние для чекбокса и числового значения
    // через общий словарь props.operationData

    // Валидация ввода только целых чисел
    const HandleInputChange = (e) => {
        const value = e.target.value;
        if (/^\d*$/.test(value) || value === '') {
            props.setOperationData({ ...props.operationData, gensAmount: value })
        }
    };

    return (
        <div className="distributed-test-component">
            <div className="checkbox-wrapper">
                <input
                    type="checkbox"
                    id="distributed-load"
                    checked={props.operationData.isDistributed}
                    onChange={(e) => props.setOperationData({ ...props.operationData, isDistributed: e.target.checked })}
                />
                <label htmlFor="distributed-load">Распределённая нагрузка</label>
            </div>

            {props.operationData.isDistributed && (
                <div className="number-input-wrapper">
                    <input
                        type="text"
                        value={props.operationData.gensAmount}
                        onChange={HandleInputChange}
                        placeholder="Введите количество генераторов нагрузки"
                        className="number-input"
                    />
                </div>
            )}
        </div>
    );
};