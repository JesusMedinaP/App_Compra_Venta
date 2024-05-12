package medina.jesus.app_compra_y_venta.Adaptadores

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import medina.jesus.app_compra_y_venta.Constantes
import medina.jesus.app_compra_y_venta.Modelo.Chat
import medina.jesus.app_compra_y_venta.R

class AdaptadorChat : RecyclerView.Adapter<AdaptadorChat.HolderChat>{

    private val context : Context
    private val chats : ArrayList<Chat>
    private val firebaseAuth : FirebaseAuth
    private var rutaChat = ""

    companion object{
        private const val MENSAJE_IZQUIERDO = 0
        private const val MENSAJE_DERECHO = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderChat {
        if(viewType == MENSAJE_DERECHO){
            val view = LayoutInflater.from(context).inflate(R.layout.item_chat_derecho, parent, false)
            return HolderChat(view)
        }else{
            val view = LayoutInflater.from(context).inflate(R.layout.item_chat_izquierdo, parent, false)
            return HolderChat(view)
        }
    }

    override fun getItemCount(): Int {
        return chats.size
    }

    override fun getItemViewType(position: Int): Int {
        if(chats[position].emisorUid == firebaseAuth.uid){
            return MENSAJE_DERECHO
        }else{
            return MENSAJE_IZQUIERDO
        }
    }

    override fun onBindViewHolder(holder: HolderChat, position: Int) {
        val modeloChat = chats[position]

        val mensaje = modeloChat.mensaje
        val tipoMensaje = modeloChat.tipoMensaje
        val tiempo = modeloChat.tiempo

        val formatoFechaHora = Constantes.obtenerFechaHora(tiempo)
        holder.Tv_tiempo_mensaje.text = formatoFechaHora

        if(tipoMensaje == Constantes.MENSAJE_TIPO_TEXTO){
            holder.Tv_mensaje.visibility = View.VISIBLE
            holder.Iv_mensaje.visibility = View.GONE
            holder.Tv_mensaje.text = mensaje

            if(modeloChat.emisorUid.equals(firebaseAuth.uid)){
                holder.itemView.setOnClickListener {
                    val opciones = arrayOf<CharSequence>("Eliminar Mensaje", "Cancelar")
                    val builder : AlertDialog.Builder = AlertDialog.Builder(holder.itemView.context)
                    builder
                    builder.setTitle("¿Qué desea realizar?")
                    builder.setItems(opciones, DialogInterface.OnClickListener { dialogInterface, i ->
                        if(i == 0){
                            eliminarMensaje(position, holder, modeloChat)
                        }
                    })
                    builder.show()
                }
            }
        }else{
            holder.Tv_mensaje.visibility = View.GONE
            holder.Iv_mensaje.visibility = View.VISIBLE

            try {
                Glide.with(context)
                    .load(mensaje)
                    .placeholder(R.drawable.imagen_chat)
                    .error(R.drawable.imagen_chat_falla)
                    .into(holder.Iv_mensaje)
            }catch (e : Exception){
                println(e.message)
            }

            if(modeloChat.emisorUid.equals(firebaseAuth.uid)){
                holder.itemView.setOnClickListener {
                    val opciones = arrayOf<CharSequence>("Eliminar imagen", "Ver imagen completa", "Cancelar")
                    val builder : AlertDialog.Builder = AlertDialog.Builder(holder.itemView.context)
                    builder.setTitle("¿Qué desea realizar?")
                    builder.setItems(opciones, DialogInterface.OnClickListener{dialogIntercae, i ->
                        if(i == 0){
                            eliminarMensaje(position, holder, modeloChat)
                        }else if(i == 1){
                            //Visualizar imagen
                        }
                    })
                    builder.show()
                }
            }else if(!modeloChat.emisorUid.equals(firebaseAuth)){
                holder.itemView.setOnClickListener {
                    val opciones = arrayOf<CharSequence>("Ver imagen completa", "Cancelar")
                    val builder : AlertDialog.Builder = AlertDialog.Builder(holder.itemView.context)
                    builder.setTitle("¿Qué desea realizar?")
                    builder.setItems(opciones, DialogInterface.OnClickListener{dialogIntercae, i ->
                        if(i == 0){
                            //Visualizar imagen
                        }
                    })
                    builder.show()
                }
            }
        }
    }

    private fun eliminarMensaje(position: Int, holder: HolderChat, modeloChat: Chat) {
        rutaChat = Constantes.rutaChat(modeloChat.receptorUid, modeloChat.emisorUid)
        val ref = Constantes.obtenerReferenciaChatsDB()
        ref.child(rutaChat).child(chats.get(position).idMensaje)
            .removeValue()
            .addOnSuccessListener {
                Constantes.toastConMensaje(holder.itemView.context, "Mensaje eliminado")
            }
            .addOnFailureListener { e->
                Constantes.toastConMensaje(holder.itemView.context, "No se pudo eliminar el mensaje")
                println(e.message)
            }
    }

    constructor(context: Context, chats: ArrayList<Chat>) {
        this.context = context
        this.chats = chats
        firebaseAuth = FirebaseAuth.getInstance()
    }

    inner class HolderChat(itemView : View) : RecyclerView.ViewHolder(itemView){
        var Tv_mensaje : TextView = itemView.findViewById(R.id.Tv_Mensaje)
        var Iv_mensaje : ShapeableImageView = itemView.findViewById(R.id.Iv_Mensaje)
        var Tv_tiempo_mensaje : TextView = itemView.findViewById(R.id.Tv_Tiempo_Mensaje)
    }
}