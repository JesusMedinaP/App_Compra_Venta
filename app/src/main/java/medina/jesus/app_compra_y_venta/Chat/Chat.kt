package medina.jesus.app_compra_y_venta.Chat

import android.app.ProgressDialog
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import medina.jesus.app_compra_y_venta.Constantes
import medina.jesus.app_compra_y_venta.R
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

        cargarInfoVendedor()
    }

    private fun cargarInfoVendedor()
    {
        val ref = Constantes.obtenerReferenciaUsuariosDB()
        ref.child(uidVendedor)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        val nombre = "${snapshot.child("nombres").value}"
                        val imagen = "${snapshot.child("urlImagenPerfil").value}"

                        binding.TxtNombreVendedorChat.text = nombre
                        try {

                            Glide.with(this@Chat)
                                .load(imagen)
                                .placeholder(R.drawable.img_perfil)
                                .into(binding.ToolbarIv)

                        }catch (e : Exception){
                            println("Error al cargar la imagne con Glide " + e.message)
                        }
                    }catch (e : Exception){
                        println("Error al acceder al vendedor " + e.message)
                        Constantes.toastConMensaje(this@Chat, "Ha habido un problema al cargar la info " +
                                "del vendedor")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    println("Error en la base de datos " + error.message)
                    Constantes.toastConMensaje(this@Chat, "Ha habido un problema al acceder a la info " +
                            "del vendedor")
                }
            })
    }
}