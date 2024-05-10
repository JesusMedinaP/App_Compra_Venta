package medina.jesus.app_compra_y_venta.opciones_login

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import medina.jesus.app_compra_y_venta.Constantes
import medina.jesus.app_compra_y_venta.MainActivity
import medina.jesus.app_compra_y_venta.RecuperarPassword
import medina.jesus.app_compra_y_venta.Registro_email
import medina.jesus.app_compra_y_venta.databinding.ActivityLoginEmailBinding

class Login_email : AppCompatActivity() {

    private lateinit var binding : ActivityLoginEmailBinding
    private lateinit var firebaseAuth : FirebaseAuth
    private lateinit var progressDialog: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere por favor")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.TxtRegistrarme.setOnClickListener {
            startActivity(Intent(this@Login_email, Registro_email::class.java))
        }

        binding.BtnIngresar.setOnClickListener {
            validarInfo()
        }

        binding.TvRecuperar.setOnClickListener {
            startActivity(Intent(this@Login_email, RecuperarPassword::class.java))
        }
    }

    private var email = ""
    private var password = ""
    private fun validarInfo() {
        email = binding.EtEmail.text.toString().trim()
        password = binding.EtPassword.text.toString().trim()

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            binding.EtEmail.error = "Email inválido"
            binding.EtEmail.requestFocus()
        }else if(email.isEmpty()){
            binding.EtEmail.error = "Ingrese email"
            binding.EtEmail.requestFocus()
        }
        else if(password.isEmpty())
        {
            binding.EtPassword.error = "Ingrese contraseña"
            binding.EtEmail.requestFocus()
        }else{
            loginUsuario()
        }
    }

    private fun loginUsuario() {
        progressDialog.setMessage("Iniciando sesión")
        progressDialog.show()

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                progressDialog.dismiss()
                startActivity(Intent(this,MainActivity::class.java))
                finishAffinity()
                Constantes.toastConMensaje(this,
                    "Bienvenido")
            }
            .addOnFailureListener {e->
                progressDialog.dismiss()
                Constantes.toastConMensaje(this,
                    "No se pudo iniciar sesión debido a ${e.message}")
            }
    }
}