package medina.jesus.app_compra_y_venta

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import medina.jesus.app_compra_y_venta.databinding.ActivityOpcionesLoginBinding
import medina.jesus.app_compra_y_venta.opciones_login.Login_email

class OpcionesLogin : AppCompatActivity() {

    private lateinit var binding: ActivityOpcionesLoginBinding

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOpcionesLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        comprobarSesion()

        binding.IngresarEmail.setOnClickListener{
            startActivity(Intent(this@OpcionesLogin, Login_email::class.java))
        }

    }

    //Si el usuario ha iniciado sesión, en caso de volver a esta vista, se retorna
    //a MainAcitvity.
    private fun comprobarSesion()
    {
        if(firebaseAuth.currentUser != null)
        {
            startActivity(Intent(this, MainActivity::class.java))
            finishAffinity()
        }
    }
}