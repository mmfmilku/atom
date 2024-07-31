const UI = {
	
	test: () => {
		console.log('UI.test')
	},
	
	openInputDialog: () => {
	  	console.log('UI.openInputDialog')
        var dialog = document.createElement("dialog");
        document.body.appendChild(dialog);
        dialog.showModal();
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