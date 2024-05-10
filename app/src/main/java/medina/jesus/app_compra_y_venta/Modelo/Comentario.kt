package medina.jesus.app_compra_y_venta.Modelo

class Comentario {
    var id = ""
    var tiempo = ""
    var uid = ""
    var uidVendedor = ""
    var comentario = ""

    constructor()
    constructor(id: String, tiempo: String, uid: String, uidVendedor: String, comentario: String) {
        this.id = id
        this.tiempo = tiempo
        this.uid = uid
        this.uidVendedor = uidVendedor
        this.comentario = comentario
    }
}