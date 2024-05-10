package medina.jesus.app_compra_y_venta

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import medina.jesus.app_compra_y_venta.databinding.ActivityRecuperarPasswordBinding

class RecuperarPassword : AppCompatActivity() {

    private lateinit var binding : ActivityRecuperarPasswordBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recuperar_password)
    }
}