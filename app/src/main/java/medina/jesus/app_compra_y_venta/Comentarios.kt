package medina.jesus.app_compra_y_venta

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import medina.jesus.app_compra_y_venta.databinding.ActivityComentariosBinding

class Comentarios : AppCompatActivity() {

    private lateinit var binding : ActivityComentariosBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityComentariosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.icRegresar.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.IbAgregarComentario.setOnClickListener {
            Constantes.toastConMensaje(this, "Agregar comentario")
        }
    }
}