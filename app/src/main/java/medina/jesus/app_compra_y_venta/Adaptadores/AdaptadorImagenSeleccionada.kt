package medina.jesus.app_compra_y_venta.Adaptadores

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import medina.jesus.app_compra_y_venta.Modelo.ImagenSeleccionada
import medina.jesus.app_compra_y_venta.R
import medina.jesus.app_compra_y_venta.databinding.ItemImagenesSeleccionadasBinding

class AdaptadorImagenSeleccionada(
    private val context : Context,
    private val imagenesSeleccionadasArrayList : ArrayList<ImagenSeleccionada>
): Adapter<AdaptadorImagenSeleccionada.HolderImagenSeleccionada>() {
    private lateinit var binding : ItemImagenesSeleccionadasBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderImagenSeleccionada {
        binding = ItemImagenesSeleccionadasBinding.inflate(LayoutInflater.from(context), parent, false)
        return HolderImagenSeleccionada(binding.root)
    }

    override fun getItemCount(): Int {
        return imagenesSeleccionadasArrayList.size
    }

    override fun onBindViewHolder(holder: HolderImagenSeleccionada, position: Int) {
        val modelo = imagenesSeleccionadasArrayList[position]

        //Diferenciación entre las imágenes que se obtienen desde
        //Firebase y las que se obtienen desde la cámara o galería
        if(modelo.internetOrigin){
            try {
                val imagenUrl = modelo.imagenUrl
                Glide.with(context)
                    .load(imagenUrl)
                    .placeholder(R.drawable.item_imagen)
                    .into(binding.itemImagen)
            }catch (e : Exception){
                println(e.message)
            }
        }else{
            try {
                val imagenUri = modelo.imagenUri
                Glide.with(context)
                    .load(imagenUri)
                    .placeholder(R.drawable.item_imagen)
                    .into(holder.item_imagen)
            }catch (e : Exception)
            {
                println(e.message)
            }
        }

        holder.boton_cerrar.setOnClickListener {
            imagenesSeleccionadasArrayList.remove(modelo)
            notifyDataSetChanged()
        }
    }

    inner class HolderImagenSeleccionada(itemView: View) : ViewHolder(itemView)
    {
        var item_imagen = binding.itemImagen
        var boton_cerrar = binding.cerrarItem

    }
}