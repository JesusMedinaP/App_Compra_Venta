package medina.jesus.app_compra_y_venta

import android.content.Context
import android.text.format.DateFormat
import android.widget.Toast
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.Calendar
import java.util.Locale

object Constantes {

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

    public fun obtenerReferenciaUsuariosDB(): DatabaseReference {
        return FirebaseDatabase.getInstance(Constantes.REFERENCIADB).getReference("Usuarios")
    }
}