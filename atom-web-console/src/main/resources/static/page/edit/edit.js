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
    post(`agent/listClass?appName=${vmInfo.displayName}&offset=1`)
        .then(res => {
            let fileListDom = pageEdit.querySelector('.listClass')
            let showHtml = res.map(e =>
                `
                    <div onclick="genCode('${e}', this)" class="edit-file text-wrap">${e}</div>
                    `
            ).join('')
            fileListDom.innerHTML = showHtml
        })

}

// 获取重写列表
let listFile = clickDom => {
    btnClickChange(clickDom)
    onlyShow('listFile')
    post('config/listOrd?appName=' + vmInfo.displayName)
        .then(res => {
            let fileListDom = pageEdit.querySelector('.listFile')
            fileListDom.innerHTML = res.map(e =>
                `
                    <div onclick="readText('${e}', this)" class="edit-file text-wrap">${e}</div>
                    `
            ).join('')
        })
}

// 获取策略列表
let listStrategy = clickDom => {
    btnClickChange(clickDom)
    onlyShow('listStrategy')
    let fileListDom = pageEdit.querySelector('.listStrategy')
    fileListDom.innerHTML = 'fff'
}

let addFile = () => {
    UI.openInputDialog()
        .then(ordFileName => {
            doAddFile(ordFileName)
        })
}

let doAddFile = ordFileName => {
    post(`config/writeOrd?appName=${vmInfo.displayName}&ordFileName=${ordFileName}`
        , {fileName: ordFileName, text: ''}
    )
        .then(res => {
            UI.showMessage(res)
            listFile()
        })
}

let readText = (ordFileName, clickDom) => {
    post(`config/readOrd?appName=${vmInfo.displayName}&ordFileName=${ordFileName}`)
        .then(res => {
            // 文件选中
            let oldSelect = pageEdit.querySelector('.edit-file-select')
            oldSelect && oldSelect.classList.remove('edit-file-select')
            clickDom.classList.add('edit-file-select')

            // 文件标题反显
            pageEdit.querySelector('.edit-code-title').innerText = ordFileName
            pageEdit.querySelector('#ordFileText').readOnly = false

            // 文件内容反显
            pageEdit.querySelector('#ordFileText').value = res.text
        })
}

let saveText = () => {
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
    post(`config/writeOrd?appName=${vmInfo.displayName}&ordFileName=${ordFileName}`
        , {fileName: ordFileName, text: ordText}
    )
        .then(res => {
            UI.showMessage(res)
        })
}

let deleteFile = () => {
    if (pageEdit.querySelector('#ordFileText').readOnly) {
        UI.showError('不可编辑')
        return
    }
    let ordFileName = pageEdit.querySelector('.edit-code-title').innerText
    if (!ordFileName) {
        UI.showError('请先选择文件')
        return
    }
    UI.openConfirmDialog('确认删除' + ordFileName)
        .then(() => {
            post(`config/deleteOrd?appName=${vmInfo.displayName}&ordFileName=${ordFileName}`)
                .then(res => {
                    UI.showMessage(res)
                    listFile()
                })
        })
}


let loadFile = () => {
    let ordFileName = pageEdit.querySelector('.edit-code-title').innerText
    if (!ordFileName) {
        UI.showMessage('请先选择文件')
        return
    }
    post(`agent/loadOrdFile?appName=${vmInfo.displayName}&ordFileName=${ordFileName}`)
        .then(res => {
            UI.showMessage(res)
        })
}

let loadAgent = () => {
    post(`agent/loadAgent?appName=${vmInfo.displayName}&vmId=${vmInfo.vmId}`)
        .then(res => {
            UI.showMessage(res)
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

let genCode = (javaName) => {
    // let ordFileName = pageEdit.querySelector('.edit-code-title').innerText
    // if (!ordFileName || !ordFileName.endsWith('.java')) {
    //     UI.showMessage('请选择java文件')
    //     return
    // }
    post(`agent/genSource?appName=${vmInfo.displayName}&fullClassName=${javaName}`)
        .then(res => {
            // 文件标题反显
            pageEdit.querySelector('.edit-code-title').innerText = `${javaName} [readonly]`
            pageEdit.querySelector('#ordFileText').readOnly = true
            pageEdit.querySelector('#ordFileText').value = res
        })
}

// 初始化执行
post('agent/vmInfo?vmId=' + vmId).then(res => {
    if (!res.vmId) {
        alert('进程不存在:' + vmId)
        return
    }
    vmInfo.displayName = res.displayName
    pageEdit.querySelector('.edit-title').innerText = vmInfo.displayName

    listClass()
})

