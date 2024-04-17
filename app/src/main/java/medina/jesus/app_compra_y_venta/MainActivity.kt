package medina.jesus.app_compra_y_venta

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
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
    }

    //Función para poder comprobar si hay un usuario utenticado
    //En caso de no ser así se vuelve a la vista con las opciones de Login
    private fun comprobarSesion()
    {
        if(firebaseAuth.currentUser == null)
        {
            startActivity(Intent(this,OpcionesLogin::class.java))
            finishAffinity()
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
        binding.TituloRL.text = "Mis Anuncios"
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
}