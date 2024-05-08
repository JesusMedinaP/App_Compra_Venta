package medina.jesus.app_compra_y_venta.DetalleVendedor

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import medina.jesus.app_compra_y_venta.Constantes
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

        binding.icRegresar.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
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