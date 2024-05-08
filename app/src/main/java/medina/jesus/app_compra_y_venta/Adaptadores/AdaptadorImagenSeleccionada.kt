package medina.jesus.app_compra_y_venta.Adaptadores

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.google.firebase.storage.FirebaseStorage
import medina.jesus.app_compra_y_venta.Constantes
import medina.jesus.app_compra_y_venta.Modelo.ImagenSeleccionada
import medina.jesus.app_compra_y_venta.R
import medina.jesus.app_compra_y_venta.databinding.ItemImagenesSeleccionadasBinding

class AdaptadorImagenSeleccionada(
    private val context : Context,
    private val imagenesSeleccionadasArrayList : ArrayList<ImagenSeleccionada>,
    private val idAnuncio : String
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
            if(modelo.internetOrigin){
                val btn_si : MaterialButton
                val btn_cancelar : MaterialButton
                val dialog = Dialog(context)

                dialog.setContentView(R.layout.cuadro_dialogo_eliminar_imagen)
                btn_si = dialog.findViewById(R.id.Btn_Si)
                btn_cancelar = dialog.findViewById(R.id.Btn_Cancelar)

                btn_si.setOnClickListener {
                    eliminarImagenFirebasse(modelo, holder, position)
                    dialog.dismiss()
                }
                btn_cancelar.setOnClickListener {
                    dialog.dismiss()
                }
                dialog.show()
                dialog.setCanceledOnTouchOutside(false)
            }else{
                imagenesSeleccionadasArrayList.remove(modelo)
                notifyDataSetChanged()
            }
        }
    }

    private fun eliminarImagenFirebasse(modelo: ImagenSeleccionada, holder: AdaptadorImagenSeleccionada.HolderImagenSeleccionada, position: Int) {
        val idImagen = modelo.id

        val ref = Constantes.obtenerReferenciaAnunciosDB()
        ref.child(idAnuncio).child("Imagenes").child(idImagen)
            .removeValue()
            .addOnSuccessListener {
                try{
                    imagenesSeleccionadasArrayList.remove(modelo)
                    eliminarImagenStorage(modelo)
                    notifyItemRemoved(position)
                }catch (e : Exception){
                    println(e.message)
                }
            }
            .addOnFailureListener { e->
                Constantes.toastConMensaje(context, "Ha habido un error al borrar la imagen")
                println(e.message)
            }
    }

    private fun eliminarImagenStorage(modelo: ImagenSeleccionada) {
        val rutaImagen = "Anuncios/"+modelo.id
        val ref = FirebaseStorage.getInstance().getReference(rutaImagen)
        ref.delete()
            .addOnSuccessListener {
                Constantes.toastConMensaje(context, "La imagen ha sido eliminada")
            }
            .addOnFailureListener { e->
                Constantes.toastConMensaje(context, "Ha habido un error al borrar la imagen")
                println(e.message) }
    }

    inner class HolderImagenSeleccionada(itemView: View) : ViewHolder(itemView)
    {
        var item_imagen = binding.itemImagen
        var boton_cerrar = binding.cerrarItem

    }
}