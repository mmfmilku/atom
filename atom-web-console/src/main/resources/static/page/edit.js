SPA_RESOLVE_INIT = function(transition) {
    alert("edit.js")
}
alert("edit.js")


atom.SPA.definePage({
    self : this,
    init: param => {
        self.param = param
    },
})