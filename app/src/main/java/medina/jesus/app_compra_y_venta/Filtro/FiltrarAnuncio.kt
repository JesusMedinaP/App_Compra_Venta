package medina.jesus.app_compra_y_venta.Filtro

import android.widget.Filter
import medina.jesus.app_compra_y_venta.Adaptadores.AdaptadorAnuncio
import medina.jesus.app_compra_y_venta.Modelo.Anuncio
import java.util.Locale

class FiltrarAnuncio (
    private val adaptador: AdaptadorAnuncio,
    private val filtroLista: ArrayList<Anuncio>
) : Filter(){
    override fun performFiltering(filtro: CharSequence?): FilterResults {
        var filtro = filtro
        var resultados = FilterResults()

        if(!filtro.isNullOrEmpty()){
            filtro = filtro.toString().uppercase(Locale.getDefault())
            val filtroModelo = ArrayList<Anuncio>()
            for(i in filtroLista.indices){
                if(filtroLista[i].marca.uppercase(Locale.getDefault()).contains(filtro) ||
                    filtroLista[i].categoria.uppercase(Locale.getDefault()).contains(filtro) ||
                    filtroLista[i].condicion.uppercase(Locale.getDefault()).contains(filtro) ||
                    filtroLista[i].titulo.uppercase(Locale.getDefault()).contains(filtro))
                {
                    filtroModelo.add(filtroLista[i])
                }
            }
            resultados.count = filtroModelo.size
            resultados.values = filtroModelo
        }else{
            resultados.count = filtroLista.size
            resultados.values = filtroLista
        }
        return resultados
    }

    override fun publishResults(filtro: CharSequence?, resultados: FilterResults?) {
        adaptador.anuncioArrayList = resultados!!.values as ArrayList<Anuncio>
        adaptador.notifyDataSetChanged()
    }
}