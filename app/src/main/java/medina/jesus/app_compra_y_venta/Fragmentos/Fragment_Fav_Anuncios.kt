package medina.jesus.app_compra_y_venta.Fragmentos

import android.content.Context
import android.os.Bundle
import android.os.Handler
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
import medina.jesus.app_compra_y_venta.R
import medina.jesus.app_compra_y_venta.databinding.FragmentFavAnunciosBinding

class Fragment_Fav_Anuncios : Fragment() {

    private lateinit var binding: FragmentFavAnunciosBinding
    private lateinit var contexto : Context
    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var anuncios : ArrayList<Anuncio>
    private lateinit var anunciosAdaptador : AdaptadorAnuncio
    override fun onAttach(context: Context) {
        this.contexto = context
        super.onAttach(context)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavAnunciosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()
        cargarAnunciosFavoritos()
    }

    private fun cargarAnunciosFavoritos() {
        anuncios = ArrayList()
        val ref = Constantes.obtenerReferenciaUsuariosDB()
        ref.child(firebaseAuth.uid!!).child("Favoritos")
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    anuncios.clear()
                    for(ds in snapshot.children){
                        val idAnuncio = "${ds.child("idAnuncio").value}"
                        val refFavoritos = Constantes.obtenerReferenciaAnunciosDB()
                        refFavoritos.child(idAnuncio)
                            .addListenerForSingleValueEvent(object : ValueEventListener{
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    try {
                                        val modeloAnuncio = snapshot.getValue(Anuncio::class.java)
                                        anuncios.add(modeloAnuncio!!)
                                    }catch (e : Exception){
                                        println(e.message)
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    println(error.message)
                                }
                            })
                    }
                    Handler().postDelayed({
                        anunciosAdaptador = AdaptadorAnuncio(contexto, anuncios)
                        binding.anunciosRV.adapter = anunciosAdaptador
                    }, 500)
                }

                override fun onCancelled(error: DatabaseError) {
                    println(error.message)
                }
            })
    }
}