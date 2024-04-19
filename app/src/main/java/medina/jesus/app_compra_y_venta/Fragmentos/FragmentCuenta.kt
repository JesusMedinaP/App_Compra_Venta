package medina.jesus.app_compra_y_venta.Fragmentos

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import medina.jesus.app_compra_y_venta.OpcionesLogin
import medina.jesus.app_compra_y_venta.R
import medina.jesus.app_compra_y_venta.databinding.FragmentCuentaBinding

class FragmentCuenta : Fragment() {

    private lateinit var binding : FragmentCuentaBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var mContext : Context


    //Método para poder obtener el contexto ya que
    //estamos en un fragment y no lo tiene.
    override fun onAttach(context: Context) {
         mContext = context
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCuentaBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()

        //Método para poder cerrar sesión y volver a la vista de login.
        binding.BtnCerrarSesion.setOnClickListener {
            firebaseAuth.signOut()
            startActivity(Intent(mContext, OpcionesLogin::class.java))
            activity?.finishAffinity()
        }
    }

}