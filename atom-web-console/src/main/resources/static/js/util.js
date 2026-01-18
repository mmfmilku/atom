
const UI = {

    // 弹窗打开新页面，返回点击确定时触发的promise
    openPageWin: (pagePath, title, param) => {
        return new Promise((resolve, reject) => {
            atom.SPA.loadHtml(pagePath + '.html', null, param)
                .then(showHtml => UI.openDialog(showHtml, title))
                .then((pageDom => {
                        resolve(atom.getInputData(pageDom))
                    }
                ))
        })
    },

    // 弹窗展示
    newDialog: (showHtml = '') => {
        let dialog = document.createElement("dialog")
        document.body.appendChild(dialog)
        if (typeof dialog.showModal !== "function") {
            alert("Sorry, this browser is too low.")
            return
        }
        dialog.showModal()
        dialog.innerHTML = showHtml
        return dialog
    },

    // 弹窗展示，显示确认关闭按钮
    openDialog: (showHtml = '', title = '') => {
        let dialog = document.createElement("dialog")
        document.body.appendChild(dialog)
        if (typeof dialog.showModal !== "function") {
            alert("Sorry, this browser is too low.")
            return
        }
        dialog.showModal()
        dialog.innerHTML =
            `<h3>${title}</h3>
            <form method="dialog">
                ${showHtml}
                <div class="vm-button-container">
                    <button class="dialog-submit">确定</button>
                    <button class="dialog-cancel">取消</button>
                </div>
            </form>
            `
        return new Promise((resolve, reject) => {
            dialog.querySelector(".dialog-submit").addEventListener("click", event => {
                resolve && resolve(dialog, event)
                document.body.removeChild(dialog)
            })
            dialog.querySelector(".dialog-cancel").addEventListener("click", event => {
                reject && reject(dialog, event)
                document.body.removeChild(dialog)
            })
        })
    },

    openInputDialog: (title) => {
        return new Promise((resolve, reject) => {
            UI.openDialog('<input name="dialogInput"/>', title)
                .then((dialog) => {
                    // 点击确认的回调
                    resolve && resolve(dialog.querySelector("input[name=dialogInput]").value)
                })
                .catch((dialog) => {
                    // 点击取消的回调
                    reject && reject()
                })
        })
    },

    openConfirmDialog: (title) => {
        return UI.openDialog('', title)
    },

    // 打开浮动窗口
    openFloatWindow: (pagePath, title = '', parentDom = document.body, param = {}) => {
        return new Promise((resolve, reject) => {
            atom.SPA.loadHtml(pagePath + '.html', null, param)
                .then(showHtml => {
                    let windowHtml = `
                        <div >
                            <div class="atom-float-window-header">
                                <div >${title}</div>
                            </div>
                            <div >
                                ${showHtml}
                            </div>
                        </div>
                    `

                    let windowDom = document.createElement("div")
                    windowDom.classList.add('atom-float-window')
                    windowDom.innerHTML = windowHtml
                    parentDom.appendChild(windowDom)

                    // 绑定关闭事件
//                    windowDom.querySelector('.close-btn').addEventListener('click', function(event) {
//                        document.body.removeChild(windowDom)
//                    });

                    let winHeader = windowDom.querySelector('.atom-float-window-header')

                    let isDragging = false
                    let dragOffsetX, dragOffsetY
                    // 拖拽功能
                    winHeader.addEventListener('mousedown', function(e) {
                        isDragging = true
                        dragOffsetX = e.clientX - windowDom.getBoundingClientRect().left
                        dragOffsetY = e.clientY - windowDom.getBoundingClientRect().top
                        windowDom.style.transition = 'none';
                    });
                    // 鼠标移动事件,需要监听整个页面的鼠标移动
                    document.addEventListener('mousemove', function(e) {
                        if (isDragging) {
                            // 鼠标拖动的目标位置，为了不超过左侧与上侧不能小于0
                            let dragToX = e.clientX - dragOffsetX
                            let dragToY = e.clientY - dragOffsetY
                            // 为了不超过左侧与上侧，不能小于0
                            dragToX = Math.max(dragToX, 0)
                            dragToY = Math.max(dragToY, 0)
                            // 为了不超过右侧与下侧，不能大于body元素的边界
                            dragToX = Math.min(dragToX, document.body.getBoundingClientRect().right - windowDom.offsetWidth)
                            dragToY = Math.min(dragToY, document.body.getBoundingClientRect().bottom - winHeader.offsetHeight)

                            // 元素定位需要加上自身尺寸的一半
                            windowDom.style.left = (dragToX + windowDom.offsetWidth / 2) + 'px'
                            windowDom.style.top = (dragToY + windowDom.offsetHeight / 2) + 'px'
                        }
                    });
                    // 鼠标释放事件
                    document.addEventListener('mouseup', function() {
                        if (isDragging) {
                            isDragging = false
                            windowDom.style.transition = 'all 0.3s ease'
                        }
                    })

                    resolve(windowDom)
                })
        })
    },

    showMessage: (message) => {
        let popup = document.createElement("div")
        popup.classList.add('atom-tip')
        popup.innerText = message
        document.body.appendChild(popup)
        setTimeout(function () {
            document.body.removeChild(popup)
        }, 5000);
    },

    showError: (message) => {
        let popup = document.createElement("div")
        popup.classList.add('atom-window')
        popup.classList.add('atom-error')
        popup.innerText = message
        popup.innerHTML =
            `<div>
                <div class="atom-window-header vm-button-container">
                    <div class="close-btn">x</div>
                </div>
                <div class="atom-window-body">${message}</div>
            </div>`
        document.body.appendChild(popup)

        // 添加事件监听器
        popup.querySelector('.close-btn').addEventListener('click', () => {
            document.body.removeChild(popup)
        })
    },

    // 右键菜单
    showBlock: (blockHtml) => {
        let block = document.createElement("div")
        block.classList.add('atom-op-block')
        block.innerHTML = blockHtml
        document.body.appendChild(block)
        return block
    }

}

