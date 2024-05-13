package medina.jesus.app_compra_y_venta.Fragmentos

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
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
        binding.EtBuscar.addTextChangedListener (object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(filtro: CharSequence?, start: Int, before: Int, count: Int) {
                try {
                    val consulta = filtro.toString()
                    anunciosAdaptador.filter.filter(consulta)
                }catch (e:Exception){
                    Constantes.toastConMensaje(contexto, "Algo ha ido mal al buscar los anuncios")
                    println(e.message)
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        binding.IbLimpiar.setOnClickListener {
            val consulta = binding.EtBuscar.text.toString().trim()
            if(consulta.isNotEmpty()){
                binding.EtBuscar.setText("")
                Constantes.toastConMensaje(contexto, "Se ha limpiado el campo de búsqueda")
            }else{
                Constantes.toastConMensaje(contexto, "No se ha ingresado una consulta")
            }
        }
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

                    if(misAnuncios.isEmpty()){
                        binding.TvFeedbackMisAnuncios.visibility = View.VISIBLE
                    }else{
                        binding.TvFeedbackMisAnuncios.visibility = View.GONE
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    println(error.message)
                }
            })
    }
}