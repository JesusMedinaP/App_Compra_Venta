package medina.jesus.app_compra_y_venta

import android.content.Context
import android.text.format.DateFormat
import android.widget.Toast
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

    private const val REFERENCIADB =
        "https://app-compra-y-venta-de42d-default-rtdb.europe-west1.firebasedatabase.app"

    public fun toastConMensaje(context : Context, mensaje : String) {
        Toast.makeText(
            context, mensaje,
            Toast.LENGTH_SHORT
        ).show()
    }

    fun obtenerReferenciaUsuariosDB(): DatabaseReference {
        return FirebaseDatabase.getInstance(Constantes.REFERENCIADB).getReference("Usuarios")
    }

    fun obtenerReferenciaAnunciosDB(): DatabaseReference {
        return FirebaseDatabase.getInstance(Constantes.REFERENCIADB).getReference("Anuncios")
    }
}