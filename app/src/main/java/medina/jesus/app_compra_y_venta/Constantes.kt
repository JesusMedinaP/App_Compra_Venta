package medina.jesus.app_compra_y_venta

import android.text.format.DateFormat
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

    public const val REFERENCIADB =
        "https://app-compra-y-venta-de42d-default-rtdb.europe-west1.firebasedatabase.app"
}