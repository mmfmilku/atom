let pageData = atom.getParamsUrl()
const vmId = pageData.param.vmId

const vmInfo = {
    vmId: vmId,
    displayName: ''
}

let pageEdit = document.getElementById('page-edit')

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
                    <div onclick="genCode('${e}', this)" 
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
                    <div onclick="readText('${e.ordName}', this)" 
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
                `
                    <div onclick="readText('${e.ordName}', this, 'EXECUTE_ORD')" 
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
        '0': '<button onclick="saveText(\'BASE_ORD\')">保存</button>' +
            '<button onclick="executeOrd()">执行</button>',
        '1': '<button onclick="saveText(\'BASE_ORD\')">保存</button>' +
            '<button onclick="stopOrd()">还原</button>'
    },
    // 重写策略文件
    "STRATEGY_ORD": {
        type: 'strategy',
        '0': '<button onclick="saveText()">保存</button>'
    },
    // 控制台执行文件
    "EXECUTE_ORD": {
        type: 'executeConsole',
        '0': '<button onclick="executeGoal()">运行</button>'
    }
}
let setType = (ordEnum, prop = '0') => {
    pageEdit.querySelector('#ordFileText').readOnly = typeArr[ordEnum].readOnly
    pageEdit.querySelector('.edit-code-desc').innerHTML = typeArr[ordEnum][prop]
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

            // 文件标题反显
            pageEdit.querySelector('.edit-code-title').innerText = ordFileName
            setType(ordEnum, res.running)

            // 文件内容反显
            pageEdit.querySelector('#ordFileText').value = res.text
        })
}

let saveText = (ordEnum) => {
    if (pageEdit.querySelector('#ordFileText').readOnly) {
        UI.showError('不可编辑')
        return
    }
    let ordFileName = pageEdit.querySelector('.edit-code-title').innerText
    if (!ordFileName) {
        UI.showError('请先选择文件')
        return
    }
    let ordText = pageEdit.querySelector('#ordFileText').value
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
    post(`executeConsole/execute?appName=${vmInfo.displayName}&executeFile=${ordFileName}`)
        .then(res => {
            UI.showMessage(res.success)
        })
}

let loadAgent = () => {
    post(`agent/loadAgent?appName=${vmInfo.displayName}&vmId=${vmInfo.vmId}`)
        .then(res => {
            UI.showMessage(res)
            pageEdit.querySelector('.edit-left-bar-operate').classList.remove('hide')
            listClass()
        })
}


// 顶部按钮事件

let configView = () => {
    post(`config/getConfig?appName=${vmInfo.displayName}`)
        .then(data =>
            UI.openPageWin('/page/edit/configView', '设置', data.configData)
        ).then(formData =>
            post(`config/saveConfig?appName=${vmInfo.displayName}`, formData)
        ).then(res => UI.showMessage(res))
}

let nextJavaOffset = 1
let lastSearch = ''

// let listJavaFile = () => {
//     let dialog = UI.newDialog('<div class="javaList">'
//         + '<div class="flex-column"></div>'
//         + '<button>更多</button>'
//         + '<button>关闭</button>'
//         + '<input/>'
//         + '</div>'
//     )
//
//     let listDivDom = dialog.querySelector('.javaList > div')
//     listDivDom.addEventListener("click", event => {
//         doAddFile(event.target.innerText + '.java')
//         nextJavaOffset = 1
//         document.body.removeChild(dialog)
//     })
//
//     let searchDom = dialog.querySelector('input')
//
//     dialog.querySelectorAll('button')[0].addEventListener("click", () => {
//         if (lastSearch !== searchDom.value) {
//             // 重新搜索
//             lastSearch = searchDom.value
//             nextJavaOffset = 1
//             listDivDom.innerHTML = ''
//         }
//         if (!nextJavaOffset) {
//             UI.showMessage('没有更多了')
//             return;
//         }
//         post(`agent/listClass?appName=${vmInfo.displayName}&offset=${nextJavaOffset}&classShortNameLike=${lastSearch}`)
//             .then(res => {
//                 nextJavaOffset += (res.length || -nextJavaOffset)
//                 let showHtml = res.map(e =>
//                     `
//                     <div >${e}</div>
//                     `
//                 ).join('')
//                 listDivDom.innerHTML += showHtml
//             })
//     })
//     dialog.querySelectorAll('button')[1].addEventListener("click", () => {
//         nextJavaOffset = 1
//         document.body.removeChild(dialog)
//     })
//     dialog.querySelectorAll('button')[0].click()
// }

let genCode = (javaName, clickDom) => {
    // 文件选中
    let oldSelect = pageEdit.querySelector('.edit-file-select')
    oldSelect && oldSelect.classList.remove('edit-file-select')
    clickDom.classList.add('edit-file-select')
    post(`agent/genSource?appName=${vmInfo.displayName}&fullClassName=${javaName}`)
        .then(res => {
            // 文件标题反显
            setType(0)
            pageEdit.querySelector('.edit-code-title').innerText = javaName
            pageEdit.querySelector('#ordFileText').value = res
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
        pageEdit.querySelector('#loadAgentBtn').disabled = true
        pageEdit.querySelector('.edit-left-bar-operate').classList.remove('hide')
        listClass()
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
    contextmenu.style.left = event.pageX + 'px'
    contextmenu.style.top = event.pageY + 'px'
    contextmenu.style.display = 'block'
}

let openChildBlock = (event, blockHtml, parent) => {
    let childBlock = UI.showBlock(blockHtml)
    childBlock.style.left = parent.offsetLeft + event.target.offsetWidth + 'px'
    childBlock.style.top = parent.offsetTop + event.target.offsetTop + 'px'
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
document.addEventListener('contextmenu', function(event) {
    event.preventDefault()
    let rightClickEvent = event.target.getAttribute('rightClickEvent')
    rightClickEvent && rightClickEventMap[rightClickEvent](event)
})

// 隐藏菜单当用户点击其他地方
document.addEventListener('click', function(event) {
    clearRightMenu()
})

let clearRightMenu = () => {
    if (contextmenu) {
        let rightMenus = document.querySelectorAll('.right-menu')
        rightMenus && rightMenus.forEach(e => {
            e.remove()
        })
        // document.body.removeChild(contextmenu)
        contextmenu = null
    }
}

let clearRightChildMenu = () => {
    let rightChildMenus = document.querySelectorAll('.right-child-menu')
    rightChildMenus && rightChildMenus.forEach(e => {
        e.remove()
    })
}