package medina.jesus.app_compra_y_venta.Chat

import android.widget.Filter
import medina.jesus.app_compra_y_venta.Adaptadores.AdaptadorChats
import medina.jesus.app_compra_y_venta.Modelo.Chats
import java.util.Locale

class BuscarChat : Filter{

    private val adaptadorChats : AdaptadorChats
    private val filtroLista : ArrayList<Chats>

    constructor(adaptadorChats: AdaptadorChats, filtroLista: ArrayList<Chats>) : super() {
        this.adaptadorChats = adaptadorChats
        this.filtroLista = filtroLista
    }

    override fun performFiltering(filtro: CharSequence?): FilterResults {
        var filtro : CharSequence ?= filtro
        val resultados = FilterResults()

        if(!filtro.isNullOrEmpty()){
            filtro = filtro.toString().uppercase(Locale.getDefault())
            val filtroModelos = ArrayList<Chats>()
            for(i in filtroLista.indices){
                if(filtroLista[i].nombre.uppercase().contains(filtro)){
                    filtroModelos.add(filtroLista[i])
                }
            }
            resultados.count = filtroModelos.size
            resultados.values = filtroModelos
        }else{
            resultados.count = filtroLista.size
            resultados.values = filtroLista
        }
        return resultados
    }

    override fun publishResults(filtro: CharSequence, resultados: FilterResults) {
        adaptadorChats.chats = resultados.values as ArrayList<Chats>
        adaptadorChats.notifyDataSetChanged()
    }
}