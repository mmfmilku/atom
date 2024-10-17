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
            post(`config/writeOrd?appName=${vmInfo.displayName}&ordFileName=${ordFileName}`
                , {fileName: ordFileName, text: ''}
            )
                .then(res => {
                    UI.showMessage('添加成功')
                    listFile()
                })
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
            UI.showMessage('保存成功')
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
                    UI.showMessage('删除成功')
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
    post(`agent/retransform?appName=${vmInfo.displayName}&ordFileName=${ordFileName}`)
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