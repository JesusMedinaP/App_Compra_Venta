package medina.jesus.app_compra_y_venta.Fragmentos

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import medina.jesus.app_compra_y_venta.Adaptadores.AdaptadorAnuncio
import medina.jesus.app_compra_y_venta.Adaptadores.AdaptadorCategoria
import medina.jesus.app_compra_y_venta.Constantes
import medina.jesus.app_compra_y_venta.Modelo.Anuncio
import medina.jesus.app_compra_y_venta.Modelo.Categoria
import medina.jesus.app_compra_y_venta.RvListenerCategoria
import medina.jesus.app_compra_y_venta.SeleccionarUbicacion
import medina.jesus.app_compra_y_venta.databinding.FragmentInicioBinding

class FragmentInicio : Fragment() {

    private lateinit var binding : FragmentInicioBinding

    private companion object{
        private const val MAX_DISTANCIA_MOSTRAR_ANUNCIO = 10
    }

    private lateinit var contexto : Context

    private lateinit var anuncios : ArrayList<Anuncio>
    private lateinit var adaptadorAnuncio: AdaptadorAnuncio
    private lateinit var ubicacionSP: SharedPreferences

    private var latitudActual = 0.0
    private var longitudActual = 0.0
    private var direccionActual = ""

    override fun onAttach(context: Context) {
        contexto = context
        super.onAttach(context)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInicioBinding.inflate(LayoutInflater.from(contexto), container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ubicacionSP = contexto.getSharedPreferences("UBICACION_SP", Context.MODE_PRIVATE)
        latitudActual = ubicacionSP.getFloat("LATITUD_ACTUAL", 0.0f).toDouble()
        longitudActual = ubicacionSP.getFloat("LONGITUD_ACTUAL", 0.0f).toDouble()
        direccionActual = ubicacionSP.getString("DIRECCION_ACTUAL", "")!!

        if(latitudActual != 0.0 && longitudActual != 0.0)
        {
            binding.TvUbicacion.text = direccionActual
        }
        cargarCategorias()
        cargarAnuncios("Todos")

        binding.TvUbicacion.setOnClickListener {
            val intent = Intent(contexto, SeleccionarUbicacion::class.java)
            seleccionarUbicacionARL.launch(intent)
        }
    }

    private val seleccionarUbicacionARL = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()){resultado->
        if(resultado.resultCode == Activity.RESULT_OK)
        {
            val data = resultado.data
            if(data != null)
            {
                latitudActual = data.getDoubleExtra("latitud", 0.0)
                longitudActual = data.getDoubleExtra("longitud", 0.0)
                direccionActual = data.getStringExtra("direccion").toString()
                ubicacionSP.edit()
                    .putFloat("LATITUD_ACTUAL", latitudActual.toFloat())
                    .putFloat("LONGITUD_ACTUAL", longitudActual.toFloat())
                    .putString("DIRECCION_ACTUAL", direccionActual)
                    .apply()

                binding.TvUbicacion.text = direccionActual
                cargarAnuncios("Todos")
            }else{
                Toast.makeText(contexto, "Cancelado", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun cargarCategorias()
    {
        val categoriaArrayList = ArrayList<Categoria>()
        for(i in 0 until Constantes.categorias.size)
        {
            val modeloCategoria = Categoria(Constantes.categorias[i], Constantes.categoriasIcono[i])
            categoriaArrayList.add(modeloCategoria)
        }

        val adaptadorCategoria = AdaptadorCategoria(
            contexto,
            categoriaArrayList,
            object : RvListenerCategoria{
                override fun onCategoriaClick(modeloCategoria: Categoria) {
                    val categoriaSeleccionada = modeloCategoria.categoria
                    cargarAnuncios(categoriaSeleccionada)
                    println(categoriaSeleccionada)
                }
            }
        )

        binding.RvCategoria.adapter = adaptadorCategoria
    }


    private fun cargarAnuncios(categoria: String) {
        anuncios = ArrayList()
        val ref = Constantes.obtenerReferenciaAnunciosDB()
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                anuncios.clear()
                for(ds in snapshot.children)
                {
                    try {
                        val modeloAnuncio = ds.getValue(Anuncio::class.java)
                        val distancia = calcularDistanciaKM(
                            modeloAnuncio?.latitud ?: 0.0,
                            modeloAnuncio?.longitud ?: 0.0
                        )
                        if(categoria == "Todos")
                        {
                            if(distancia <= MAX_DISTANCIA_MOSTRAR_ANUNCIO){
                                anuncios.add(modeloAnuncio!!)
                            }
                        }else{
                            if(modeloAnuncio!!.categoria.equals(categoria))
                            {
                                if(distancia <= MAX_DISTANCIA_MOSTRAR_ANUNCIO){
                                    anuncios.add(modeloAnuncio)
                                }
                            }
                        }
                    }catch (e :Exception)
                    {
                        Constantes.toastConMensaje(contexto, "${e.message}")
                        println(e.message)
                    }
                }
                adaptadorAnuncio = AdaptadorAnuncio(contexto, anuncios)
                binding.RvAnuncios.adapter = adaptadorAnuncio
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun calcularDistanciaKM(latitud: Double, longitud: Double) : Double
    {
        val puntoPartida = Location(LocationManager.NETWORK_PROVIDER)
        puntoPartida.latitude = latitudActual
        puntoPartida.longitude = longitudActual

        val puntoFinal = Location(LocationManager.NETWORK_PROVIDER)
        puntoFinal.latitude = latitud
        puntoFinal.longitude = longitud

        val distaciaMetros = puntoPartida.distanceTo(puntoFinal).toDouble()
        return distaciaMetros/1000
    }
}