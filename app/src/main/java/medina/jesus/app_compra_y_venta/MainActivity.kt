package medina.jesus.app_compra_y_venta

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import medina.jesus.app_compra_y_venta.Anuncios.CrearAnuncio
import medina.jesus.app_compra_y_venta.Fragmentos.FragmentChats
import medina.jesus.app_compra_y_venta.Fragmentos.FragmentCuenta
import medina.jesus.app_compra_y_venta.Fragmentos.FragmentInicio
import medina.jesus.app_compra_y_venta.Fragmentos.FragmentMisAnuncios
import medina.jesus.app_compra_y_venta.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        comprobarSesion()

        verFragmentInicio()

        binding.BottomNV.setOnItemSelectedListener {item->
            when(item.itemId)
            {
                R.id.Item_Inicio->{
                    verFragmentInicio()
                    true
                }

                R.id.Item_Chats->
                {
                    verFragmentChats()
                    true
                }

                R.id.Item_Mis_Anuncios->{
                    verFragmentMisAnuncios()
                    true
                }

                R.id.Item_Cuenta->
                {
                    verFragmentCuenta()
                    true
                }

                else->{
                    false
                }
            }
        }
        binding.FAB.setOnClickListener {
            val intent = Intent(this, CrearAnuncio::class.java)
            intent.putExtra("Edicion", false)
            startActivity(intent)
        }
    }

    //Función para poder comprobar si hay un usuario utenticado
    //En caso de no ser así se vuelve a la vista con las opciones de Login
    private fun comprobarSesion()
    {
        if(firebaseAuth.currentUser == null)
        {
            startActivity(Intent(this,OpcionesLogin::class.java))
            finishAffinity()
        }else{
            agregarFcmToken()
            solicitarPermisoNotificaion()
        }
    }

    private fun verFragmentInicio()
    {
        binding.TituloRL.text = "Inicio"
        val fragment = FragmentInicio()
        val fragmentTransition = supportFragmentManager.beginTransaction()
        fragmentTransition.replace(binding.FragmentL1.id, fragment, "FragmentInicio")
        fragmentTransition.commit()
    }

    private fun verFragmentChats()
    {
        binding.TituloRL.text = "Chats"
        val fragment = FragmentChats()
        val fragmentTransition = supportFragmentManager.beginTransaction()
        fragmentTransition.replace(binding.FragmentL1.id, fragment, "FragmentChats")
        fragmentTransition.commit()
    }

    private fun verFragmentMisAnuncios()
    {
        binding.TituloRL.text = "Anuncios"
        val fragment = FragmentMisAnuncios()
        val fragmentTransition = supportFragmentManager.beginTransaction()
        fragmentTransition.replace(binding.FragmentL1.id, fragment, "FragmentMisAnuncios")
        fragmentTransition.commit()
    }

    private fun verFragmentCuenta()
    {
        binding.TituloRL.text = "Cuenta"
        val fragment = FragmentCuenta()
        val fragmentTransition = supportFragmentManager.beginTransaction()
        fragmentTransition.replace(binding.FragmentL1.id, fragment, "FragmentCuenta")
        fragmentTransition.commit()
    }

    private fun agregarFcmToken()
    {
        val uidUsuario = "${firebaseAuth.uid}"
        FirebaseMessaging.getInstance().token
            .addOnSuccessListener { fcmToken ->
                val hashMap = HashMap<String, Any>()
                hashMap["fcmToken"] = "$fcmToken"
                val ref = Constantes.obtenerReferenciaUsuariosDB()
                ref.child(uidUsuario)
                    .updateChildren(hashMap)
                    .addOnSuccessListener {
                        // Se ha agregado correctamente el el token a Firebase
                    }
                    .addOnFailureListener { e->
                        println(e.message)
                        Constantes.toastConMensaje(this, "Ha habido un problema al cargar el token del usuario")
                    }
            }
            .addOnFailureListener { e->
                println(e.message)
                Constantes.toastConMensaje(this, "Ha habido un problema al generar el token")
            }
    }

    private fun solicitarPermisoNotificaion()
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_DENIED){
                permisoNotificacion.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private val permisoNotificacion =
        registerForActivityResult(ActivityResultContracts.RequestPermission()){concesion->
            //Aquí se concede el permiso
        }
}