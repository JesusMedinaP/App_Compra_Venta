package medina.jesus.app_compra_y_venta.DetalleVendedor

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import medina.jesus.app_compra_y_venta.Adaptadores.AdaptadorAnuncio
import medina.jesus.app_compra_y_venta.Comentarios
import medina.jesus.app_compra_y_venta.Constantes
import medina.jesus.app_compra_y_venta.Modelo.Anuncio
import medina.jesus.app_compra_y_venta.R
import medina.jesus.app_compra_y_venta.databinding.ActivityDetalleVendedorBinding

class DetalleVendedor : AppCompatActivity() {

    private lateinit var binding : ActivityDetalleVendedorBinding
    private var uidVendedor = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetalleVendedorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        uidVendedor = intent.getStringExtra("uidVendedor").toString()

        cargarInfoVendedor()
        cargarAnunciosVendedor()

        binding.icRegresar.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.IvComentarios.setOnClickListener {
            val intent = Intent(this, Comentarios::class.java)
            intent.putExtra("uidVendedor", uidVendedor)
            startActivity(intent)
        }
    }

    private fun cargarAnunciosVendedor()
    {
        val anunciosArrayList : ArrayList<Anuncio> = ArrayList()

        val ref = Constantes.obtenerReferenciaAnunciosDB()
        ref.orderByChild("uid").equalTo(uidVendedor)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    anunciosArrayList.clear()
                    for(ds in snapshot.children){
                        try{
                            val modeloAnuncio = ds.getValue(Anuncio::class.java)
                            anunciosArrayList.add(modeloAnuncio!!)
                        }catch(e : Exception){
                            println(e.message)
                        }
                    }
                    val adaptador = AdaptadorAnuncio(this@DetalleVendedor, anunciosArrayList)
                    binding.anunciosRV.adapter = adaptador

                    val contadorAnuncios = "${anunciosArrayList.size}"
                    binding.TvNumAnuncios.text = contadorAnuncios

                }

                override fun onCancelled(error: DatabaseError) {
                    println(error.message)
                }
            })
    }
    private fun cargarInfoVendedor()
    {
        val ref = Constantes.obtenerReferenciaUsuariosDB()
        ref.child(uidVendedor)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val nombre = "${snapshot.child("nombres").value}"
                    val imagen = "${snapshot.child("urlImagenPerfil").value}"
                    val tiempo_registro = snapshot.child("tiempo").value as Long

                    val fecha = Constantes.obtenerFecha(tiempo_registro)

                    binding.TvNombres.text = nombre
                    binding.TvMiembro.text = fecha

                    try{
                        Glide.with(this@DetalleVendedor)
                            .load(imagen)
                            .placeholder(R.drawable.img_perfil)
                            .into(binding.IvVendedor)
                    }catch(e : Exception){
                        println(e.message)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    println(error.message)
                }
            })
    }
}