package medina.jesus.app_compra_y_venta.Chat

import android.app.ProgressDialog
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import medina.jesus.app_compra_y_venta.Constantes
import medina.jesus.app_compra_y_venta.databinding.ActivityChatBinding

class Chat : AppCompatActivity() {

    private lateinit var binding : ActivityChatBinding
    private lateinit var firebaseAuth : FirebaseAuth
    private lateinit var progressDialog: ProgressDialog

    private var uidVendedor = "" //Emisor
    private var uidUsuario = "" //Receptor
    private var rutaChat = ""
    private var imagenUri : Uri ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere por favor")
        progressDialog.setCanceledOnTouchOutside(false)



        uidVendedor = intent.getStringExtra("uidVendedor")!!
        uidUsuario = firebaseAuth.uid!!

        rutaChat = Constantes.rutaChat(uidVendedor, uidUsuario)

        binding.icRegresar.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}