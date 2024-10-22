let pageData = atom.getParamsUrl()
const vmId = pageData.param.vmId

const vmInfo = {
    vmId: vmId,
    displayName: ''
}

let pageEdit = document.getElementById('page-edit')

let listFile = () => {
    // 获取文件列表
    post('config/listOrd?appName=' + vmInfo.displayName)
        .then(res => {
            let fileListDom = pageEdit.querySelector('.edit-file-list')
            fileListDom.innerHTML = res.map(e =>
                `
                    <div onclick="readText('${e}', this)" class="edit-file">${e}</div>
                    `
            ).join('')
        })
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

            // 文件内容反显
            pageEdit.querySelector('#ordFileText').value = res.text
        })
}

let saveText = () => {
    let ordFileName = pageEdit.querySelector('.edit-code-title').innerText
    if (!ordFileName) {
        UI.showMessage('请先选择文件')
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
    let ordFileName = pageEdit.querySelector('.edit-code-title').innerText
    if (!ordFileName) {
        UI.showMessage('请先选择文件')
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

post('agent/vmInfo?vmId=' + vmId).then(res => {
    if (!res.vmId) {
        alert('进程不存在:' + vmId)
        return
    }
    vmInfo.displayName = res.displayName
    pageEdit.querySelector('.edit-title').innerText = vmInfo.displayName

    // 获取文件列表
    listFile()
})

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

let nextJavaOffset = 1

let listJavaFile = () => {
    let dialog = UI.newDialog('<div class="javaList"></div><button>加载</button>')
    dialog.querySelector('.javaList').addEventListener("click", event => {
        doAddFile(event.target.innerText + '.java')
        document.body.removeChild(dialog)
    })

    dialog.querySelector('button').addEventListener("click", () => {
        post(`agent/listClass?appName=${vmInfo.displayName}&offset=${nextJavaOffset}`)
            .then(res => {
                nextJavaOffset += (res.length | 0)
                let showHtml = res.map(e =>
                    `
                    <li >${e}</li>
                    `
                ).join('')
                dialog.querySelector('.javaList').innerHTML += showHtml
            })
    })
}



let genCode = () => {
    let ordFileName = pageEdit.querySelector('.edit-code-title').innerText
    if (!ordFileName || !ordFileName.endsWith('.java')) {
        UI.showMessage('请选择java文件')
        return
    }
    let className = ordFileName.substring(0, ordFileName.length - 5)
    post(`agent/genSource?appName=${vmInfo.displayName}&fullClassName=${className}`)
        .then(res => {
            pageEdit.querySelector('#ordFileText').value = res
            console.log(res)
        })
}