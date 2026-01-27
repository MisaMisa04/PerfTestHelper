const MODE = { view: 'VIEW', login: 'LOGIN', register: 'REGISTER' }
const ACTION = { [MODE.login]: '/login', [MODE.register]: '/register' }

const HeaderComponent = () => {

    const [mode, setMode] = React.useState(MODE.view)
    const { userCtx: { userInfo, setUserInfo } } = React.useContext(AppContext);

    const HandleLogInPressed = () => {
        setMode(MODE.login)
    }

    const HandleRegisterPressed = () => {
        setMode(MODE.register)
    }

    const HandleLogoutPressed = () => {
        setUserInfo({ username: undefined, jwt: undefined })
        Cookies.remove('jwt')
    }


    React.useEffect(() => {
        if (Cookies.get('jwt'))
            $.ajax({
                method: 'GET',
                url: `${PROTOCOL}://${HOST}:${PORT}${REQUESTS.getUsername}`,
                contentType: 'application/json',
                headers: {
                    'Authorization': `Bearer ${Cookies.get('jwt')}`
                }
            })
                .done((response) => {
                    setUserInfo({ username: response, jwt: `${Cookies.get('jwt')}` })
                    setMode(MODE.view)
                })
                .fail((jqXHR, textStatus, error) => {
                    console.error(error)
                    console.log('Статус:', textStatus)
                    console.log('Ответ сервера:', jqXHR.responseText)
                    console.log('Статус код:', jqXHR.status)
                    console.log('Заголовки:', jqXHR.getAllResponseHeaders())
                });
    }, [])

    return [
        <p className="header-text">Hello, {userInfo.username ? userInfo.username : 'guest'}</p>,
        !userInfo.jwt && (<button className="header-btn" onClick={HandleLogInPressed}>Log in</button>),
        !userInfo.jwt && (<button className="header-btn" onClick={HandleRegisterPressed}>Register</button>),
        <UserCredsForm mode={mode} setMode={setMode} />,
        userInfo.jwt && (<button className="header-btn" onClick={HandleLogoutPressed}>Log out</button>),
    ]
}

const UserCredsForm = (props) => {

    const usernameRef = React.useRef(null)
    const passwordRef = React.useRef(null)

    const { userCtx: { userInfo, setUserInfo } } = React.useContext(AppContext);

    const HandleCloseClick = (e) => {
        e.preventDefault();
        props.setMode(MODE.view)
    }

    const HandleLoginClick = (e) => {
        e.preventDefault();
        const data = { username: usernameRef.current.value, password: passwordRef.current.value }
        $.ajax({
            method: 'POST',
            url: `${PROTOCOL}://${HOST}:${PORT}${REQUESTS.login}`,
            contentType: 'application/json',
            dataType: 'json',
            data: JSON.stringify(data)
        })
            .done((response) => {
                Cookies.set('jwt', response.token)
                setUserInfo({ username: usernameRef.current.value, jwt: response.token })
                props.setMode(MODE.view)
            })
            .fail((jqXHR, textStatus, error) => {
                console.error(error)
                console.log('Статус:', textStatus)
                console.log('Ответ сервера:', jqXHR.responseText)
                console.log('Статус код:', jqXHR.status)
                console.log('Заголовки:', jqXHR.getAllResponseHeaders())
            });
    }

    const HandleRegisterClick = (e) => {
        e.preventDefault();
        const data = { username: usernameRef.current.value, password: passwordRef.current.value }
        $.ajax({
            method: 'POST',
            url: `${PROTOCOL}://${HOST}:${PORT}${REQUESTS.register}`,
            contentType: 'application/json',
            dataType: 'json',
            data: JSON.stringify(data)
        })
            .done((response) => {
                Cookies.set('jwt', response.token)
                setUserInfo({ username: usernameRef.current.value, jwt: response.token })
                props.setMode(MODE.view)
            })
            .fail((jqXHR, textStatus, error) => {
                console.error(error)
                console.log('Статус:', textStatus)
                console.log('Ответ сервера:', jqXHR.responseText)
                console.log('Статус код:', jqXHR.status)
                console.log('Заголовки:', jqXHR.getAllResponseHeaders())
            });
    }

    return props.mode !== MODE.view && (<div className="user-creds-wrapper">
        <form class="user-creds" action={ACTION[props.mode]}>
            <button onClick={HandleCloseClick}>X</button>
            <input type="text" name="username" id="username" placeholder="Enter Name" ref={usernameRef} />
            <input type="password" name="password" id="password" placeholder="Enter Password" ref={passwordRef} />
            {props.mode === MODE.login && (<input type="submit" onClick={HandleLoginClick} value="Log In" />)}
            {props.mode === MODE.register && (<input type="submit" onClick={HandleRegisterClick} value="Register" />)}
            <input type="button" value="Enter through VKID" />
        </form>
    </div >)

}