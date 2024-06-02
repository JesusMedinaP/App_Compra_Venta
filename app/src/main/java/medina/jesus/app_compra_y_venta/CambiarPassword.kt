package medina.jesus.app_compra_y_venta

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import medina.jesus.app_compra_y_venta.databinding.ActivityCambiarPasswordBinding
import medina.jesus.app_compra_y_venta.opciones_login.Login_email

class CambiarPassword : AppCompatActivity() {

    private lateinit var binding : ActivityCambiarPasswordBinding
    private lateinit var progressDialog: ProgressDialog
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseUser: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCambiarPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere por favor")
        progressDialog.setCanceledOnTouchOutside(false)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseUser = firebaseAuth.currentUser!!

        binding.IbRegresar.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.BtnActualizarPassword.setOnClickListener {
            validarInformacion()
        }
    }

    private var passwordActual = ""
    private var passwordNueva = ""
    private var passwordNuevaR = ""
    private fun validarInformacion() {
        passwordActual = binding.EtPasswordActual.text.toString().trim()
        passwordNueva = binding.EtPasswordNueva.text.toString().trim()
        passwordNuevaR = binding.EtPasswordNuevaR.text.toString().trim()

        if(passwordActual.isEmpty()){
            binding.EtPasswordActual.error = "Ingrese la contraseña actual"
            binding.EtPasswordActual.requestFocus()
        }else if (passwordNueva.isEmpty()){
            binding.EtPasswordNueva.error = "Ingrese una nueva contraseña"
            binding.EtPasswordNueva.requestFocus()
        }else if(passwordNuevaR.isEmpty()){
            binding.EtPasswordNuevaR.error = "Confirme la contraseña"
            binding.EtPasswordNuevaR.requestFocus()
        }else if(passwordNueva != passwordNuevaR){
            binding.EtPasswordNuevaR.error = "Las contraseñas no coinciden"
            binding.EtPasswordNuevaR.requestFocus()
        }else if (passwordActual.equals(passwordNueva)){
            binding.EtPasswordNueva.error = "La nueva contraseña no puede ser igual a la anterior"
            binding.EtPasswordNueva.requestFocus()
        }else{
            cambiarPasswordUsuario()
        }
    }

    private fun cambiarPasswordUsuario() {
        progressDialog.setMessage("Autenticando usuario")
        progressDialog.show()

        val autoCredencial = EmailAuthProvider.getCredential(firebaseUser.email.toString(), passwordActual)
        firebaseUser.reauthenticate(autoCredencial)
            .addOnSuccessListener {
                progressDialog.dismiss()
                actualizarPassword()
            }
            .addOnFailureListener { e->
                progressDialog.dismiss()
                Constantes.toastConMensaje(this, "Ha habido un problema al cambiar la contraseña")
                println(e.message)
            }
    }

    private fun actualizarPassword() {
        progressDialog.setMessage("Cambiando contraseña")
        progressDialog.show()

        firebaseUser.updatePassword(passwordNueva)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Constantes.toastConMensaje(this, "Su contraseña ha sido actualizada")

                finishAffinity()
                startActivity(Intent(this, Login_email::class.java))
                firebaseAuth.signOut()
            }
            .addOnFailureListener { e->
                progressDialog.dismiss()
                Constantes.toastConMensaje(this, "Ha habido un problema al cambiar la contraseña")
                println(e.message)
            }
    }
}