const atom = {

    // TODO fix 特殊字符转义
    post: (path, data) => {
        console.log(this)
        return new Promise((resolve, reject) => {
            let xhr = new XMLHttpRequest()
            xhr.open("POST", path, true)
            xhr.setRequestHeader('content-type', 'application/json; charset=UTF-8')
            xhr.onload = () => {
                if (xhr.status !== 200) {
                    UI.showError(JSON.parse(xhr.response).message)
                    return
                }
                try {
                    resolve(JSON.parse(xhr.response))
                } catch (e) {
                    resolve(xhr.response)
                }
            }
            xhr.onerror = () => {
                reject()
            }
            xhr.send(JSON.stringify(data))
        })
    },

    // 获取路由的路径和详细参数
    getParamsUrl: function () {
        return atom.getPathParam(location.hash)
    },

    getPathParam: path => {
        // path like page?p1=1&p2=2
        let pathArr = path.split("?")
        let page = pathArr[0].charAt(0) === '#' ? pathArr[0].substring(1) : pathArr[0]
        let paramArr = pathArr[1] ? pathArr[1].split("&") : []
        let paramData = {}
        paramArr.forEach((value, index) => {
            let item = value.split("=");
            paramData[item[0]] = item[1]
        })
        return {
            page: page,
            param: paramData
        }
    },

    getInputData: pageDom => {
        let allInput = pageDom.querySelectorAll('input')
        let formData = {}
        allInput.forEach(item => {
            if (item.type === 'checkbox') {
                formData[item.name] = item.checked
            } else {
                formData[item.name] = item.value
            }
        })
        return formData
    },

    SPA: {

        router: null,

        // 读取html
        loadHtml: (path, dom, param = {}) => {
            return atom.SPA.router.loadHtml(path, dom, param)
        },

        loadJS: (path, dom) => {
            return atom.SPA.router.loadJS(path, dom)
        },

        // 页面路由
        route: (pagePath, param) => {
            return atom.SPA.router.route(pagePath, param)
        },

        reload: () => {
            atom.SPA.router.reload()
        },

        definePage: define => {
            atom.SPA.routers.push(define)
        },
    },

    isFunction: obj => {
        return !!(obj && Object.prototype.toString.call(obj) === '[object Function]')
    }
}

function spaRouters() {
    // 保存注册的所有路由
    this.routers = {
        'list': {
            htmlPath: '/page/main/list.html'
        },
        'edit': {
            htmlPath: '/page/edit/edit.html'
        },
        'test': {
            htmlPath: '/page/test/test.html',
            jsPath: '/page/test/test.js'
        }
    }
    // 路由前钩子
    this.beforeHook = null
    // 路由后钩子
    this.afterFun = null
}

spaRouters.prototype = {
    init: function () {
        let self = this;
        // 页面加载匹配路由
        window.addEventListener('load', function () {
            console.log('load:' + location.hash)
            // 页面初始加载
            self.urlChange()
        })
        //路由切换
        window.addEventListener('hashchange', function () {
            console.log('hashchange:' + location.hash)
            // 页面hash变化
            self.urlChange()
        })
        // 异步引入js通过回调传递参数
        // window.SPA_RESOLVE_INIT = null;
    },
    //路由处理
    urlChange: function () {
        let currentHash = atom.getParamsUrl();
        this.loadPage(currentHash)
    },
    reload: function () {
        let currentHash = atom.getParamsUrl();
        this.loadPage(currentHash)
    },
    route: function (pagePath, param) {
        location.hash = pagePath
    },
    loadPage: function (pageHash) {
        if (!pageHash) {
            location.reload()
            return
        }
        let pageData = this.routers[pageHash.page]
        if (!pageData) {
            // 不存在的地址重定向到首页
            // this.route('index')
            return
        }
        let self = this;
        if (self.beforeHook) {

        } else {
            let htmlPath = pageData.htmlPath
            let jsPath = pageData.jsPath || htmlPath.replace('.html', '.js')
            // 加载html
            this.loadHtml(htmlPath, document.getElementById('app'))
                .then(showHtml => {
                    // 最后加载js
                    let jsDom = this.loadJS(jsPath, document.getElementById('app'), () => {
//                        load && load(document.getElementById('app'), showHtml)
                        window.atomPage && window.atomPage.beforeDestroy && window.atomPage.beforeDestroy()
                        window.atomPage = {}
                        load && load.apply(window.atomPage)
                    })
                })
        }
    },
    loadHtml: (path, dom, param = {}) => {
        return new Promise(((resolve, reject) => {
            fetch(path)
                .then(response => response.text())
                .then(data => {
                    let showHtml = eval('`' + data + '`')
                    dom && (dom.innerHTML = showHtml)
                    resolve(showHtml)
                })
                .catch(error => {
                    reject(error)
                })
        }))
    },
    loadJS: (path, dom, onload) => {
        let _body = document.getElementsByTagName('body')[0];
        let scriptEle = document.createElement('script');
        scriptEle.type = 'text/javascript';
        scriptEle.src = path;
        scriptEle.async = true;
        scriptEle.onload = onload
        dom.appendChild(scriptEle);
        return scriptEle;
    },

}

atom.SPA.router = new spaRouters()

atom.SPA.router.init()

const post = (path, data) => {
    return atom.post(path, data)
}