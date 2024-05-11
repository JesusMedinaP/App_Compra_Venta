package medina.jesus.app_compra_y_venta.Chat

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import medina.jesus.app_compra_y_venta.R
import medina.jesus.app_compra_y_venta.databinding.ActivityChatBinding

class Chat : AppCompatActivity() {

    private lateinit var binding : ActivityChatBinding
    private var uidVendedor = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        uidVendedor = intent.getStringExtra("uidVendedor")!!


        binding.icRegresar.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}