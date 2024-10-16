let pageData = atom.getParamsUrl()
const vmId = pageData.param.vmId

const vmInfo = {
    vmId: vmId,
    displayName: ''
}

let pageEdit = document.getElementById('page-edit')

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
            pageEdit.querySelector('#ordFileText').value  = res.text
        })
}

post('agent/vmInfo?vmId=' + vmId).then(res => {
    if (!res.vmId) {
        alert('进程不存在:' + vmId)
        return
    }
    vmInfo.displayName = res.displayName
    let appName = res.displayName
    pageEdit.querySelector('.edit-title').innerText = vmInfo.displayName

    // 获取文件列表
    post('config/listOrd?appName=' + appName)
        .then(res => {
            let fileListDom = pageEdit.querySelector('.edit-file-list')
            fileListDom.innerHTML = res.map(e =>
                `
                    <div onclick="readText('${e}', this)" class="edit-file">${e}</div>
                    `
            ).join('')
        })
})