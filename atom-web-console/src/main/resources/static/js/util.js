
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
                    windowDom.style.zIndex = '99'
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
        return new Promise((resolve, reject) => {
            let xhr = new XMLHttpRequest()
            xhr.open("POST", path, true)
            xhr.setRequestHeader('content-type', 'application/json; charset=UTF-8')
            xhr.setRequestHeader('atom-proxy-env', localStorage.env || 'local')
            xhr.onload = () => {
                if (xhr.status !== 200) {
                    try {
                        UI.showError(JSON.parse(xhr.response))
                        return
                    } catch (e) {
                        UI.showError(xhr.response)
                        return
                    }
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

class PackageTrie {
    /**
     * 字典树节点类
     */
    TrieNode = class {
        constructor() {
            // 子节点映射，键为节点字符串值
            this.children = new Map();
            // 记录class出现次数
            this.count = 0;
            // 是否为单词结束，即类名
            this.isEndOfWord = false;
        }
    };

    /**
     * 构造函数，初始化字典树
     */
    constructor() {
        // 字典树根节点
        this.root = new this.TrieNode();
    }

    /**
     * 插入字符串到字典树中
     * @param {string} word 要插入的字符串，如"com.aa.bb.Add"
     */
    insert(word) {
        if (!word || word.trim() === '') {
            return;
        }

        // 将字符串按点号分割
        const segments = word.split('.');
        let current = this.root;

        for (const segment of segments) {
            // 如果子节点中不存在该字符串段，则创建新节点
            if (!current.children.has(segment)) {
                current.children.set(segment, new this.TrieNode());
            }
            current = current.children.get(segment);
        }

        current.isEndOfWord = true;
        current.count++;
    }

    /**
     * 查找字符串是否存在于字典树中
     * @param {string} word 要查找的字符串，如"com.aa.bb.Add"
     * @returns {boolean} 如果存在返回true，否则返回false
     */
    search(word) {
        if (!word || word.trim() === '') {
            return false;
        }

        // 将字符串按点号分割
        const segments = word.split('.');
        let current = this.root;

        for (const segment of segments) {
            const child = current.children.get(segment);
            if (!child) {
                return false;
            }
            current = child;
        }

        return current.isEndOfWord;
    }

    /**
     * 检查是否有以给定前缀开头的字符串
     * @param {string} prefix 要检查的前缀，如"com.aa"
     * @returns {boolean} 如果存在返回true，否则返回false
     */
    startsWith(prefix) {
        if (!prefix || prefix.trim() === '') {
            return true;
        }

        // 将前缀按点号分割
        const segments = prefix.split('.');
        let current = this.root;

        for (const segment of segments) {
            const child = current.children.get(segment);
            if (!child) {
                return false;
            }
            current = child;
        }

        return true;
    }

    /**
     * 获取所有以给定前缀开头的字符串
     * @param {string} prefix 要查找的前缀，如"com.aa"
     * @returns {string[]} 所有匹配的字符串列表
     */
    getAllWithPrefix(prefix) {
        const result = [];

        if (!prefix || prefix.trim() === '') {
            // 获取所有字符串
            this.collectAllWords(this.root, '', result);
            return result;
        }

        // 将前缀按点号分割
        const segments = prefix.split('.');
        let current = this.root;

        // 先找到前缀的最后一个节点
        for (const segment of segments) {
            const child = current.children.get(segment);
            if (!child) {
                return result;  // 前缀不存在
            }
            current = child;
        }

        // 从该节点开始收集所有单词
        this.collectAllWords(current, prefix, result);
        return result;
    }

    /**
     * 递归收集所有以当前节点为根的单词
     * @param {TrieNode} node 当前节点
     * @param {string} prefix 当前前缀
     * @param {string[]} result 结果列表
     */
    collectAllWords(node, prefix, result) {
        if (node.isEndOfWord) {
            result.push(prefix);
        }

        for (const [nextSegment, childNode] of node.children) {
            const newPrefix = prefix === '' ? nextSegment : `${prefix}.${nextSegment}`;
            this.collectAllWords(childNode, newPrefix, result);
        }
    }

    /**
     * 删除字符串从字典树中
     * @param {string} word 要删除的字符串，如"com.aa.bb.Add"
     * @returns {boolean} 如果删除成功返回true，否则返回false
     */
    delete(word) {
        if (!word || word.trim() === '') {
            return false;
        }

        const segments = word.split('.');
        return this.deleteRecursive(this.root, segments, 0, '');
    }

    /**
     * 递归删除字符串
     * @param {TrieNode} current 当前节点
     * @param {string[]} segments 字符串分割后的数组
     * @param {number} index 当前处理的分割段索引
     * @param {string} currentPath 当前路径的字符串表示
     * @returns {boolean} 如果节点可以被删除返回true，否则返回false
     */
    deleteRecursive(current, segments, index, currentPath) {
        // 基本情况：已经处理完所有分割段
        if (index === segments.length) {
            // 如果不是单词结束，不能删除
            if (!current.isEndOfWord) {
                return false;
            }

            // 标记为不是单词结束
            current.isEndOfWord = false;
            current.count--;

            // 如果没有子节点，可以删除当前节点
            return current.children.size === 0;
        }

        const segment = segments[index];
        const child = current.children.get(segment);

        // 如果不存在该分割段的节点，不能删除
        if (!child) {
            return false;
        }

        // 递归删除子节点
        const shouldDeleteChild = this.deleteRecursive(child, segments, index + 1,
            currentPath === '' ? segment : `${currentPath}.${segment}`);

        // 如果子节点应该被删除
        if (shouldDeleteChild) {
            current.children.delete(segment);
            // 如果当前节点不是单词结束且没有其他子节点，可以删除当前节点
            return !current.isEndOfWord && current.children.size === 0;
        }

        return false;
    }

    /**
     * 获取单词出现的次数
     * @param {string} word 要查询的单词，如"com.aa.bb.Add"
     * @returns {number} 单词出现的次数
     */
    getWordCount(word) {
        if (!word || word.trim() === '') {
            return 0;
        }

        const segments = word.split('.');
        let current = this.root;

        for (const segment of segments) {
            const child = current.children.get(segment);
            if (!child) {
                return 0;
            }
            current = child;
        }

        return current.isEndOfWord ? current.count : 0;
    }

    /**
     * 清空字典树
     */
    clear() {
        // 清空根节点的所有子节点
        this.root.children.clear();
        this.root.isEndOfWord = false;
        this.root.count = 0;
    }

    /**
     * 检查字典树是否为空
     * @returns {boolean} 如果为空返回true，否则返回false
     */
    isEmpty() {
        return !this.root.isEndOfWord && this.root.children.size === 0;
    }

    /**
     * 获取字典树中单词的总数
     * @returns {number} 单词总数
     */
    size() {
        return this.countWords(this.root);
    }

    /**
     * 递归计算单词总数
     * @param {TrieNode} node 当前节点
     * @returns {number} 单词总数
     */
    countWords(node) {
        let count = node.isEndOfWord ? 1 : 0;
        for (const child of node.children.values()) {
            count += this.countWords(child);
        }
        return count;
    }

    /**
     * 获取节点的原始值（通过拼接所有父节点和当前节点的值）
     * @param {TrieNode} node 要获取值的节点（此方法仅用于调试）
     * @returns {string} 节点的原始值
     */
    getOriginalValue(node) {
        // 注意：此方法需要遍历从根节点到目标节点的路径，实际使用时需要额外的路径记录
        // 这里仅作为示例，实际实现可能需要修改节点结构以存储完整路径
        return "需要额外实现路径记录功能";
    }


}

// 文件浏览器实现
class FileExplorer {
    constructor(containerId, leafNodeFunc) {
        this.container = document.getElementById(containerId);
        this.trie = new PackageTrie();
        this.selectedNode = null;
        this.leafNodeFunc = leafNodeFunc;
    }

    // 添加文件路径
    addFile(filePath) {
        this.trie.insert(filePath);
    }

    // 添加多个文件路径
    addFiles(filePaths) {
        filePaths.forEach(path => this.addFile(path));
    }

    // 渲染文件树
    render() {
        this.container.innerHTML = '';
        this.renderNode(this.container, this.trie.root, '');
    }

    // 检查节点是否为叶子目录（只有一个子节点且不是类文件）
    isLeafDirectory(node) {
        return node.children.size === 1 && !node.isEndOfWord;
    }

    // 递归合并连续的叶子目录
    mergeLeafDirectories(segment, node, segments = [segment]) {
        if (this.isLeafDirectory(node)) {
            const nextSegment = Array.from(node.children.keys())[0];
            segments.push(nextSegment);
            return this.mergeLeafDirectories(nextSegment, node.children.get(nextSegment), segments);
        }
        return { mergedPath: segments.join('.'), finalNode: node };
    }

    // 递归渲染节点
    renderNode(parentElement, node, currentPath) {
        // 遍历所有子节点
        for (const [segment, childNode] of node.children) {
            let nodePath = currentPath === '' ? segment : `${currentPath}.${segment}`;
            let displayText = segment;
            let finalNode = childNode;
            let isDirectory = finalNode.children.size > 0;
            let isClass = finalNode.isEndOfWord;

            // 检查是否需要合并连续目录
            if (this.isLeafDirectory(childNode)) {
                const mergedResult = this.mergeLeafDirectories(segment, childNode);
                displayText = mergedResult.mergedPath;
                nodePath = currentPath === '' ? mergedResult.mergedPath : `${currentPath}.${mergedResult.mergedPath}`;
                finalNode = mergedResult.finalNode;
                isDirectory = finalNode.children.size > 0;
                isClass = finalNode.isEndOfWord;
            }

            // 创建节点元素
            const nodeElement = document.createElement('div');
            nodeElement.className = 'tree-node';
            nodeElement.dataset.path = nodePath;

            // 展开/折叠图标
            if (isDirectory) {
                const expandIcon = document.createElement('span');
                expandIcon.className = 'expand-icon';
                expandIcon.textContent = '▶';
                expandIcon.onclick = (e) => {
                    e.stopPropagation();
                    this.toggleNode(nodeElement);
                };
                nodeElement.appendChild(expandIcon);
            } else {
                this.leafNodeFunc && this.leafNodeFunc(nodeElement)
                const spacer = document.createElement('span');
                spacer.style.width = '21px';
                spacer.style.display = 'inline-block';
                nodeElement.appendChild(spacer);
            }

            // 文件/包图标
            const fileIcon = document.createElement('span');
            fileIcon.className = `file-icon ${isDirectory ? 'package' : 'class'}`;
            nodeElement.appendChild(fileIcon);

            // 节点文本
            const nodeText = document.createElement('span');
            nodeText.className = 'node-text';
            nodeText.textContent = displayText;
            nodeElement.appendChild(nodeText);

            // 类文件计数
            if (isDirectory && finalNode.isEndOfWord) {
                const nodeCount = document.createElement('span');
                nodeCount.className = 'node-count';
                nodeCount.textContent = `(${finalNode.count})`;
                nodeElement.appendChild(nodeCount);
            }

            // 点击事件
            let oldClickFunc = nodeElement.onclick || (() => {});
            nodeElement.onclick = () => {
                this.selectNode(nodeElement);
                oldClickFunc(nodeElement);
            }

            // 添加到父元素
            parentElement.appendChild(nodeElement);

            // 如果是目录，创建子节点容器
            if (isDirectory) {
                const childrenContainer = document.createElement('div');
                childrenContainer.className = 'node-children';
                childrenContainer.dataset.path = nodePath;
                parentElement.appendChild(childrenContainer);

                // 默认展开根目录
                if (currentPath === '') {
                    this.toggleNode(nodeElement);
                }
            }
        }
    }

    // 展开/折叠节点
    toggleNode(nodeElement) {
        const expandIcon = nodeElement.querySelector('.expand-icon');
        const childrenContainer = nodeElement.nextElementSibling;

        if (expandIcon && childrenContainer) {
            // 切换展开/折叠状态
            expandIcon.classList.toggle('expanded');
            childrenContainer.classList.toggle('expanded');

            // 如果是首次展开，渲染子节点
            if (childrenContainer.classList.contains('expanded') && childrenContainer.children.length === 0) {
                const nodePath = nodeElement.dataset.path;
                const segments = nodePath.split('.');
                let currentNode = this.trie.root;

                // 找到对应的节点（支持合并目录的路径解析）
                for (const segment of segments) {
                    currentNode = currentNode.children.get(segment);
                    if (!currentNode) break;
                }

                // 渲染子节点
                if (currentNode) {
                    this.renderNode(childrenContainer, currentNode, nodePath);
                }
            }
        }
    }

    // 选择节点
    selectNode(nodeElement) {
        // 取消之前的选择
        if (this.selectedNode) {
            this.selectedNode.classList.remove('selected');
        }

        // 选择当前节点
        nodeElement.classList.add('selected');
        this.selectedNode = nodeElement;

        // 输出选中的路径
        console.log('Selected:', nodeElement.dataset.path);
    }

    // 展开所有节点
    expandAll() {
        const expandIcons = this.container.querySelectorAll('.expand-icon');
        expandIcons.forEach(icon => {
            const nodeElement = icon.parentElement;
            const childrenContainer = nodeElement.nextElementSibling;

            if (nodeElement && childrenContainer && !childrenContainer.classList.contains('expanded')) {
                this.toggleNode(nodeElement);
            }
        });
    }

    // 折叠所有节点
    collapseAll() {
        const expandIcons = this.container.querySelectorAll('.expand-icon.expanded');
        expandIcons.forEach(icon => {
            const nodeElement = icon.parentElement;
            this.toggleNode(nodeElement);
        });
    }
}