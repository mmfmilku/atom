const UI = {

    // 弹窗打开新页面
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
        popup.classList.add('atom-error')
        popup.innerText = message
        document.body.appendChild(popup)
        setTimeout(function () {
            document.body.removeChild(popup)
        }, 5000);
    },

    // 右键菜单
    showBlock: () => {

    }

}

const atom = {

    // TODO fix 特殊字符转义
    post: (path, data) => {
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
            formData[item.name] = item.value
        })
        return formData
    },

    SPA: {

        routers: [],

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

        loadJS: (path, dom) => {
            let _body = document.getElementsByTagName('body')[0];
            let scriptEle = document.createElement('script');
            scriptEle.type = 'text/javascript';
            scriptEle.src = path;
            scriptEle.async = true;
            scriptEle.onload = function () {

            }
            dom.appendChild(scriptEle);
            return scriptEle;
        },

        route: (pagePath, param) => {
            let pathData = atom.getPathParam(pagePath)
            let page = pathData.page
            let htmlPath = '/page/' + page + '/' + page + '.html'
            let jsPath = '/page/' + page + '/' + page + '.js'
            atom.SPA.loadHtml(htmlPath, document.getElementById('app'))
                .then(data => {
                    // 页面加载成功修改hash
                    location.hash = '#' + pagePath
                    // 最后加载js
                    let jsDom = atom.SPA.loadJS(jsPath, document.getElementById('app'))
                })
        },

        definePage: define => {
            atom.SPA.routers.push(define)
        },
    },

    isFunction: obj => {
        return !!(obj && Object.prototype.toString.call(obj) === '[object Function]')
    }
}


window.addEventListener('load', function () {
    console.log('load')
})
//路由切换
window.addEventListener('hashchange', function () {
    console.log('hashchange')
})


function spaRouters() {
    this.routers = {};//保存注册的所有路由
    this.beforeFun = null;//切换前
    this.afterFun = null;
}

spaRouters.prototype = {
    init: function () {
        var self = this;
        //页面加载匹配路由
        window.addEventListener('load', function () {
            self.urlChange()
        })
        //路由切换
        window.addEventListener('hashchange', function () {
            self.urlChange()
        })
        //异步引入js通过回调传递参数
        window.SPA_RESOLVE_INIT = null;
    },
    refresh: function (currentHash) {
        var self = this;
        if (self.beforeFun) {
            self.beforeFun({
                to: {
                    path: currentHash.path,
                    query: currentHash.query
                },
                next: function () {
                    self.routers[currentHash.path].callback.call(self, currentHash)
                }
            })
        } else {
            self.routers[currentHash.path].callback.call(self, currentHash)
        }
    },
    //路由处理
    urlChange: function () {
        var currentHash = atom.getParamsUrl();
        if (this.routers[currentHash.path]) {
            this.refresh(currentHash)
        } else {
            //不存在的地址重定向到首页
            location.hash = '/index'
        }
    },
    //单层路由注册
    map: function (path, callback) {
        path = path.replace(/\s*/g, "");//过滤空格
        if (callback && Object.prototype.toString.call(callback) === '[object Function]') {
            this.routers[path] = {
                callback: callback,//回调
                fn: null //存储异步文件状态
            }
        } else {
            console.trace('注册' + path + '地址需要提供正确的的注册回调')
        }
    },
    //切换之前一些处理
    beforeEach: function (callback) {
        if (Object.prototype.toString.call(callback) === '[object Function]') {
            this.beforeFun = callback;
        } else {
            console.trace('路由切换前钩子函数不正确')
        }
    },
    //切换成功之后
    afterEach: function (callback) {
        if (Object.prototype.toString.call(callback) === '[object Function]') {
            this.afterFun = callback;
        } else {
            console.trace('路由切换后回调函数不正确')
        }
    },
    //路由异步懒加载js文件
    asyncFun: function (file, transition) {
        var self = this;
        if (self.routers[transition.path].fn) {
            self.afterFun && self.afterFun(transition)
            self.routers[transition.path].fn(transition)
        } else {
            console.log("开始异步下载js文件" + file)
            var _body = document.getElementsByTagName('body')[0];
            var scriptEle = document.createElement('script');
            scriptEle.type = 'text/javascript';
            scriptEle.src = file;
            scriptEle.async = true;
            SPA_RESOLVE_INIT = null;
            scriptEle.onload = function () {
                console.log('下载' + file + '完成')
                self.afterFun && self.afterFun(transition)
                self.routers[transition.path].fn = SPA_RESOLVE_INIT;
                self.routers[transition.path].fn(transition)
            }
            _body.appendChild(scriptEle);
        }
    },
    //同步操作
    syncFun: function (callback, transition) {
        this.afterFun && this.afterFun(transition)
        callback && callback(transition)
    }

}
//注册到window全局
// window.spaRouters = new spaRouters();


//
//
// 案例如下:
//
//     1、创建静态.html/.htm文件；
//
// 2、在html中创建文档元素
// <a href="#/login">测试1</a>，
//     <a href="#/register">测试2</a>
//     <a href="#/main">测试3</a>
// “#”后代表的是相对应的内容；
//
// 3、分别创建3个
// login.js
// index.js
// main.js脚本文件；
// 4、注册路由 :
//     spaRouters.map('/index',function(transition){
//         //异步加载js
//         spaRouters.asyncFun('index.js',transition)
//         //或者同步执行回调
//         //spaRouters.syncFun(function(transition){},transition)
//     })
//
// 5、login.js脚本代码如下:
// SPA_RESOLVE_INIT = function(transition) {
//     alert("请登录");
// }
// 5、index.js脚本代码如下:
// SPA_RESOLVE_INIT = function(transition) {
//     alert("测试1");
// }
//
// 5、main.js脚本代码如下:
// SPA_RESOLVE_INIT = function(transition) {
//     alert("测试2");
// }
//
// 6、初始化：spaRouters.init()；
