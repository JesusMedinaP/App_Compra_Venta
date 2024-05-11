package medina.jesus.app_compra_y_venta.Adaptadores

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import medina.jesus.app_compra_y_venta.Chat.Chat
import medina.jesus.app_compra_y_venta.Constantes
import medina.jesus.app_compra_y_venta.Modelo.Chats
import medina.jesus.app_compra_y_venta.R
import medina.jesus.app_compra_y_venta.databinding.ItemChatsBinding

class AdaptadorChats : RecyclerView.Adapter<AdaptadorChats.HolderChats> {

    private var context : Context
    private var chats : ArrayList<Chats>
    private lateinit var binding : ItemChatsBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var uidUsuario = ""

    constructor(context: Context, chats: ArrayList<Chats>) {
        this.context = context
        this.chats = chats
        firebaseAuth = FirebaseAuth.getInstance()
        uidUsuario = firebaseAuth.uid!!
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderChats {
        binding = ItemChatsBinding.inflate(LayoutInflater.from(context), parent, false)
        return HolderChats(binding.root)
    }

    override fun getItemCount(): Int {
        return chats.size
    }

    override fun onBindViewHolder(holder: HolderChats, position: Int) {
        val modeloChats = chats[position]

        cargarUltimoMensaje(modeloChats, holder)

        holder.itemView.setOnClickListener {
            val uidRecibido = modeloChats.uidRecibido
            if(uidRecibido!=null){
                val intent = Intent(context, Chat::class.java)
                intent.putExtra("uidVendedor", uidRecibido)
                context.startActivity(intent)
            }
        }
    }

    private fun cargarUltimoMensaje(modeloChats: Chats, holder: HolderChats) {
        val chatKey = modeloChats.keyChat

        val ref = Constantes.obtenerReferenciaChatsDB()
        ref.child(chatKey).limitToLast(1)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (ds in snapshot.children){
                        val emisorUid = "${ds.child("emisorUid").value}"
                        val idMensaje = "${ds.child("idMensaje").value}"
                        val mensaje = "${ds.child("mensaje").value}"
                        val receptorUid = "${ds.child("receptorUid").value}"
                        val tiempo = ds.child("tiempo").value as Long
                        val tipo_mensaje = "${ds.child("tipoMensaje").value}"

                        val formatoFechaHora = Constantes.obtenerFechaHora(tiempo)

                        modeloChats.uidEmisor = emisorUid
                        modeloChats.idMensaje = idMensaje
                        modeloChats.mensaje = mensaje
                        modeloChats.uidReceptor = receptorUid
                        modeloChats.tipoMensaje = tipo_mensaje

                        holder.Tv_fecha.text = "$formatoFechaHora"

                        if(tipo_mensaje == Constantes.MENSAJE_TIPO_TEXTO){
                            holder.Tv_ultimo_mensaje.text = mensaje
                        }else{
                            holder.Tv_ultimo_mensaje.text = "Foto"
                        }

                        cargarInfoUsuarioRecibido(modeloChats, holder)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Constantes.toastConMensaje(context, "Ha habido un problema al cargar el último mensaje")
                    println(error.message)
                }
            })
    }

    private fun cargarInfoUsuarioRecibido(modeloChats: Chats, holder: HolderChats) {
        val emisorUid = modeloChats.uidEmisor
        val receptorUid = modeloChats.uidReceptor

        var uidRecibido = ""
        if(emisorUid == uidUsuario){
            uidRecibido = receptorUid
        }else{
            uidRecibido = emisorUid
        }

        modeloChats.uidRecibido = uidRecibido

        val ref = Constantes.obtenerReferenciaUsuariosDB()
        ref.child(uidRecibido)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val nombres = "${snapshot.child("nombres").value}"
                    val imagen = "${snapshot.child("urlImagenPerfil").value}"

                    modeloChats.nombre = nombres
                    modeloChats.urlImagenPerfil = imagen

                    holder.Tv_nombres.text = nombres
                    try{
                        Glide.with(context)
                            .load(imagen)
                            .placeholder(R.drawable.img_perfil)
                            .into(holder.Iv_perfil)
                    }catch(e:Exception){
                        Constantes.toastConMensaje(context, "Ha habido un problema al cargar la imagen")
                        println(e.message)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Constantes.toastConMensaje(context, "Ha habido un problema al obtener la imagen")
                    println(error.message)
                }
            })
    }

    inner class HolderChats(itemView : View) : RecyclerView.ViewHolder(itemView){
        var Iv_perfil = binding.IvPerfil
        var Tv_nombres = binding.TvNombres
        var Tv_ultimo_mensaje = binding.TvUltimoMensaje
        var Tv_fecha = binding.TvFecha
    }
}