package medina.jesus.app_compra_y_venta

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import medina.jesus.app_compra_y_venta.databinding.ActivityRecuperarPasswordBinding

class RecuperarPassword : AppCompatActivity() {

    private lateinit var binding : ActivityRecuperarPasswordBinding
    private lateinit var progressDialog : ProgressDialog
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecuperarPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere por favor")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.IbRegresar.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.BtnEnviarInstrucciones.setOnClickListener {
            validarEmail()
        }
    }

    private var email = ""
    private fun validarEmail() {
        email = binding.EtEmail.text.toString().trim()

        if(email.isEmpty()){
            Constantes.toastConMensaje(this, "Ingrese su correo")
        }else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.EtEmail.error = "Email inválido"
            binding.EtEmail.requestFocus()
        }else{
            enviarInstrucciones()
        }
    }

    private fun enviarInstrucciones() {
        progressDialog.setMessage("Enviando instrucciones al email ${email}")

        firebaseAuth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Constantes.toastConMensaje(this, "Las instrucciones de recuperaración han sido enviadas")
            }
            .addOnFailureListener { e->
                progressDialog.dismiss()
                Constantes.toastConMensaje(this, "No se han podido enviar las instrucciones debido a un error")
                println(e.message)
            }
    }
}