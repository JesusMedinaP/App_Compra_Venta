package medina.jesus.app_compra_y_venta.opciones_login

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import medina.jesus.app_compra_y_venta.R
import medina.jesus.app_compra_y_venta.Registro_email
import medina.jesus.app_compra_y_venta.databinding.ActivityLoginEmailBinding

class Login_email : AppCompatActivity() {

    private lateinit var binding : ActivityLoginEmailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.TxtRegistrarme.setOnClickListener {
            startActivity(Intent(this@Login_email, Registro_email::class.java))
        }
    }
}