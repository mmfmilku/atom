function load() {

    let pageData = atom.getParamsUrl()
    const vmId = pageData.param.vmId
    let selectTypeEnum = ''

    const vmInfo = {
        vmId: vmId,
        displayName: ''
    }

    let pageEdit = document.getElementById('page-edit')
    // ------------------------------编辑器相关---------------------------
    let editorType = 'monaco'
    // 编辑器容器dom
    let editorContainer = pageEdit.querySelector('#editorContainer')
    // 编辑器操作对象
    let editor
    // 加载编辑器
    atom.SPA.loadHtml(`/page/edit/editor/${editorType}.html`, editorContainer)
        .then(html => {
            atom.SPA.router.loadJS(`/page/edit/editor/${editorType}.js`, editorContainer, () => {
                editor = load()
            })
        })
    // ------------------------------编辑器相关---------------------------

    let btnClickChange = clickDom => {
        if (!clickDom) return
        let className = 'button-container-select'
        let oldSelect = pageEdit.querySelector(`.${className}`)
        oldSelect && oldSelect.classList.remove(className)
        clickDom.classList.add(className)
    }

    let onlyShow = showClass => {
        let all = pageEdit.querySelectorAll('.edit-file-list')
        all && all.forEach(e => {
            e.classList.add('hide')
        })
        pageEdit.querySelector('.' + showClass).classList.remove('hide')
    }

    // 获取类列表
    let listClass = clickDom => {
        btnClickChange(clickDom)
        onlyShow('listClass')
        post(`agent/listAllClass?appName=${vmInfo.displayName}&offset=1`)
            .then(res => {
                let fileListDom = pageEdit.querySelector('.listClass')
                let showHtml = res.map(e =>
                    `
                        <div onclick="atomPage.genCode('${e}', this)"
                        rightClickEvent="classRightMenu"
                        class="edit-file text-wrap">${e}</div>
                        `
                ).join('')
                fileListDom.innerHTML = showHtml
            })

    }

    // 获取重写列表
    let listFile = clickDom => {
        btnClickChange(clickDom)
        onlyShow('listFile')
        post('config/listBaseOrd?appName=' + vmInfo.displayName)
            .then(res => {
                let fileListDom = pageEdit.querySelector('.listFile')
                fileListDom.innerHTML = res.map(e =>
                    `
                        <div onclick="atomPage.readText('${e.ordName}', this)"
                        rightClickEvent="fileRightMenu"
                        class="edit-file text-wrap ${e.running === '1' ? 'ord-running' : ''}">${e.ordName}</div>
                        `
                ).join('')
            })
    }

    // 获取策略列表
    let listStrategy = clickDom => {
        btnClickChange(clickDom)
        onlyShow('listStrategy')
        let fileListDom = pageEdit.querySelector('.listStrategy')
        fileListDom.innerHTML = '待支持'
    }

    // 可执行控制台
    let executeConsole = clickDom => {
        btnClickChange(clickDom)
        onlyShow('executeConsole')
        post('executeConsole/listExecuteOrd?appName=' + vmInfo.displayName)
            .then(res => {
                let fileListDom = pageEdit.querySelector('.executeConsole')
                fileListDom.innerHTML = res.map(e =>
                    e.ordEnum == 'EXECUTE_ORD'
                    ? `
                        <div onclick="atomPage.getTerminal('${e.ordName}', this)"
                        rightClickEvent="consoleRightMenu"
                        class="edit-file text-wrap">${e.ordName}</div>
                        `
                    :
                    `
                        <div onclick="atomPage.readText('${e.ordName}', this, '${e.ordEnum}')"
                        rightClickEvent="consoleRightMenu"
                        class="edit-file text-wrap">${e.ordName}</div>
                        `
                ).join('')
            })
    }

    let classToFile = () => {
        let ordFileName = contextTarget.innerText + '.java'
        // 避免已存在重写文件被覆盖，先查询
        post(`config/readOrd?appName=${vmInfo.displayName}&ordFileName=${ordFileName}&ordEnum=BASE_ORD`)
            .then(res => {
                if (res.text == null) {
                    UI.openConfirmDialog('新建重写' + ordFileName)
                        .then(() => {
                            // 首次新建，使用反编译源码
                            return post(`agent/genSource?appName=${vmInfo.displayName}&fullClassName=${contextTarget.innerText}`)
                        })
                        .then(text => {
                            btnClickChange(pageEdit.querySelector('.listFileBtn'))
                            doAddFile(ordFileName, 'BASE_ORD', text, listFile)
                        })
                } else {
                    btnClickChange(pageEdit.querySelector('.listFileBtn'))
                    // 已存在直接读取
                    readText(ordFileName)
                    listFile()
                }
            })

    }

    // 设置编辑类型，同时设置是否可编辑
    let typeArr = {
        // 类源码
        "0": {
            type: '',
            readOnly: true,
            '0': '类(只读)'
        },
        // 重写ord文件
        "BASE_ORD": {
            type: 'file',
            '0': '<button onclick="atomPage.saveText(\'BASE_ORD\')">保存</button>' +
                '<button onclick="atomPage.executeOrd()">执行</button>',
            '1': '<button onclick="atomPage.saveText(\'BASE_ORD\')">保存</button>' +
                '<button onclick="atomPage.stopOrd()">还原</button>'
        },
        // 重写策略文件
        "STRATEGY_ORD": {
            type: 'strategy',
            '0': '<button onclick="atomPage.saveText()">保存</button>'
        },
        // 控制台执行文件
        "EXECUTE_ORD": {
            type: 'executeConsole',
            '0': '<button onclick="atomPage.submitJTerminal()">提交</button>' +
                '<button onclick="atomPage.showJTerminalContext()">变量</button>'
        },
        // 脚本化执行文件
        "SCRIPT_ORD": {
            type: 'jScript',
            '0': '<button onclick="atomPage.saveText(\'SCRIPT_ORD\')">保存</button>' +
                '<button onclick="atomPage.executeGoal()">运行</button>'
        }
    }
    let setType = (ordEnum, prop = '0') => {
        editor.setReadOnly(typeArr[ordEnum].readOnly)
        pageEdit.querySelector('.edit-code-desc').innerHTML = typeArr[ordEnum][prop]
        selectTypeEnum = ordEnum
    }

    let addFile = (title = '', ordEnum, text = '', callBack) => {
        UI.openInputDialog(title)
            .then(ordFileName => {
                doAddFile(ordFileName, ordEnum, text, callBack)
            })
    }

    let doAddFile = (ordFileName, ordEnum, text = '', callBack) => {
        post(`config/writeOrd?appName=${vmInfo.displayName}&ordFileName=${ordFileName}&ordEnum=${ordEnum}`
            , {fileName: ordFileName, text: text}
        )
            .then(res => {
                UI.showMessage(res)
                readText(ordFileName, null, ordEnum)
                callBack && callBack()
                // listFile()
            })
    }

    let readText = (ordFileName, clickDom, ordEnum = 'BASE_ORD') => {
        post(`config/readOrd?appName=${vmInfo.displayName}&ordFileName=${ordFileName}&ordEnum=${ordEnum}`)
            .then(res => {
                // 文件选中
                let oldSelect = pageEdit.querySelector('.edit-file-select')
                oldSelect && oldSelect.classList.remove('edit-file-select')
                clickDom && clickDom.classList.add('edit-file-select')

                setType(ordEnum, res.running)
                // 文件标题反显,文件内容反显
                showOrdText(ordFileName, res.text)
            })
    }

    let saveText = (ordEnum) => {
        if (editor.getReadOnly()) {
            UI.showError('不可编辑')
            return
        }
        let ordFileName = pageEdit.querySelector('.edit-code-title').innerText
        if (!ordFileName) {
            UI.showError('请先选择文件')
            return
        }
        let ordText = editor.getText()
        post(`config/writeOrd?appName=${vmInfo.displayName}&ordFileName=${ordFileName}&ordEnum=${ordEnum}`
            , {fileName: ordFileName, text: ordText}
        )
            .then(res => {
                UI.showMessage(res)
            })
    }

    let deleteFile = (ordEnum, callBack) => {
        UI.openConfirmDialog('确认删除' + contextTarget.innerText)
            .then(() => {
                // TODO ordEnum从 contextTarget 获取
                post(`config/deleteOrd?appName=${vmInfo.displayName}&ordFileName=${contextTarget.innerText}&ordEnum=${ordEnum}`)
                    .then(res => {
                        UI.showMessage(res)
                        callBack && callBack()
                        // listFile()
                    })
            })
    }


    let executeOrd = () => {
        let ordFileName = pageEdit.querySelector('.edit-code-title').innerText
        if (!ordFileName) {
            UI.showMessage('请先选择文件')
            return
        }
        post(`agent/loadOrdFile?appName=${vmInfo.displayName}&ordFileName=${ordFileName}`)
            .then(res => {
                UI.showMessage(res)
                readText(ordFileName)
                listFile()
            })
    }

    let stopOrd = () => {
        let ordFileName = pageEdit.querySelector('.edit-code-title').innerText
        if (!ordFileName) {
            UI.showMessage('请先选择文件')
            return
        }
        let fullClassName = ordFileName.substring(0, ordFileName.length - 5)
        post(`agent/stopClassOrd?appName=${vmInfo.displayName}&fullClassName=${fullClassName}`)
            .then(res => {
                UI.showMessage(res)
                readText(ordFileName)
                listFile()
            })
    }

    let executeGoal = () => {
        let ordFileName = pageEdit.querySelector('.edit-code-title').innerText
        if (!ordFileName) {
            UI.showMessage('请先选择文件')
            return
        }
        post(`executeConsole/executeJScript?appName=${vmInfo.displayName}&jScriptFile=${ordFileName}`)
            .then(res => {
                if (res.success) {
                    UI.showMessage(res.executeReturn)
                } else {
                    let showError = res.errMsg
                    for (let i = 0; i < 5; i++) {
                        let stackTrace = res.throwable.stackTrace
                        if (stackTrace.length > i) {
                            let stackLine = stackTrace[i]
                            showError += `\n at ${stackLine.className}(${stackLine.fileName}:${stackLine.lineNumber})`
                        }
                    }

                    UI.showError(showError)
                }
            })
    }

    // 主文本展示区域
    let showOrdText = (title, text) => {
        // 标题
        pageEdit.querySelector('.edit-code-title').innerText = title
        // 内容
        editor.setText(text)
        editorContainer.style.height = ''
        // terminal部分移除
        pageEdit.querySelector('.terminal-box').style.height = ''
        pageEdit.querySelector('.terminal-box').innerHTML = ''
    }

    // ---------------terminal相关-------------- beg
    let showLine = (left, right) => {
        return `
            <div class="flex">
                <b style="flex:1;margin:5px 0px;padding: 5px 0px;">${left}</b>
                <div style="flex: 31;" class="terminal-his-line">${right}</div>
            </div>
            `
    }

    let terminalCodeShow = (code) => {
        return showLine('>', code)
    }

    let terminalResultShow = (res) => {
        if (res.success) {
            return showLine('<', res.executeReturn)
        } else {
            let showError = res.errMsg
            for (let i = 0; i < 5; i++) {
                let stackTrace = res.throwable.stackTrace
                if (stackTrace.length > i) {
                    let stackLine = stackTrace[i]
                    showError += `\n at ${stackLine.className}(${stackLine.fileName}:${stackLine.lineNumber})`
                }
            }
            // 异常展示
            return showLine('<span class="text-err">x</span>', `<span class="text-err">${showError}</span>`)
        }
    }

    let showTerminalText = (title, terminalInfo) => {
        let history = terminalInfo.history
        let resultHistory = terminalInfo.resultHistory
        // 标题
        pageEdit.querySelector('.edit-code-title').innerText = title
        // 内容
        editor.setText('')
        // 流程高度展示历史命令
        editorContainer.style.height = '24%'
        // terminal历史命令部分
        pageEdit.querySelector('.terminal-box').style.height = '70%'
        let lines = ''
        for (let i = 0;i < history.length;i++) {
            lines += terminalCodeShow(history[i])
            resultHistory[i] && (lines += terminalResultShow(resultHistory[i]))
        }
        pageEdit.querySelector('.terminal-box').innerHTML = lines
    }

    let curJTerminal;
    let getTerminal = (ordFileName, clickDom) => {
        curJTerminal = null
        post(`executeConsole/getTerminal?appName=${vmInfo.displayName}&terminalFile=${ordFileName}`)
            .then(res => {
                curJTerminal = res
                // 文件选中
                let oldSelect = pageEdit.querySelector('.edit-file-select')
                oldSelect && oldSelect.classList.remove('edit-file-select')
                clickDom && clickDom.classList.add('edit-file-select')

                // 文件标题反显
                setType('EXECUTE_ORD')
                showTerminalText(ordFileName, res)
            })
    }

    let submitJTerminal = () => {
        let input = editor.getText()
        if (!input) {
            return
        }
        post(`executeConsole/executeJTerminal?appName=${vmInfo.displayName}`,
            {
                id: curJTerminal.id,
                code: input
            })
            .then(res => {
                pageEdit.querySelector('.terminal-box').innerHTML += terminalCodeShow(input)
                pageEdit.querySelector('.terminal-box').innerHTML += terminalResultShow(res)
                pageEdit.querySelector('.terminal-box').scrollTop = pageEdit.querySelector('.terminal-box').scrollHeight
                // 上次内容清空
                editor.setText('')
            })
    }

    let showJTerminalContext = () => {
        let floatParent = pageEdit.querySelector('.terminal-box')
        if (!curJTerminal || floatParent.querySelector('.jTerminalContext')) {
            return
        }
        UI.openFloatWindow('/page/edit/jTerminalContext', '上下文变量', floatParent)
            .then(dom => {
                // 关闭浮窗事件
                dom.querySelector('.close').addEventListener('click', function(event) {
                    floatParent.removeChild(dom)
                })
                let context = dom.querySelector('.jTerminalContext')
                post(`executeConsole/getTerminalContext?appName=${vmInfo.displayName}&terminalId=${curJTerminal.id}`)
                    .then(res => {
                        // 内容
                        context.innerHTML = ''
                        for (let importItem of res.importList) {
                            context.innerHTML += showLine('', importItem)
                        }
                        for (let i in res.contextVarsType) {
                            let varType = res.contextVarsType[i]
                            let varName = i
                            let varValue = res.contextVars[i]
                            context.innerHTML += showLine('', `${varType} ${varName} = ${varValue};`)
                        }
                    })
            })
    }

    // ---------------terminal相关-------------- end

    let loadAgent = () => {
        post(`agent/loadAgent?appName=${vmInfo.displayName}&vmId=${vmInfo.vmId}`)
            .then(res => {
                UI.showMessage(res)
                pageEdit.querySelectorAll('.agent-run').forEach(e => e.classList.remove('hide'))
                pageEdit.querySelectorAll('.agent-stop').forEach(e => e.classList.add('hide'))
                listClass()
            })
    }

    let stopAgent = () => {
        post(`agent/stopAgent?appName=${vmInfo.displayName}&vmId=${vmInfo.vmId}`)
            .then(res => {
                UI.showMessage(res)
                if (res) {
                    setTimeout(() => {
                      // 终止成功，重写加载当前页面
                      atom.SPA.reload()
                    }, 1000)

                }
            })
    }

    // 顶部按钮事件
    let logTimeId = null
    this.beforeDestroy = () => {
        logTimeId && clearInterval(logTimeId)
    }
    let showLog = () => {
        if (logTimeId) {
            return
        }
        UI.openFloatWindow('/page/edit/logWindow', '日志', pageEdit)
            .then(dom => {
                console.log(dom)

                let logDom = dom.querySelector('.logWindow')
                let timeFunc = () => {
                    post(`agent/allScreenLogs?appName=${vmInfo.displayName}`)
                        .then(res => {
                            logDom.innerHTML = res.map(e => `<div>${e}</div>`).join('')
                            logDom.scrollTop = logDom.scrollHeight
                        })
                }
                timeFunc()
                if (!logTimeId) {
                    logTimeId = setInterval(timeFunc, 5000)
                }

                // 关闭浮窗事件
                dom.querySelector('.logClose').addEventListener('click', function(event) {
                    pageEdit.removeChild(dom)
                    logTimeId && clearInterval(logTimeId)
                    logTimeId = null
                })
            })
    }

    let configView = () => {
        post(`config/getConfig?appName=${vmInfo.displayName}`)
            .then(data =>
                UI.openPageWin('/page/edit/configView', '设置', data.configData)
            ).then(formData =>
                post(`config/saveConfig?appName=${vmInfo.displayName}`, formData)
            ).then(res => UI.showMessage(res))
    }

    let agentInfo = () => {
        post(`agent/agentInfo?appName=${vmInfo.displayName}`)
            .then(data => UI.openPageWin('/page/edit/agentInfo', '状态信息', data))
    }

    let nextJavaOffset = 1
    let lastSearch = ''

    let genCode = (javaName, clickDom) => {
        // 文件选中
        let oldSelect = pageEdit.querySelector('.edit-file-select')
        oldSelect && oldSelect.classList.remove('edit-file-select')
        clickDom.classList.add('edit-file-select')
        post(`agent/genSource?appName=${vmInfo.displayName}&fullClassName=${javaName}`)
            .then(res => {
                setType(0)
                // 文件标题反显,文件内容反显
                showOrdText(javaName, res)
            })
    }


    // -------------------------------初始化执行-----------------------------

    post('agent/vmInfo?vmId=' + vmId).then(res => {
        if (!res.vmId) {
            alert('进程不存在:' + vmId)
            return
        }
        vmInfo.displayName = res.displayName
        pageEdit.querySelector('.edit-title').innerText = vmInfo.displayName
        pageEdit.querySelector('.edit-operate-bar').classList.remove('hide')

        if (res.hasAgent) {
            // 已经连接过
            pageEdit.querySelectorAll('.agent-run').forEach(e => e.classList.remove('hide'))
            listClass()
        } else {
            pageEdit.querySelectorAll('.agent-stop').forEach(e => e.classList.remove('hide'))
        }
    })

    let contextmenu
    let fileRightMenu
    let classRightMenu
    let consoleRightMenu
    let consoleRightMenuNew

    // 用于保存右键菜单点击的目标对象dom
    let contextTarget

    atom.SPA.loadHtml('/page/edit/rightMenu/fileRightMenu.html')
        .then(html => {
            fileRightMenu = html
        })
    atom.SPA.loadHtml('/page/edit/rightMenu/classRightMenu.html')
        .then(html => {
            classRightMenu = html
        })
    atom.SPA.loadHtml('/page/edit/rightMenu/consoleRightMenu.html')
        .then(html => {
            consoleRightMenu = html
        })
    atom.SPA.loadHtml('/page/edit/rightMenu/consoleRightMenuNew.html')
        .then(html => {
            consoleRightMenuNew = html
        })

    let openBlock = (event, blockHtml) => {
        clearRightMenu()
        contextTarget = event.target
        contextmenu = UI.showBlock(blockHtml)
        contextmenu.style.left = event.clientX + 'px'
        contextmenu.style.top = event.clientY + 'px'
        contextmenu.style.display = 'block'
    }

    let openChildBlock = (event, blockHtml) => {
        let childBlock = UI.showBlock(blockHtml)
        childBlock.style.left = contextmenu.offsetLeft + event.target.offsetWidth + 'px'
        childBlock.style.top = contextmenu.offsetTop + event.target.offsetTop + 'px'
        childBlock.style.display = 'block'
    }

    /**
     * 右键菜单事件
     * 如 <div rightClickEvent="fileRightMenu"></div>
     * 右键将触发fileRightMenu对应事件
     * */
    let rightClickEventMap = {
        fileRightMenu: event => openBlock(event, fileRightMenu),
        classRightMenu: event => openBlock(event, classRightMenu),
        consoleRightMenu: event => openBlock(event, consoleRightMenu),
        // 无删除的右键菜单
        consoleRightMenuNew: event => openBlock(event, consoleRightMenuNew),
    }

    // 右键菜单事件
    pageEdit.addEventListener('contextmenu', function(event) {
        event.preventDefault()
        let rightClickEvent = event.target.getAttribute('rightClickEvent')
        rightClickEvent && rightClickEventMap[rightClickEvent](event)
    })

    // 隐藏菜单当用户点击其他地方
    pageEdit.addEventListener('click', function(event) {
        clearRightMenu()
    })

    let clearRightMenu = () => {
        if (contextmenu) {
            let rightMenus = document.querySelectorAll('.right-menu')
            rightMenus && rightMenus.forEach(e => {
                e.remove()
            })
            contextmenu = null
        }
    }

    let clearRightChildMenu = () => {
        let rightChildMenus = document.querySelectorAll('.right-child-menu')
        rightChildMenus && rightChildMenus.forEach(e => {
            e.remove()
        })
    }

    // 事件函数范围
    this.configView = configView
    this.showLog = showLog
    this.loadAgent = loadAgent
    this.stopAgent = stopAgent
    this.agentInfo = agentInfo
    this.listClass = listClass
    this.listFile = listFile
    this.listStrategy = listStrategy
    this.executeConsole = executeConsole
    this.classToFile = classToFile
    this.addFile = addFile
    this.clearRightChildMenu = clearRightChildMenu
    this.deleteFile = deleteFile
    this.openChildBlock = openChildBlock
    this.executeGoal = executeGoal
    this.showJTerminalContext = showJTerminalContext
    this.submitJTerminal = submitJTerminal
    this.stopOrd = stopOrd
    this.executeOrd = executeOrd
    this.saveText = saveText
    this.getTerminal = getTerminal
    this.readText = readText
    this.genCode = genCode

    pageEdit.addEventListener('click', (event) => {
        let actionClick = event.target.getAttribute('action-click')
        actionClick && this[actionClick](event.target)
    })

}