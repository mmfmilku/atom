const UI = {
	
	test: () => {
		console.log('UI.test')
	},
	
	openInputDialog: (title) => {
        return new Promise((resolve, reject) => {
            let dialog = document.createElement("dialog")
            document.body.appendChild(dialog)
            if (typeof dialog.showModal !== "function") {
                alert("Sorry, this browser is too low.")
                return
            }
            dialog.showModal()
            let titleHtml = title ? `<h3>${title}</h3>` : ''
            dialog.innerHTML =
                `
            ${titleHtml}
            <form method="dialog">
                <input name="dialogInput"/>
                <div class="vm-button-container">
                    <button class="dialog-submit">确定</button>
                    <button class="dialog-cancel">关闭</button>
                </div>
            </form>
            `
            dialog.querySelector(".dialog-submit").addEventListener("click", () => {
                let dialogValue = dialog.querySelector("input[name=dialogInput]").value
                resolve(dialogValue)
            })
            dialog.querySelector(".dialog-cancel").addEventListener("click", () => {
                let dialogValue = dialog.querySelector("input[name=dialogInput]").value
                reject(dialogValue)
            })
        })
	}
}

const atom = {

    post: (path, data) => {
        return new Promise((resolve, reject) => {
            let xhr = new XMLHttpRequest()
            xhr.open("POST",path,true)
            xhr.setRequestHeader('content-type', 'application/json; charset=UTF-8')
            xhr.onload = () => {
                try {
                    resolve(JSON.parse(xhr.response))
                } catch(e) {
                    resolve(xhr.response)
                }
            }
            xhr.onerror = () => {
                reject()
            }
            xhr.send(JSON.stringify(data))
        })
    }
    
}