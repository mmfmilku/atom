

const urlParams = new URLSearchParams(window.location.hash);

let pageData = atom.getParamsUrl()
console.log(pageData)

const vmId = pageData.param.vmId

console.log(vmId);

const vmInfo = {
    vmId: vmId,
    displayName: ''
}

post('agent/vmInfo?vmId=' + vmId).then(res => {
    if (!res.vmId) {
        alert('进程不存在:' + vmId)
        return
    }
    vmInfo.displayName = res.displayName
    console.log(vmInfo)
})