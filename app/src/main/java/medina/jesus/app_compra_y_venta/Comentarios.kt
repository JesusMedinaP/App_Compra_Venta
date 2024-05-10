package medina.jesus.app_compra_y_venta

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import medina.jesus.app_compra_y_venta.Adaptadores.AdaptadorComentario
import medina.jesus.app_compra_y_venta.Modelo.Comentario
import medina.jesus.app_compra_y_venta.databinding.ActivityComentariosBinding
import medina.jesus.app_compra_y_venta.databinding.CuadroDialogoAgregarComentarioBinding

class Comentarios : AppCompatActivity() {

    private lateinit var binding : ActivityComentariosBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog
    private var uidVendedor = ""
    private lateinit var comentarios : ArrayList<Comentario>
    private lateinit var adaptadorComentario : AdaptadorComentario

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityComentariosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        uidVendedor = intent.getStringExtra("uidVendedor").toString()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Por favor espere")
        progressDialog.setCanceledOnTouchOutside(false)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.icRegresar.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.IbAgregarComentario.setOnClickListener {
            dialogComentar()
        }

        listarComentarios()
    }

    private fun listarComentarios() {
        comentarios = ArrayList()

        val ref = Constantes.obtenerReferenciaComentariosDB()
        ref.child(uidVendedor).child("Comentarios")
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    comentarios.clear()
                    for(ds in snapshot.children){
                        val modelo = ds.getValue(Comentario::class.java)
                        comentarios.add(modelo!!)
                    }

                    adaptadorComentario = AdaptadorComentario(this@Comentarios, comentarios)
                    binding.RvComentarios.adapter = adaptadorComentario
                }

                override fun onCancelled(error: DatabaseError) {
                    Constantes.toastConMensaje(this@Comentarios, "Ha habido un problema al cargar los comentarios")
                    println(error.message)
                }
            })
    }

    private var comentario = ""

    private fun dialogComentar() {
        val agregarComentarioBinding = CuadroDialogoAgregarComentarioBinding.inflate(LayoutInflater.from(this))

        val builder = AlertDialog.Builder(this)
        builder.setView(agregarComentarioBinding.root)

        val alertDialog = builder.create()
        alertDialog.show()
        alertDialog.setCanceledOnTouchOutside(false)

        agregarComentarioBinding.IbCerrar.setOnClickListener {
            alertDialog.dismiss()
        }

        agregarComentarioBinding.BtnComentar.setOnClickListener {
            comentario = agregarComentarioBinding.EtAgregarComentario.text.toString()
            if(comentario.isEmpty()){
                Constantes.toastConMensaje(this, "Ingrese un comentario")
            }else{
                alertDialog.dismiss()
                agregarComentario()
            }
        }
    }

    private fun agregarComentario() {
        progressDialog.setMessage("Agregando comentario")
        progressDialog.show()

        val tiempo = "${Constantes.obtenerTiempoDis()}"
        val hashMap = HashMap<String, Any>()
        hashMap["id"] = "$tiempo"
        hashMap["tiempo"] = "$tiempo"
        hashMap["uid"] = "${firebaseAuth.uid}"
        hashMap["uidVendedor"] = uidVendedor
        hashMap["comentario"] = "$comentario"

        val ref = Constantes.obtenerReferenciaComentariosDB()
        ref.child(uidVendedor).child("Comentarios").child(tiempo)
            .setValue(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Constantes.toastConMensaje(this, "Su comentario se ha publicado")
            }
            .addOnFailureListener { e->
                progressDialog.dismiss()
                Constantes.toastConMensaje(this, "${e.message}")
                println(e.message)
            }
    }
}