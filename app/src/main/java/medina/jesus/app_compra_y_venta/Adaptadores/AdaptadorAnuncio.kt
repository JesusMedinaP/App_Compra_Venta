package medina.jesus.app_compra_y_venta.Adaptadores

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import medina.jesus.app_compra_y_venta.Constantes
import medina.jesus.app_compra_y_venta.DetalleAnuncio.DetalleAnuncio
import medina.jesus.app_compra_y_venta.Filtro.FiltrarAnuncio
import medina.jesus.app_compra_y_venta.Modelo.Anuncio
import medina.jesus.app_compra_y_venta.R
import medina.jesus.app_compra_y_venta.databinding.ItemAnuncioNuevaVersionBinding

class AdaptadorAnuncio: RecyclerView.Adapter<AdaptadorAnuncio.HolderAnuncio>, Filterable {

    private lateinit var binding: ItemAnuncioNuevaVersionBinding

    private var context : Context
    var anuncioArrayList : ArrayList<Anuncio>
    private var firebaseAuth : FirebaseAuth
    private var filtroLista : ArrayList<Anuncio>
    private var filtro: FiltrarAnuncio ?= null

    constructor(context: Context, anuncioArrayList: ArrayList<Anuncio>) {
        this.context = context
        this.anuncioArrayList = anuncioArrayList
        firebaseAuth = FirebaseAuth.getInstance()
        this.filtroLista = anuncioArrayList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderAnuncio {
        binding = ItemAnuncioNuevaVersionBinding.inflate(LayoutInflater.from(context), parent,false)
        return HolderAnuncio(binding.root)
    }

    override fun getItemCount(): Int {
        return anuncioArrayList.size
    }

    override fun onBindViewHolder(holder: HolderAnuncio, position: Int) {
        val modeloAnuncio = anuncioArrayList[position]
        val titulo = modeloAnuncio.titulo
        val descripcion = modeloAnuncio.descripcion
        val direccion = modeloAnuncio.direccion
        val condicion = modeloAnuncio.condicion
        val precio = modeloAnuncio.precio
        val tiempo = modeloAnuncio.tiempo

        val formatoFecha = Constantes.obtenerFecha(tiempo)

        cargarPrimeraImagenAnuncio(modeloAnuncio, holder)
        comprobarFavorito(modeloAnuncio, holder)

        holder.Tv_titulo.text = titulo
        holder.Tv_descripcion.text = descripcion
        holder.Tv_direccion.text = direccion
        holder.Tv_condicion.text = condicion
        holder.Tv_precio.text = precio
        holder.Tv_fecha.text = formatoFecha

        holder.itemView.setOnClickListener {
            val intent = Intent(context, DetalleAnuncio::class.java)
            intent.putExtra("idAnuncio", modeloAnuncio.id)
            context.startActivity(intent)
        }

        holder.Ib_fav.setOnClickListener {
            val favorito = modeloAnuncio.favorito
            //Alternamos el comportamiento si ya era favorito.
            if(favorito){
                Constantes.eliminarAnuncioFavoritos(context, modeloAnuncio.id)
            }else{
                Constantes.agregarAnuncioFavoritos(context, modeloAnuncio.id)
            }
        }
    }

    private fun comprobarFavorito(modeloAnuncio: Anuncio, holder: HolderAnuncio) {
        val ref = Constantes.obtenerReferenciaUsuariosDB()
        ref.child(firebaseAuth.uid!!).child("Favoritos").child(modeloAnuncio.id)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val favorito = snapshot.exists()
                    modeloAnuncio.favorito = favorito

                    if(favorito){
                        holder.Ib_fav.setImageResource(R.drawable.ic_anuncio_favorito)
                    }else{
                        holder.Ib_fav.setImageResource(R.drawable.ic_no_favorito)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    println(error.message)
                }
            })
    }

    private fun cargarPrimeraImagenAnuncio(
        modeloAnuncio: Anuncio,
        holder: AdaptadorAnuncio.HolderAnuncio
    ) {
        val idAnuncio = modeloAnuncio.id
        val ref = Constantes.obtenerReferenciaAnunciosDB()
        ref.child(idAnuncio).child("Imagenes").limitToFirst(1)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for(ds in snapshot.children){
                        val imagenUrl = "${ds.child("imagenUrl").value}"
                        try{
                            Glide.with(context)
                                .load(imagenUrl)
                                .placeholder(R.drawable.ic_imagen)
                                .into(holder.imagenIv)
                        }catch (e:Exception)
                        {
                            Constantes.toastConMensaje(context, "${e.message}")
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    print(error.message)
                }
            })
    }

    inner class HolderAnuncio(itemView : View): RecyclerView.ViewHolder(itemView){
        var imagenIv = binding.imagenIv
        var Tv_titulo = binding.TvTitulo
        var Tv_descripcion = binding.TvDescripcion
        var Tv_direccion = binding.TvDireccion
        var Tv_condicion = binding.TvCondicion
        var Tv_precio = binding.TvPrecio
        var Tv_fecha = binding.TvFecha
        var Ib_fav = binding.IbFav
    }

    override fun getFilter(): Filter {
        if(filtro == null){
            filtro = FiltrarAnuncio(this, filtroLista)
        }
        return filtro as FiltrarAnuncio
    }

}