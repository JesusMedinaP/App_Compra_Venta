package medina.jesus.app_compra_y_venta.Modelo

import android.net.Uri

class ImagenSeleccionada {

    var id = ""
    var imagenUri : Uri?= null
    var imagenUrl : String?= null
    var internetOrigin = false

    constructor()
    constructor(id: String, imagenUri: Uri?, imagenUrl: String?, internetOrigin: Boolean) {
        this.id = id
        this.imagenUri = imagenUri
        this.imagenUrl = imagenUrl
        this.internetOrigin = internetOrigin
    }


}