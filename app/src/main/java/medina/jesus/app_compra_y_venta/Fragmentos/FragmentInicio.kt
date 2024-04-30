package medina.jesus.app_compra_y_venta.Fragmentos

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import medina.jesus.app_compra_y_venta.Adaptadores.AdaptadorCategoria
import medina.jesus.app_compra_y_venta.Constantes
import medina.jesus.app_compra_y_venta.Modelo.Categoria
import medina.jesus.app_compra_y_venta.R
import medina.jesus.app_compra_y_venta.RvListenerCategoria
import medina.jesus.app_compra_y_venta.databinding.FragmentInicioBinding

class FragmentInicio : Fragment() {

    private lateinit var binding : FragmentInicioBinding
    private lateinit var contexto : Context

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
        cargarCategorias()
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
                    TODO("Not yet implemented")
                }
            }
        )

        binding.RvCategoria.adapter = adaptadorCategoria
    }
}