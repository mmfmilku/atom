function load() {

    let editorDom = document.getElementById('monacoEditor')

    let atomEditor = {}

    atom.SPA.router.loadJS('https://cdnjs.cloudflare.com/ajax/libs/monaco-editor/0.34.0/min/vs/loader.min.js', editorDom, () => {

        require.config({
            paths: { vs: 'https://cdnjs.cloudflare.com/ajax/libs/monaco-editor/0.34.0/min/vs'}
        })

        require(['vs/editor/editor.main'], function() {

        	// 注册自定义 Java 语法配置（增强体验）
            monaco.languages.register({ id: 'java' });

            // Java 语言配置
            monaco.languages.setLanguageConfiguration('java', {
                comments: {
                    lineComment: '//',
                    blockComment: ['/*', '*/']
                },
                brackets: [
                    ['{', '}'],
                    ['[', ']'],
                    ['(', ')']
                ],
                autoClosingPairs: [
                    { open: '{', close: '}' },
                    { open: '[', close: ']' },
                    { open: '(', close: ')' },
                    { open: '"', close: '"' },
                    { open: "'", close: "'" }
                ],
                surroundingPairs: [
                    { open: '{', close: '}' },
                    { open: '[', close: ']' },
                    { open: '(', close: ')' },
                    { open: '"', close: '"' },
                    { open: "'", close: "'" },
                    { open: '<', close: '>' }
                ],
                folding: {
                    markers: {
                        start: /^\s*\/\/\s*#region\b/,
                        end: /^\s*\/\/\s*#endregion\b/
                    }
                }
            });

        	// Java 语法高亮配置
            monaco.languages.setMonarchTokensProvider('java', {
                keywords: [
                    'abstract', 'assert', 'boolean', 'break', 'byte', 'case', 'catch',
                    'char', 'class', 'continue', 'default', 'do', 'double',
                    'else', 'enum', 'extends', 'final', 'finally', 'float', 'for',
                    'if', 'implements', 'import', 'instanceof', 'int', 'interface',
                    'long', 'native', 'new', 'package', 'private', 'protected', 'public',
                    'return', 'short', 'static', 'super', 'switch',
                    'synchronized', 'this', 'throw', 'throws', 'transient', 'try',
                    'void', 'volatile', 'while', 'var', 'record'
                ],
                typeKeywords: [
                    'String', 'Integer', 'Double', 'Float', 'Boolean', 'Character',
                    'Byte', 'Short', 'Long', 'Object', 'List', 'Map', 'Set', 'ArrayList',
                    'HashMap', 'HashSet'
                ],
                operators: [
                    '=', '>', '<', '!', '~', '?', ':', '==', '<=', '>=', '!=',
                    '&&', '||', '++', '--', '+', '-', '*', '/', '&', '|', '^', '%',
                    '<<', '>>', '>>>', '+=', '-=', '*=', '/=', '&=', '|=', '^=', '%=',
                    '<<=', '>>=', '>>>='
                ],
                symbols: /[=><!~?:&|+\-*\/\^%]+/,
                escapes: /\\(?:[abfnrtv\\"']|x[0-9A-Fa-f]{1,4}|u[0-9A-Fa-f]{4}|U[0-9A-Fa-f]{8})/,
                tokenizer: {
                    root: [
                        // 标识符和关键字
                        [/[a-zA-Z_$][\w$]*/, {
                            cases: {
                                '@keywords': 'keyword',
                                '@typeKeywords': 'type.identifier',
                                '@default': 'identifier'
                            }
                        }],

                        // 数字
                        [/\d*\.\d+([eE][\-+]?\d+)?[fFdD]?/, 'number.float'],
                        [/0[xX][0-9a-fA-F]+/, 'number.hex'],
                        [/\d+[lL]?/, 'number'],

                        // 字符串
                        [/"([^"\\]|\\.)*$/, 'string.invalid'],
                        [/"/, { token: 'string.quote', bracket: '@open', next: '@string' }],

                        // 字符
                        [/'[^\\']'/, 'string'],
                        [/(')(@escapes)(')/, ['string', 'string.escape', 'string']],
                        [/'/, 'string.invalid'],

                        // 注释
                        [/\/\/.*$/, 'comment'],
                        [/\/\*/, 'comment', '@comment']
                    ],
                    comment: [
                        [/[^\/*]+/, 'comment'],
                        [/\/\*/, 'comment', '@push'],
                        ["\\*/", 'comment', '@pop'],
                        [/[\/*]/, 'comment']
                    ],
                    string: [
                        [/[^\\"]+/, 'string'],
                        [/@escapes/, 'string.escape'],
                        [/\\./, 'string.escape.invalid'],
                        [/"/, { token: 'string.quote', bracket: '@close', next: '@pop' }]
                    ]
                }
            });

            // /*注释*/

            // 创建编辑器
            const editor = monaco.editor.create(editorDom, {
                value: '',
                language: 'java',
                theme: 'vs-dark',
                fontSize: 14,
        		lineNumbers: 'on',
                minimap: { enabled: false },
                scrollBeyondLastLine: false,
                automaticLayout: true,
                tabSize: 4,
                insertSpaces: true,
                wordWrap: 'on',
                formatOnPaste: true,
                formatOnType: true,
                suggestOnTriggerCharacters: true,
                acceptSuggestionOnEnter: 'on',
                snippetSuggestions: 'inline',
                parameterHints: {
                    enabled: true,
                    cycle: true
                },
        		// Java 特定配置
                java: {
                    // 添加更多 Java 特定配置
                }
            });

        	// 注册代码补全
            setupJavaCompletion();

            // 添加键盘快捷键
            setupKeyboardShortcuts(editor);

            // 监听内容变化
//            editor.onDidChangeModelContent(function(e) {
//                console.log('代码已更改');
//            });

            // 获取当前代码
//            function getCode() {
//                return editor.getValue();
//            }
//
//            // 设置代码
//            function setCode(code) {
//                editor.setValue(code);
//            }

            // 暴露相关函数
            atomEditor.getText = () => editor.getValue()
            atomEditor.setText = (text) => editor.setValue(text)
            atomEditor.getReadOnly = () => editor.getOptions().get(monaco.editor.EditorOption.readOnly)
            atomEditor.setReadOnly = (readOnly) => editor.updateOptions({ readOnly: readOnly })
        })

         // 设置 Java 代码补全
        function setupJavaCompletion() {
            monaco.languages.registerCompletionItemProvider('java', {
                provideCompletionItems: (model, position) => {
                    const word = model.getWordUntilPosition(position);
                    const range = {
                        startLineNumber: position.lineNumber,
                        endLineNumber: position.lineNumber,
                        startColumn: word.startColumn,
                        endColumn: word.endColumn
                    };

                    // Java 关键字补全
                    const suggestions = [
                        // 关键字
                        { label: 'public', kind: monaco.languages.CompletionItemKind.Keyword, insertText: 'public ' },
                        { label: 'private', kind: monaco.languages.CompletionItemKind.Keyword, insertText: 'private ' },
                        { label: 'protected', kind: monaco.languages.CompletionItemKind.Keyword, insertText: 'protected ' },
                        { label: 'class', kind: monaco.languages.CompletionItemKind.Keyword, insertText: 'class ' },
                        { label: 'interface', kind: monaco.languages.CompletionItemKind.Keyword, insertText: 'interface ' },
                        { label: 'void', kind: monaco.languages.CompletionItemKind.Keyword, insertText: 'void ' },
                        { label: 'int', kind: monaco.languages.CompletionItemKind.Keyword, insertText: 'int ' },
                        { label: 'String', kind: monaco.languages.CompletionItemKind.Keyword, insertText: 'String ' },
                        { label: 'boolean', kind: monaco.languages.CompletionItemKind.Keyword, insertText: 'boolean ' },
                        { label: 'return', kind: monaco.languages.CompletionItemKind.Keyword, insertText: 'return ' },
                        { label: 'if', kind: monaco.languages.CompletionItemKind.Keyword, insertText: 'if ()' },
                        { label: 'for', kind: monaco.languages.CompletionItemKind.Keyword, insertText: 'for ()' },
                        { label: 'while', kind: monaco.languages.CompletionItemKind.Keyword, insertText: 'while ()' },

                        // 常用代码片段
                        {
                            label: 'main',
                            kind: monaco.languages.CompletionItemKind.Snippet,
                            insertText: 'public static void main(String[] args) {\n\t${1:System.out.println("Hello, World!");}\n}',
                            documentation: '创建 main 方法'
                        },
                        {
                            label: 'fori',
                            kind: monaco.languages.CompletionItemKind.Snippet,
                            insertText: 'for (int ${1:i} = 0; ${1:i} < ${2:length}; ${1:i}++) {\n\t${3}\n}',
                            documentation: '创建 for 循环'
                        },
                        {
                            label: 'sysout',
                            kind: monaco.languages.CompletionItemKind.Snippet,
                            insertText: 'System.out.println(${1});',
                            documentation: '输出到控制台'
                        },
                        {
                            label: 'try',
                            kind: monaco.languages.CompletionItemKind.Snippet,
                            insertText: 'try {\n\t${1}\n} catch (Exception e) {\n\t${2}\n}',
                            documentation: 'try-catch 语句'
                        }
                    ];

                    return { suggestions: suggestions };
                }
            });
        }

        // 设置键盘快捷键
        function setupKeyboardShortcuts(editor) {
            // Ctrl+S 保存
            editor.addCommand(monaco.KeyMod.CtrlCmd | monaco.KeyCode.KeyS, () => {
                saveCode();
            });

            // Ctrl+Space 触发代码补全
            editor.addCommand(monaco.KeyMod.CtrlCmd | monaco.KeyCode.Space, () => {
                editor.trigger('keyboard', 'editor.action.triggerSuggest', {});
            });

            // Alt+Shift+F 格式化
            editor.addCommand(monaco.KeyMod.Alt | monaco.KeyMod.Shift | monaco.KeyCode.KeyF, () => {
                formatCode();
            });

            // Tab 键处理
            editor.addCommand(monaco.KeyCode.Tab, () => {
                const selection = editor.getSelection();
                if (selection.isEmpty()) {
                    // 插入4个空格
                    editor.executeEdits("", [{
                        range: selection,
                        text: "    ",
                        forceMoveMarkers: true
                    }]);
                } else {
                    // 多行缩进
                    editor.getAction('editor.action.indentLines').run();
                }
            });

            // Shift+Tab 减少缩进
            editor.addCommand(monaco.KeyMod.Shift | monaco.KeyCode.Tab, () => {
                editor.getAction('editor.action.outdentLines').run();
            });
        }

    })

    return atomEditor
}
