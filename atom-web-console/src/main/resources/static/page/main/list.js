function load() {
    let pageDom = document.getElementById('pageMain')
    let vmList
    let init = () => {
        post('agent/listVm', {})
            .then(res => {
                vmList = res
                let listHtml = res.map(e =>
                    `
                    <div class="vm-list-block" rightClickEvent="${e.vmId}" onclick="atom.SPA.route('edit?vmId=${e.vmId}')">
                        <div class="vm-field">
                            <label class="vm-field-label">应用名称:</label>
                            <div class="vm-field-value">${e.displayName}</div>
                        </div>
                        <div class="vm-field">
                            <label class="vm-field-label">别名:</label>
                            <div class="vm-field-value">${e.alias || ''}</div>
                        </div>
                        <div class="vm-field">
                            <label class="vm-field-label">id:</label>
                            <div class="vm-field-value">${e.vmId}</div>
                        </div>
                    </div>
                    `
                ).join('')
                pageDom.querySelector('.vm-list').innerHTML  = listHtml

                let vmBlocks = pageDom.querySelectorAll('.vm-list-block')
                vmBlocks && vmBlocks.forEach(e => {
                    e.addEventListener('contextmenu', function(event) {
                        event.preventDefault()
                        let vmId = event.currentTarget.getAttribute('rightClickEvent')
                        vmId && openBlock(event, vmId)
                    })
                })
            })
    }
    init()

    let rightBlock
    let openBlock = (event, vmId) => {
        this.currVm = vmList.find(e => e.vmId == vmId)
        // 由于该html中需要获取currVm，所以无法提前加载html模板
        atom.SPA.loadHtml('/page/main/vmRightMenu.html')
            .then(html => {
                rightBlock && document.body.removeChild(rightBlock)
                rightBlock = UI.showBlock(html)
                rightBlock.style.left = event.clientX + 'px'
                rightBlock.style.top = event.clientY + 'px'
                rightBlock.style.display = 'block'
            })
    }

    // 隐藏菜单当用户点击其他地方
    pageDom.addEventListener('click', function(event) {
        rightBlock && document.body.removeChild(rightBlock)
        rightBlock = null
    })

    this.setAlias = (target) => {
        UI.openInputDialog()
            .then(alias => post(`agent/setAlias?appName=${this.currVm.displayName}&aliasName=${alias}`))
            .then(res => {
                UI.showMessage(res)
                // 重新加载列表
                init()
            })
    }

    this.serviceInfo = () => {
        this.currVm.vmId
        post(`agent/serviceInfo?vmId=${this.currVm.vmId}`)
            .then(data => UI.openPageWin('/page/main/serviceInfo', '服务信息', {...data, ...this.currVm}))
    }

    this.beforeDestroy = () => {
        rightBlock && document.body.removeChild(rightBlock)
        rightBlock = null
    }

}