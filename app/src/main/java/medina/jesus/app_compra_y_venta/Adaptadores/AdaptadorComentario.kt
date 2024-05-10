package medina.jesus.app_compra_y_venta.Adaptadores

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import medina.jesus.app_compra_y_venta.Constantes
import medina.jesus.app_compra_y_venta.Modelo.Comentario
import medina.jesus.app_compra_y_venta.R
import medina.jesus.app_compra_y_venta.databinding.ItemComentarioBinding

class AdaptadorComentario: RecyclerView.Adapter<AdaptadorComentario.HolderComentario> {
    val context : Context
    val comentarios : ArrayList<Comentario>

    private lateinit var binding : ItemComentarioBinding

    constructor(context: Context, comentarios: ArrayList<Comentario>) {
        this.context = context
        this.comentarios = comentarios
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderComentario {
        binding = ItemComentarioBinding.inflate(LayoutInflater.from(context), parent, false)
        return HolderComentario(binding.root)
    }

    override fun getItemCount(): Int {
        return comentarios.size
    }

    override fun onBindViewHolder(holder: HolderComentario, position: Int) {
        val modelo = comentarios[position]

        val id = modelo.id
        val comentario = modelo.comentario
        val uid = modelo.uid
        val tiempo = modelo.tiempo

        val formatoFecha = Constantes.obtenerFecha(tiempo.toLong())

        holder.Tv_fecha.text = formatoFecha
        holder.Tv_comentario.text = comentario

        cargarInformacion(modelo, holder)
    }

    private fun cargarInformacion(
        modelo: Comentario,
        holder: HolderComentario
    ) {
        val uid = modelo.uid
        val ref = Constantes.obtenerReferenciaUsuariosDB()
        ref.child(uid)
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val nombres = "${snapshot.child("nombres").value}"
                    val imagen = "${snapshot.child("urlImagenPerfil").value}"

                    holder.Tv_nombre.text = nombres

                    try {
                        Glide.with(context)
                            .load(imagen)
                            .placeholder(R.drawable.img_perfil)
                            .into(holder.IvImagen)
                    }catch (e:Exception){
                        holder.IvImagen.setImageResource(R.drawable.img_perfil)
                        Constantes.toastConMensaje(context, "Ha habido un error al cargar la imagen de perfil")
                        println(e.message)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    println(error.message)
                }
            })
    }

    inner class HolderComentario (itemView: View) : RecyclerView.ViewHolder(itemView){
        val IvImagen = binding.IvImagenPerfil
        val Tv_nombre = binding.TvNombreComentarios
        val Tv_fecha = binding.TvFechaComentario
        val Tv_comentario = binding.TvComentario

    }
}