package medina.jesus.app_compra_y_venta

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.format.DateFormat
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.Calendar
import java.util.Locale

object Constantes {

    const val ANUNCIO_DISPONIBLE = "Disponible"
    const val ANUNCIO_VENDIDO = "Vendido"


    val categorias = arrayOf(
        "Todos",
        "Móviles",
        "Ordenador/Portatil",
        "Electrónica y electrodomésticos",
        "Vehículos",
        "Consolas y Videojuegos",
        "Hogar y muebles",
        "Belleza y cuidado personal",
        "Libros",
        "Deportes",
        "Juguetes y figuras",
        "Mascotas"
    )

    val categoriasIcono = arrayOf(
        R.drawable.ic_categoria_todos,
        R.drawable.ic_categoria_moviles,
        R.drawable.ic_categoria_ordenadores,
        R.drawable.ic_categoria_electrodomesticos,
        R.drawable.ic_categoria_vehiculos,
        R.drawable.ic_categoria_videojuegos,
        R.drawable.ic_categoria_muebles,
        R.drawable.ic_categoria_belleza,
        R.drawable.ic_categoria_libros,
        R.drawable.ic_categoria_deportes,
        R.drawable.ic_categoria_juguetes,
        R.drawable.ic_categoria_mascotas
    )

    val condiciones = arrayOf(
        "Nuevo",
        "Usado",
        "Renovado"
    )

    fun obtenerTiempoDis(): Long
    {
        return System.currentTimeMillis()
    }

    fun obtenerFecha(tiempo : Long) : String
    {
        val calendario = Calendar.getInstance(Locale.ENGLISH)
        calendario.timeInMillis = tiempo

        return DateFormat.format("dd/MM/yyyy", calendario).toString()
    }

    fun agregarAnuncioFavoritos(context: Context, idAnuncio: String)
    {
        val firebaseAuth = FirebaseAuth.getInstance()
        val tiempo = obtenerTiempoDis()

        val hashMap = HashMap<String, Any>()
        hashMap["idAnuncio"] = idAnuncio
        hashMap["tiempo"] = tiempo

        val ref = obtenerReferenciaUsuariosDB()
        ref.child(firebaseAuth.uid!!).child("Favoritos").child(idAnuncio)
            .setValue(hashMap)
            .addOnSuccessListener {
                toastConMensaje(context, "Anuncio agregado a favoritos")
            }
            .addOnFailureListener { e-> toastConMensaje(context, "${e.message}") }
    }

    fun eliminarAnuncioFavoritos(context: Context, idAnuncio: String)
    {
        val firebaseAuth = FirebaseAuth.getInstance()
        val ref = obtenerReferenciaUsuariosDB()
        ref.child(firebaseAuth.uid!!).child("Favoritos").child(idAnuncio)
            .removeValue()
            .addOnSuccessListener {
                toastConMensaje(context, "Anuncio eliminado de favoritos")
            }
            .addOnFailureListener { e-> toastConMensaje(context, "${e.message}") }
    }

    private const val REFERENCIADB =
        "https://app-compra-y-venta-de42d-default-rtdb.europe-west1.firebasedatabase.app"

    public fun toastConMensaje(context : Context, mensaje : String) {
        Toast.makeText(
            context, mensaje,
            Toast.LENGTH_SHORT
        ).show()
    }

    fun obtenerReferenciaUsuariosDB(): DatabaseReference {
        return FirebaseDatabase.getInstance(REFERENCIADB).getReference("Usuarios")
    }

    fun obtenerReferenciaAnunciosDB(): DatabaseReference {
        return FirebaseDatabase.getInstance(REFERENCIADB).getReference("Anuncios")
    }

    fun mapaIntent(context: Context, latitud : Double, longitud : Double)
    {
        val googleMapaIntentUri = Uri.parse("http://maps.google.com/maps?daddr=$latitud,$longitud")

        val mapIntent = Intent(Intent.ACTION_VIEW, googleMapaIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        if(mapIntent.resolveActivity(context.packageManager)!=null){
            /*La app de Google Maps está instalada*/
            context.startActivity(mapIntent)
        }else{
            toastConMensaje(context, "No tienes instalado Google Maps")
        }
    }

    fun llamarIntent (context: Context, telef : String)
    {
        val intent = Intent(Intent.ACTION_CALL)
        intent.setData(Uri.parse("tel:${telef}"))
        context.startActivity(intent)
    }
}