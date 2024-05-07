package medina.jesus.app_compra_y_venta.Modelo

class ImagenSlider{
    var id : String = ""
    var imagenUrl : String = ""

    constructor()

    constructor(id: String, imagenUrl: String) {
        this.id = id
        this.imagenUrl = imagenUrl
    }

}