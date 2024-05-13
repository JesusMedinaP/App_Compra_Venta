package medina.jesus.app_compra_y_venta.Fragmentos

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import medina.jesus.app_compra_y_venta.Adaptadores.AdaptadorChats
import medina.jesus.app_compra_y_venta.Constantes
import medina.jesus.app_compra_y_venta.Modelo.Chats
import medina.jesus.app_compra_y_venta.R
import medina.jesus.app_compra_y_venta.databinding.FragmentChatsBinding

class FragmentChats : Fragment() {

    private lateinit var binding : FragmentChatsBinding
    private var uidUsuario = ""
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var chats : ArrayList<Chats>
    private lateinit var adaptadorChats: AdaptadorChats
    private lateinit var contexto : Context

    override fun onAttach(context: Context) {
        contexto = context
        super.onAttach(context)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()
        uidUsuario = "${firebaseAuth.uid}"

        cargarChats()
        binding.EtBuscar.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(filtro: CharSequence?, start: Int, before: Int, count: Int) {
                try {
                    val consulta = filtro.toString()
                    adaptadorChats.filter.filter(consulta)
                }catch (e : Exception){
                    println(e.message)
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }

    private fun cargarChats() {
        chats = ArrayList()
        val ref = Constantes.obtenerReferenciaChatsDB()
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                chats.clear()
                for(ds in snapshot.children){
                    val chatKey = "${ds.key}"
                    if(chatKey.contains(uidUsuario)){
                        val modeloChats = Chats()
                        modeloChats.keyChat = chatKey
                        chats.add(modeloChats)
                    }
                }

                adaptadorChats = AdaptadorChats(contexto, chats)
                binding.RvChats.adapter = adaptadorChats

                if(chats.isEmpty()){
                    binding.TvFeedback.visibility = View.VISIBLE
                }else{
                    binding.TvFeedback.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Constantes.toastConMensaje(contexto, "Ha habido un problema al cargar los chats")
                println(error.message)
            }
        })
    }
}