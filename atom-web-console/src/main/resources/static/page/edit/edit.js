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
                    <div onclick="genCode('${e}', this)" class="edit-file text-wrap">${e}</div>
                    `
            ).join('')
            fileListDom.innerHTML = showHtml
        })

}

// 获取重写列表
let listFile = clickDom => {
    btnClickChange(pageEdit.querySelector('.listFileBtn'))
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
    fileListDom.innerHTML = '待支持'
}

let classToFile = () => {
    let ordFileName = contextTarget.innerText + '.java'
    // 避免已存在重写文件被覆盖，先查询
    post(`config/readOrd?appName=${vmInfo.displayName}&ordFileName=${ordFileName}`)
        .then(res => {
            if (res.text == null) {
                UI.openConfirmDialog('新建重写' + ordFileName)
                    .then(() => {
                        // 首次新建，使用反编译源码
                        return post(`agent/genSource?appName=${vmInfo.displayName}&fullClassName=${contextTarget.innerText}`)
                    })
                    .then(text => {
                        doAddFile(ordFileName, text)
                    })
            } else {
                // 已存在直接读取
                readText(ordFileName)
                listFile()
            }
        })

}

// 设置编辑类型，同时设置是否可编辑
let typeArr = [
    {
        type: '',
        desc: '类(只读)'
    },
    {
        type: 'file',
        desc: '<button onclick="saveText()">保存</button>'
    },
    {
        type: 'strategy',
        desc: '<button onclick="saveText()">保存</button>'
    }
]
let setType = (typeIdx) => {
    // class 类型不可编辑
    pageEdit.querySelector('#ordFileText').readOnly = !typeIdx
    pageEdit.querySelector('.edit-code-desc').innerHTML = typeArr[typeIdx]['desc']
}

let doAddFile = (ordFileName, text = '') => {
    post(`config/writeOrd?appName=${vmInfo.displayName}&ordFileName=${ordFileName}`
        , {fileName: ordFileName, text: text}
    )
        .then(res => {
            UI.showMessage(res)
            readText(ordFileName)
            listFile()
        })
}

let readText = (ordFileName, clickDom) => {
    post(`config/readOrd?appName=${vmInfo.displayName}&ordFileName=${ordFileName}`)
        .then(res => {
            // 文件选中
            let oldSelect = pageEdit.querySelector('.edit-file-select')
            oldSelect && oldSelect.classList.remove('edit-file-select')
            clickDom && clickDom.classList.add('edit-file-select')

            // 文件标题反显
            pageEdit.querySelector('.edit-code-title').innerText = ordFileName
            setType(1)

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
    UI.openConfirmDialog('确认删除' + contextTarget.innerText)
        .then(() => {
            post(`config/deleteOrd?appName=${vmInfo.displayName}&ordFileName=${contextTarget.innerText}`)
                .then(res => {
                    UI.showMessage(res)
                    listFile()
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
        })
}

let stopOrd = () => {

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
})

let contextmenu
let fileRightMenu
let classRightMenu
let contextTarget

atom.SPA.loadHtml('/page/edit/fileRightMenu.html')
    .then(html => {
        fileRightMenu = html
    })
atom.SPA.loadHtml('/page/edit/classRightMenu.html')
    .then(html => {
        classRightMenu = html
    })

let openBlock = (event, blockHtml) => {
    if (contextmenu) {
        document.body.removeChild(contextmenu)
        contextmenu = null
        contextTarget = null
    }
    contextTarget = event.target
    contextmenu = UI.showBlock(blockHtml)
    contextmenu.style.left = event.pageX + 'px';
    contextmenu.style.top = event.pageY + 'px';
    contextmenu.style.display = 'block';
}

document.addEventListener('contextmenu', function(event) {
    event.preventDefault();
    let parentClassList = event.target.parentNode.classList
    if (parentClassList.contains('listFile')) {
        // 在重写文件上右键
        openBlock(event, fileRightMenu)
    } else if (parentClassList.contains('listClass')) {
        // 在类文件上右键
        openBlock(event, classRightMenu)
    }
})

// 隐藏菜单当用户点击其他地方
document.addEventListener('click', function(event) {
    if (contextmenu) {
        document.body.removeChild(contextmenu)
        contextmenu = null
    }
})