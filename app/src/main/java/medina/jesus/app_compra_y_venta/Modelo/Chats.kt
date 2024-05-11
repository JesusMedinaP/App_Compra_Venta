package medina.jesus.app_compra_y_venta.Modelo

class Chats {
    var urlImagenPerfil : String = ""
    var nombre : String = ""
    var keyChat : String = ""
    var uidRecibido : String = ""
    var idMensaje : String = ""
    var tipoMensaje : String = ""
    var mensaje : String = ""
    var uidEmisor : String = ""
    var uidReceptor : String = ""
    var tiempo : Long = 0

    constructor()
    constructor(
        urlImagenPerfil: String,
        nombre: String,
        keyChat: String,
        uidRecibido: String,
        idMensaje: String,
        tipoMensaje: String,
        mensaje: String,
        uidEmisor: String,
        uidReceptor: String,
        tiempo: Long
    ) {
        this.urlImagenPerfil = urlImagenPerfil
        this.nombre = nombre
        this.keyChat = keyChat
        this.uidRecibido = uidRecibido
        this.idMensaje = idMensaje
        this.tipoMensaje = tipoMensaje
        this.mensaje = mensaje
        this.uidEmisor = uidEmisor
        this.uidReceptor = uidReceptor
        this.tiempo = tiempo
    }


}