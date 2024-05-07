package medina.jesus.app_compra_y_venta.DetalleAnuncio

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import medina.jesus.app_compra_y_venta.Adaptadores.AdaptadorImagenSlider
import medina.jesus.app_compra_y_venta.Constantes
import medina.jesus.app_compra_y_venta.MainActivity
import medina.jesus.app_compra_y_venta.Modelo.Anuncio
import medina.jesus.app_compra_y_venta.Modelo.ImagenSlider
import medina.jesus.app_compra_y_venta.R
import medina.jesus.app_compra_y_venta.databinding.ActivityDetalleAnuncioBinding

class DetalleAnuncio : AppCompatActivity() {

    private lateinit var binding : ActivityDetalleAnuncioBinding
    private lateinit var firebaseAuth : FirebaseAuth
    private var idAnuncio = ""
    private var anuncioLatitud = 0.0
    private var anuncioLongitud = 0.0

    private var uidVendedor = ""
    private var telVendedor = ""

    private var favorito = false

    private lateinit var imagenSliderArrayList : ArrayList<ImagenSlider>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetalleAnuncioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        idAnuncio = intent.getStringExtra("idAnuncio").toString()

        comprobarFavorito()
        cargarInfo()
        cargarImagenesAnuncio()

        binding.IbRegresar.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.IbFav.setOnClickListener {
            if(favorito){
                Constantes.eliminarAnuncioFavoritos(this, idAnuncio)
            }else{
                Constantes.agregarAnuncioFavoritos(this, idAnuncio)
            }
        }

        binding.IbEliminar.setOnClickListener {
            val alertDialog = MaterialAlertDialogBuilder(this)
            alertDialog.setTitle("Eliminar Anuncio")
                .setMessage("¿Estás seguro de eliminar este anuncio?")
                .setPositiveButton("Eliminar"){ dialog, which ->
                    eliminarAnuncio()
                }
                .setNegativeButton("Cancelar"){ dialog, which ->
                    dialog.dismiss()
                }.show()
        }

        binding.BtnMapa.setOnClickListener {
            Constantes.mapaIntent(this, anuncioLatitud, anuncioLongitud)
        }

        binding.BtnLlamar.setOnClickListener {
//            val numeroTelefono = telVendedor
            if(telVendedor.isEmpty())
            {
                Constantes.toastConMensaje(this@DetalleAnuncio, "El vendedor no tiene número telefónico")
            }else{
                Constantes.llamarIntent(this, telVendedor)
            }
        }
    }

    private fun cargarInfo()
    {
        val ref = Constantes.obtenerReferenciaAnunciosDB()
        ref.child(idAnuncio)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    try{
                        val modeloAnuncio = snapshot.getValue(Anuncio::class.java)

                        uidVendedor = "${modeloAnuncio!!.uid}"
                        val titulo = modeloAnuncio.titulo
                        val descripcion = modeloAnuncio.descripcion
                        val direccion = modeloAnuncio.direccion
                        val condicion = modeloAnuncio.condicion
                        val categoria = modeloAnuncio.categoria
                        val precio = modeloAnuncio.precio
                        anuncioLatitud = modeloAnuncio.latitud
                        anuncioLongitud = modeloAnuncio.longitud
                        val tiempo = modeloAnuncio.tiempo

                        val formatoFecha = Constantes.obtenerFecha(tiempo)

                        if(uidVendedor == firebaseAuth.uid)
                        {
                            binding.IbEditar.visibility = View.VISIBLE
                            binding.IbEliminar.visibility = View.VISIBLE

                            binding.BtnMapa.visibility = View.GONE
                            binding.BtnLlamar.visibility = View.GONE
                            binding.BtnSMS.visibility = View.GONE
                            binding.BtnChat.visibility = View.GONE
                        }else{
                            binding.IbEditar.visibility = View.GONE
                            binding.IbEliminar.visibility = View.GONE

                            binding.BtnMapa.visibility = View.VISIBLE
                            binding.BtnLlamar.visibility = View.VISIBLE
                            binding.BtnSMS.visibility = View.VISIBLE
                            binding.BtnChat.visibility = View.VISIBLE
                        }

                        binding.TvTitulo.text = titulo
                        binding.TvDescripcionDetalle.text = descripcion
                        binding.TvDireccion.text = direccion
                        binding.TvCondicion.text = condicion
                        binding.TvCategoria.text = categoria
                        binding.TvPrecio.text = precio
                        binding.TvFecha.text = formatoFecha

                        cargarInfoVendedor()

                    }catch (e : Exception){
                        println(e.message)
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    println(error.message)
                }
            })
    }

    private fun cargarInfoVendedor() {
        val ref = Constantes.obtenerReferenciaUsuariosDB()
        ref.child(uidVendedor)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val telefono = "${snapshot.child("telefono").value}"
                    val codTel = "${snapshot.child("codigoTelefono").value}"
                    val nombres = "${snapshot.child("nombres").value}"
                    val imagenPerfil = "${snapshot.child("urlImagenPerfil").value}"
                    val tiempo_registro = snapshot.child("tiempo").value as Long

                    val formatoFecha = Constantes.obtenerFecha(tiempo_registro)

                    telVendedor = "$codTel$telefono"

                    binding.TvNombres.text = nombres
                    binding.TvMiembro.text = formatoFecha

                    try{
                        Glide.with(this@DetalleAnuncio)
                            .load(imagenPerfil)
                            .placeholder(R.drawable.img_perfil)
                            .into(binding.imgPerfil)
                    }catch (e : Exception){
                        println(e.message)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    println(error.message)
                }
            })
    }

    private fun cargarImagenesAnuncio()
    {
        imagenSliderArrayList = ArrayList()

        val ref = Constantes.obtenerReferenciaAnunciosDB()
        ref.child(idAnuncio).child("Imagenes")
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    imagenSliderArrayList.clear()
                    for(ds in snapshot.children){
                        try{
                            val modeloImagenSlider = ds.getValue(ImagenSlider::class.java)
                            imagenSliderArrayList.add(modeloImagenSlider!!)
                        }catch(e : Exception){
                            println(e.message)
                        }
                    }

                    val adaptadorImagenSlider = AdaptadorImagenSlider(this@DetalleAnuncio, imagenSliderArrayList)
                    binding.ImagenSliderVP.adapter = adaptadorImagenSlider
                }

                override fun onCancelled(error: DatabaseError) {
                    println(error.message)
                }
            })
    }

    fun comprobarFavorito()
    {
        val ref = Constantes.obtenerReferenciaUsuariosDB()
        ref.child("${firebaseAuth.uid}").child("Favoritos").child(idAnuncio)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    favorito = snapshot.exists()
                    if(favorito){
                        binding.IbFav.setImageResource(R.drawable.ic_anuncio_favorito)
                    }else{
                        binding.IbFav.setImageResource(R.drawable.ic_no_favorito)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    println(error.message)
                }
            })
    }


    private fun eliminarAnuncio()
    {
        val ref = Constantes.obtenerReferenciaAnunciosDB()
        ref.child(idAnuncio)
            .removeValue()
            .addOnSuccessListener {
                startActivity((Intent(this@DetalleAnuncio, MainActivity::class.java)))
                finishAffinity()
                Constantes.toastConMensaje(this, "Se eliminó el anuncio con éxito")

            }
            .addOnFailureListener { e->
                Constantes.toastConMensaje(this, "${e.message}")
            }
    }
}