package medina.jesus.app_compra_y_venta.Fragmentos

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import medina.jesus.app_compra_y_venta.Adaptadores.AdaptadorAnuncio
import medina.jesus.app_compra_y_venta.Constantes
import medina.jesus.app_compra_y_venta.Modelo.Anuncio
import medina.jesus.app_compra_y_venta.databinding.FragmentMisAnunciosPublicadosBinding

class Fragment_Mis_Anuncios_Publicados : Fragment() {

    private lateinit var binding: FragmentMisAnunciosPublicadosBinding
    private lateinit var contexto : Context
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var misAnuncios : ArrayList<Anuncio>
    private lateinit var anunciosAdaptador : AdaptadorAnuncio

    override fun onAttach(context: Context) {
        this.contexto = context
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMisAnunciosPublicadosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()
        cargarMisAnuncios()
    }

    private fun cargarMisAnuncios() {
        misAnuncios = ArrayList()
        val ref = Constantes.obtenerReferenciaAnunciosDB()
        ref.orderByChild("uid").equalTo(firebaseAuth.uid!!)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    misAnuncios.clear()
                    for(ds in snapshot.children){
                        try{
                            val modeloAnuncio = ds.getValue(Anuncio::class.java)
                            misAnuncios.add(modeloAnuncio!!)
                        }catch (e: Exception){
                            Constantes.toastConMensaje(contexto, "Error al cargar mis anuncios")
                            println(e.message)
                        }
                    }
                    anunciosAdaptador = AdaptadorAnuncio(contexto, misAnuncios)
                    binding.misAnunciosRV.adapter = anunciosAdaptador
                }

                override fun onCancelled(error: DatabaseError) {
                    println(error.message)
                }
            })
    }
}