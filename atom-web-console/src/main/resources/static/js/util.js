const UI = {
	
	test: () => {
		console.log('UI.test')
	},
	
	openInputDialog: () => {
	  	console.log('UI.openInputDialog')
        let dialog = document.createElement("dialog");
        document.body.appendChild(dialog);
        dialog.showModal();
        dialog.innerHTML =
            `
            <h3>title</h3>
            <form method="dialog">
                <div class="vm-button-container">
                    <button value="cancel">关闭</button>
                </div>
            </form>
            `

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