package medina.jesus.app_compra_y_venta

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import medina.jesus.app_compra_y_venta.databinding.ActivityRegistroEmailBinding

class Registro_email : AppCompatActivity() {

    private lateinit var binding: ActivityRegistroEmailBinding

    private lateinit var firebaseAuth : FirebaseAuth
    private lateinit var progressDialog : ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistroEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere por favor")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.BtnRegistrar.setOnClickListener{
            validarInfo()
        }
    }

    private var email = ""
    private var password = ""
    private var r_password = ""

    //Función para la validación de los campos email, password y r_password.
    //En caso de estar vacíos o no cumplir con los patrones básicos, saltará un error en la vista.
    private fun validarInfo() {
        email = binding.EtEmail.text.toString().trim()
        password = binding.EtPassword.text.toString().trim()
        r_password = binding.EtRPassword.text.toString().trim()

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            binding.EtEmail.error = "Email inválido"
            binding.EtEmail.requestFocus()

        }else if(email.isEmpty()){
            binding.EtEmail.error = "Ingrese email"
            binding.EtEmail.requestFocus()

        }else if(password.isEmpty()) {
            binding.EtPassword.error = "Ingrese password"
            binding.EtPassword.requestFocus()

        }else if(r_password.isEmpty()) {
            binding.EtRPassword.error = "Repita el password"
            binding.EtRPassword.requestFocus()

        }else if(password != r_password)
        {
            binding.EtRPassword.error = "Las contraseñas no coinciden"
            binding.EtRPassword.requestFocus()
        }
        else{
            registarUsuario()
        }
    }

    //Creación de usuario en Firebase con el email y la contraseña
    //Y manejo de eventos tanto de éxito como de fallo.
    private fun registarUsuario() {
        progressDialog.setMessage("Creando Cuenta")
        progressDialog.show()

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                InsercionDBEmail()
            }
            .addOnFailureListener{e->
                progressDialog.dismiss()
                Toast.makeText(this,
                    "No se registró el usuario debiado a ${e.message}",
                    Toast.LENGTH_SHORT).show()
            }
    }

    //Al insertar de esta manera al usuario en la base de datos,
    //se crea directamente la tabla en Firebase con los campos específicados
    //en el hashmap
    //Además manejamos el error tanto de si se puede completar la operación como si no.
    private fun InsercionDBEmail() {
        progressDialog.setMessage("Guardando Información")

        val tiempo = Constantes.obtenerTiempoDis()

        val emailUsuario = firebaseAuth.currentUser!!.email
        val uidUsuario = firebaseAuth.uid

        val hashMap = HashMap<String, Any>()
        hashMap["nombres"] = ""
        hashMap["codigoTelefono"] = ""
        hashMap["telefono"] = ""
        hashMap["urlImagenPerfil"] = ""
        hashMap["proveedor"] = "Email"
        hashMap["escribiendo"] = ""
        hashMap["tiempo"] = tiempo
        hashMap["online"] = true
        hashMap["email"] = "${emailUsuario}"
        hashMap["uid"] = "${uidUsuario}"
        hashMap["fecha_nac"] = ""

        //URL de la DATABASE en caso de tener que usarla
        //https://app-compra-y-venta-de42d-default-rtdb.europe-west1.firebasedatabase.app/
        val ref = FirebaseDatabase.getInstance("https://app-compra-y-venta-de42d-default-rtdb.europe-west1.firebasedatabase.app").getReference("Usuarios")
        ref.child(uidUsuario!!)
            .setValue(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                startActivity(Intent(this, MainActivity::class.java))
                finishAffinity()
            }
            .addOnFailureListener { e->
                progressDialog.dismiss()
                Toast.makeText(
                    this,
                    "No se ha podido registrar debido a ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}