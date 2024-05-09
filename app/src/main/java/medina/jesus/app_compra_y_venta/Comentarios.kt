package medina.jesus.app_compra_y_venta

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import medina.jesus.app_compra_y_venta.databinding.ActivityComentariosBinding
import medina.jesus.app_compra_y_venta.databinding.CuadroDialogoAgregarComentarioBinding

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
            dialogComentar()
        }
    }

    private fun dialogComentar() {
        val agregarComentario = CuadroDialogoAgregarComentarioBinding.inflate(LayoutInflater.from(this))

        val builder = AlertDialog.Builder(this)
        builder.setView(agregarComentario.root)

        val alertDialog = builder.create()
        alertDialog.show()
    }
}