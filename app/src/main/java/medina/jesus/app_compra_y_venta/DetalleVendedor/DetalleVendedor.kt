package medina.jesus.app_compra_y_venta.DetalleVendedor

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import medina.jesus.app_compra_y_venta.R

class DetalleVendedor : AppCompatActivity() {

    private var uidVendedor = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_vendedor)

        uidVendedor = intent.getStringExtra("uidVendedor").toString()
    }
}