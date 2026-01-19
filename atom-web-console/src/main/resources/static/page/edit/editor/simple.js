function load() {

    let editorDom = document.getElementById('simpleEditor')

    let editor = {
        setSubmitEvent: (event) => editor.submitEvent = event,
        getText: () => editorDom.value,
        setText: (text) => editorDom.value = text,
        getReadOnly: () => editorDom.readOnly,
        setReadOnly: (readOnly) => editorDom.readOnly = readOnly,
    }

    let keydownHandle = (event) => {
        // 检查是否为回车键（Enter 的 keyCode 是 13，或直接判断 event.key）
        if (event.key === 'Enter' || event.keyCode === 13) {
            if (event.ctrlKey) {
                event.preventDefault();
                // ctrl加回车,提交命令
                editor.submitEvent && editor.submitEvent()
            }
        }
        // 检查是否是Tab键
        else if (event.key === 'Tab' || event.keyCode === 9) {
            // 阻止默认的Tab行为（切换焦点）
            event.preventDefault();

            // 获取当前光标位置
            const start = editorDom.selectionStart
            const end = editorDom.selectionEnd
            const value = editorDom.value
            // 定义缩进（4个空格） 或者使用 '\t' 来插入制表符
            const indent = '    '

            // 如果选择了多行文本
            if (start !== end) {
                const linesBefore = value.substring(0, start).split('\n')
                const linesSelected = value.substring(start, end).split('\n')

                let newText = ''

                // 为每一行添加缩进
                linesSelected.forEach((line, index) => {
                    newText += indent + line
                    if (index < linesSelected.length - 1) {
                        newText += '\n'
                    }
                })

                editorDom.value = value.substring(0, start) + newText + value.substring(end)

                // 调整光标位置
                editorDom.selectionStart = start
                editorDom.selectionEnd = start + newText.length
            } else {
                // 单行插入缩进
                // 在光标位置插入缩进
                editorDom.value = value.substring(0, start) +
                                indent +
                                value.substring(end)

                // 将光标移动到插入缩进后的位置
                editorDom.selectionStart = editorDom.selectionEnd = start + indent.length
            }

            // 触发input事件（如果需要实时保存或其他操作）
            editorDom.dispatchEvent(new Event('input'))
        }
    }

    // 监听键盘按键
    editorDom.addEventListener('keydown', keydownHandle);

    return editor
}
