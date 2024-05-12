package medina.jesus.app_compra_y_venta

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import dmax.dialog.SpotsDialog
import medina.jesus.app_compra_y_venta.databinding.ActivityEliminarCuentaBinding

class EliminarCuenta : AppCompatActivity() {

    private lateinit var binding : ActivityEliminarCuentaBinding

    private lateinit var progressDialog: AlertDialog
    private lateinit var firebaseAuth: FirebaseAuth
    private var firebaseUser : FirebaseUser?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEliminarCuentaBinding.inflate(layoutInflater)
        setContentView(binding.root)


        firebaseAuth = FirebaseAuth.getInstance()
        firebaseUser = firebaseAuth.currentUser

        binding.IbRegresar.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.BtnEliminarMiCuenta.setOnClickListener {
            eliminarCuenta()
        }

    }

    private fun eliminarCuenta() {
        progressDialog = SpotsDialog.Builder()
            .setContext(this)
            .setMessage("Eliminando su cuenta")
            .setCancelable(false)
            .build()
            .apply { show() }

        val uidUsuario = firebaseAuth.uid
        firebaseUser!!.delete()
            .addOnSuccessListener {
                val anuncios = Constantes.obtenerReferenciaAnunciosDB()
                anuncios.orderByChild("uid").equalTo(uidUsuario)
                    .addValueEventListener(object : ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {
                            for(ds in snapshot.children){
                                ds.ref.removeValue()
                            }
                            val usuarioDb = Constantes.obtenerReferenciaUsuariosDB()
                            usuarioDb.child(uidUsuario!!).removeValue()
                                .addOnSuccessListener {
                                    progressDialog.dismiss()
                                    irMainActivity()
                                }
                                .addOnFailureListener { e->
                                    progressDialog.dismiss()
                                    Constantes.toastConMensaje(this@EliminarCuenta, "Ha habido un problema al eliminar el usuario")
                                    println(e.message)
                                    irMainActivity()
                                }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            progressDialog.dismiss()
                            println(error.message)
                            Constantes.toastConMensaje(this@EliminarCuenta, "Ha habido un problema al eliminar sus anuncios")
                        }
                    })
            }
            .addOnFailureListener {e->
                progressDialog.dismiss()
                Constantes.toastConMensaje(this, "Ha habido un problema al eliminar la cuenta")
                println(e.message)
            }
    }

    private fun irMainActivity()
    {
        startActivity(Intent(this, MainActivity::class.java))
        finishAffinity()
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        irMainActivity()
    }
}