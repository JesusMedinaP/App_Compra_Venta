package medina.jesus.app_compra_y_venta.Adaptadores

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.github.chrisbanes.photoview.PhotoView
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView
import medina.jesus.app_compra_y_venta.Constantes
import medina.jesus.app_compra_y_venta.Modelo.ImagenSlider
import medina.jesus.app_compra_y_venta.R
import medina.jesus.app_compra_y_venta.databinding.ItemImagenSliderBinding

class AdaptadorImagenSlider : RecyclerView.Adapter<AdaptadorImagenSlider.HolderImagenSlider> {

    private lateinit var binding : ItemImagenSliderBinding
    private var context : Context
    private var imagenesArrayList : ArrayList<ImagenSlider>

    constructor(context: Context, imagenesArrayList: ArrayList<ImagenSlider>) {
        this.context = context
        this.imagenesArrayList = imagenesArrayList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderImagenSlider {
        binding = ItemImagenSliderBinding.inflate(LayoutInflater.from(context), parent, false)
        return HolderImagenSlider(binding.root)
    }

    override fun getItemCount(): Int {
        return imagenesArrayList.size
    }

    override fun onBindViewHolder(holder: HolderImagenSlider, position: Int) {
        val modeloImagenSlider = imagenesArrayList[position]

        val imagenUrl = modeloImagenSlider.imagenUrl
        val imagenContador = "${position+1} / ${imagenesArrayList.size}"

        holder.imagenContadorTv.text = imagenContador
        try {
            Glide.with(context)
                .load(imagenUrl)
                .placeholder(R.drawable.ic_imagen)
                .into(holder.imagenIv)
        }catch(e : Exception){
            Constantes.toastConMensaje(context, "${e.message}")
            println(e.message)
        }

        holder.itemView.setOnClickListener {
            visualizarImagen(modeloImagenSlider.imagenUrl)
        }
    }

    private fun visualizarImagen(imagen : String)
    {
        val Pv : PhotoView
        val Btn_cerrar : MaterialButton

        val dialog = Dialog(context)
        dialog.setContentView(R.layout.cuadro_dialogo_visualizar_imagen)

        Pv = dialog.findViewById(R.id.Pv_Imagen)
        Btn_cerrar = dialog.findViewById(R.id.Btn_Cerrar_Visualizar)

        try{
            Glide.with(context)
                .load(imagen)
                .placeholder(R.drawable.imagen_chat)
                .into(Pv)
        }catch (e : Exception){
            println(e.message)
        }

        Btn_cerrar.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
        dialog.setCanceledOnTouchOutside(false)
    }

    inner class HolderImagenSlider(itemView : View) : RecyclerView.ViewHolder(itemView){
        var imagenIv : ShapeableImageView = binding.ImagenIV
        var imagenContadorTv : TextView = binding.imagenContadorTv
    }
}