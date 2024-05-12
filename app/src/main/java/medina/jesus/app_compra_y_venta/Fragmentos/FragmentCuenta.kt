package medina.jesus.app_compra_y_venta.Fragmentos

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import medina.jesus.app_compra_y_venta.CambiarPassword
import medina.jesus.app_compra_y_venta.Constantes
import medina.jesus.app_compra_y_venta.EditarPerfil
import medina.jesus.app_compra_y_venta.EliminarCuenta
import medina.jesus.app_compra_y_venta.OpcionesLogin
import medina.jesus.app_compra_y_venta.R
import medina.jesus.app_compra_y_venta.databinding.FragmentCuentaBinding

class FragmentCuenta : Fragment() {

    private lateinit var binding : FragmentCuentaBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var contexto : Context
    private lateinit var progressDialog: ProgressDialog


    //Método para poder obtener el contexto ya que
    //estamos en un fragment y no lo tiene.
    override fun onAttach(context: Context) {
         contexto = context
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCuentaBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressDialog = ProgressDialog(contexto)
        progressDialog.setTitle("Espere por favor")
        progressDialog.setCanceledOnTouchOutside(false)

        firebaseAuth = FirebaseAuth.getInstance()

        leerInfo()

        binding.BtnEditarPerfil.setOnClickListener {
            startActivity(Intent(contexto, EditarPerfil::class.java))
        }

        //Método para poder cerrar sesión y volver a la vista de login.
        binding.BtnCerrarSesion.setOnClickListener {
            firebaseAuth.signOut()
            startActivity(Intent(contexto, OpcionesLogin::class.java))
            activity?.finishAffinity()
        }

        binding.BtnCambiarPasssword.setOnClickListener {
            startActivity(Intent(contexto, CambiarPassword::class.java))
        }

        binding.BtnVerificarCuenta.setOnClickListener {
            verificarCuenta()
        }

        binding.BtnEliminarAnuncios.setOnClickListener {
            val alertDialog = MaterialAlertDialogBuilder(contexto)
            alertDialog.setTitle("Eliminar todos mis anuncios")
                .setMessage("¿Estás seguro de eliminar todos tus anuncios?")
                .setPositiveButton("Eliminar"){dialog, which->
                    eliminarMisAnuncios()
                }
                .setNegativeButton("Cancelar"){dialog, which->
                    dialog.dismiss()
                }
                .show()
        }

        binding.BtnEliminarCuenta.setOnClickListener {
            startActivity(Intent(contexto, EliminarCuenta::class.java))
        }
    }

    private fun eliminarMisAnuncios() {
        val uidUsuario = firebaseAuth.uid
        val ref = Constantes.obtenerReferenciaAnunciosDB().orderByChild("uid").equalTo(uidUsuario)
        ref.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(ds in snapshot.children){
                    ds.ref.removeValue()
                }
                Constantes.toastConMensaje(contexto, "Se han eliminado todos sus anuncios")
            }

            override fun onCancelled(error: DatabaseError) {
                println(error.message)
                Constantes.toastConMensaje(contexto,"Ha habido un error al borrar tus anuncios")
            }
        })
    }

    private fun leerInfo() {
        val ref = Constantes.obtenerReferenciaUsuariosDB()
        ref.child("${firebaseAuth.uid}")
            .addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val nombres = "${snapshot.child("nombres").value}"
                    val email = "${snapshot.child("email").value}"
                    val imagen = "${snapshot.child("urlImagenPerfil").value}"
                    val f_nacimiento = "${snapshot.child("fecha_nac").value}"
                    var tiempo = "${snapshot.child("tiempo").value}"
                    val telefono = "${snapshot.child("telefono").value}"
                    val codTelefono = "${snapshot.child("codigoTelefono").value}"
                    val proveedor = "${snapshot.child("proveedor").value}"

                    val cod_tel = codTelefono + telefono

                    if(tiempo == "null")
                    {
                        tiempo = "0"
                    }

                    val for_tiempo = Constantes.obtenerFecha(tiempo.toLong())

                    //Obtenemos la información y la volcamos de los TextView
                    binding.TvEmail.text = email
                    binding.TvNacimiento.text = f_nacimiento
                    binding.TvNombres.text = nombres
                    binding.TvTelefono.text = cod_tel
                    binding.TvMiembro.text = for_tiempo

                    //Obtención de la imagen con Glide
                    try {
                        Glide.with(contexto)
                            .load(imagen)
                            .placeholder(R.drawable.img_perfil)
                            .into(binding.IvPerfil)
                    }catch (e : Exception)
                    {
                        Constantes.toastConMensaje(contexto,
                            "${e.message}")
                    }

                    if(proveedor == "Email")
                    {
                        val esVerificado = firebaseAuth.currentUser!!.isEmailVerified
                        if(esVerificado)
                        {
                            binding.BtnVerificarCuenta.visibility = View.GONE
                            binding.TvEstadoCuenta.text = "Verificado"
                        }else{
                            binding.BtnVerificarCuenta.visibility = View.VISIBLE
                            binding.TvEstadoCuenta.text = "No Verificado"
                        }
                    }else{
                        binding.BtnVerificarCuenta.visibility = View.GONE
                        binding.TvEstadoCuenta.text = "Verificado"
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    private fun verificarCuenta(){
        progressDialog.setMessage("Enviando instrucciones de verificación a su email")
        progressDialog.show()

        firebaseAuth.currentUser!!.sendEmailVerification()
            .addOnSuccessListener {
                progressDialog.dismiss()
                Constantes.toastConMensaje(contexto, "Las instrucciones han sido enviadas a su correo")
            }
            .addOnFailureListener { e->
                progressDialog.dismiss()
                Constantes.toastConMensaje(contexto, "Ha habido un problema con la verificación")
                println(e.message)
            }
    }
}