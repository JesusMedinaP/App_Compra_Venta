package medina.jesus.app_compra_y_venta

import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import medina.jesus.app_compra_y_venta.databinding.ActivityEditarPerfilBinding

class EditarPerfil : AppCompatActivity() {

    private lateinit var binding: ActivityEditarPerfilBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog

    private var imageUri : Uri ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditarPerfilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere por favor")
        progressDialog.setCanceledOnTouchOutside(false)

        cargarInfo()

        binding.BtnActualizar.setOnClickListener {
            validarInfo()
        }

        binding.FABCambiarImg.setOnClickListener {
            select_imagen_de()
        }
    }

    private var nombres = ""
    private var fecha_nacimiento = ""
    private var codigo = ""
    private var telefono = ""
    private fun validarInfo() {
        nombres = binding.EtNombres.text.toString().trim()
        fecha_nacimiento = binding.EtFechaNacimiento.text.toString().trim()
        codigo = binding.selectorCod.selectedCountryCodeWithPlus
        telefono = binding.EtTelefono.text.toString().trim()

        if(nombres.isEmpty())
        {
            Constantes.toastConMensaje(this, "Ingrese su nombre completo")
        }else if(fecha_nacimiento.isEmpty())
        {
            Constantes.toastConMensaje(this, "Ingrese su fecha de nacimiento")
        }else if(telefono.isEmpty())
        {
            Constantes.toastConMensaje(this, "Ingrese un teléfono")
        }else{
            actualizarInfo()
        }
    }

    private fun actualizarInfo() {
        progressDialog.setMessage("Actualizando información")

        val hashMap: HashMap<String, Any> = HashMap()

        hashMap["nombres"] = "${nombres}"
        hashMap["fecha_nac"] = "${fecha_nacimiento}"
        hashMap["codigoTelefono"] = "${codigo}"
        hashMap["telefono"] = "${telefono}"

        val ref = Constantes.obtenerReferenciaUsuariosDB()
        ref.child(firebaseAuth.uid!!)
            .updateChildren(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Constantes.toastConMensaje(this, "Se actualizó la información correctamente")
            }
            .addOnFailureListener { e->
                progressDialog.dismiss()
                Constantes.toastConMensaje(this, "${e.message}")
            }
    }


    private fun cargarInfo() {
        val ref = Constantes.obtenerReferenciaUsuariosDB()
        ref.child("${firebaseAuth.uid}")
            .addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val nombres = "${snapshot.child("nombres").value}"
                    val imagen = "${snapshot.child("urlImagenPerfil").value}"
                    val f_nacimiento = "${snapshot.child("fecha_nac").value}"
                    val telefono = "${snapshot.child("telefono").value}"
                    val codTelefono = "${snapshot.child("codigoTelefono").value}"

                    //Volcamos la información en las vistas.

                    binding.EtNombres.setText(nombres)
                    binding.EtFechaNacimiento.setText(f_nacimiento)
                    binding.EtTelefono.setText(telefono)

                    try {

                        Glide.with(applicationContext)
                            .load(imagen)
                            .placeholder(R.drawable.img_perfil)
                            .into(binding.imgPerfil)
                    }catch (e: Exception){
                        Constantes.toastConMensaje(this@EditarPerfil, "${e.message}")
                    }

                    //Transformación del código del país sin el +
                    try {
                            val codigo = codTelefono.replace("+", "").toInt()
                            binding.selectorCod.setCountryForPhoneCode(codigo)
                    }catch (e: Exception){
                        //Comentado para poder eliminar el mensaje que sale al cargar la vista
//                        Toast.makeText(
//                            this@EditarPerfil,
//                            "${e.message}",
//                            Toast.LENGTH_SHORT
//                        ).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
    }

    private fun subirImagenAlStorage()
    {
        progressDialog.setMessage("Subiendo imagen a Storage")
        progressDialog.show()

        val rutaImagen = "imagenesPerfil/" + firebaseAuth.uid
        val ref = FirebaseStorage.getInstance().getReference(rutaImagen)
        ref.putFile(imageUri!!)
            .addOnSuccessListener { taskSnapShot ->
                val uriTask = taskSnapShot.storage.downloadUrl
                while (!uriTask.isSuccessful);
                val urlImagenCargada = uriTask.result.toString()
                if(uriTask.isSuccessful)
                {
                    actualizarImagenDB(urlImagenCargada)
                }
            }
            .addOnFailureListener{e->
                progressDialog.dismiss()
                Constantes.toastConMensaje(applicationContext, "${e.message}")
            }
    }

    private fun actualizarImagenDB(urlImagenCargada: String) {
        progressDialog.setMessage("Actualizando Imagen")
        progressDialog.show()

        val hashMap : HashMap<String, Any> = HashMap()
        if(imageUri != null)
        {
            hashMap["urlImagenPerfil"] = urlImagenCargada
        }

        val ref = Constantes.obtenerReferenciaUsuariosDB()
        ref.child(firebaseAuth.uid!!)
            .updateChildren(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Constantes.toastConMensaje(applicationContext,"Imagen de perfil actualizada con éxito")
            }

            .addOnFailureListener { e->
                progressDialog.dismiss()
                Constantes.toastConMensaje(applicationContext, "${e.message}")
            }
    }

    private fun select_imagen_de()
    {
        val popupMenu = PopupMenu(this, binding.FABCambiarImg)

        popupMenu.menu.add(Menu.NONE, 1, 1, "Cámara")
        popupMenu.menu.add(Menu.NONE, 2, 2, "Galería")

        popupMenu.show()

        popupMenu.setOnMenuItemClickListener { item ->
            val itemId = item.itemId
            if(itemId == 1)
            {
                // Cámara
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                {
                    concederPermisoCamara.launch(arrayOf(android.Manifest.permission.CAMERA))
                }else{
                    concederPermisoCamara.launch(arrayOf(
                        android.Manifest.permission.CAMERA,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE))
                }
            }else if(itemId == 2)
            {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                {
                    imagenGaleria()
                }else{
                    concederPermisoAlmacenamiento.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }
            return@setOnMenuItemClickListener true
        }

    }

    private val concederPermisoCamara =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
            resultado ->
            var concedidoTodos = true
            for(concesion in resultado.values)
            {
                concedidoTodos = concedidoTodos && concesion
            }

            if(concedidoTodos){
                imagenCamara()
            }else{
                Constantes.toastConMensaje(this,"El permiso de la cámara o almacenamiento ha sido denegado")
            }
        }

    private fun imagenCamara() {
        val  contentValues = ContentValues()
        contentValues.put(MediaStore.Images.Media.TITLE, "Titulo_imagen")
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Descripcion_imagen")
        imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        resultadoCamara_ARL.launch(intent)
    }

    private val resultadoCamara_ARL =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        {
            resultado ->
            if(resultado.resultCode == Activity.RESULT_OK)
            {
                subirImagenAlStorage()
                //Se ha comentado tras comprobar que funcionaba el subir la imagen desde la galería
                /*try{
                    Glide.with(this)
                        .load(imageUri)
                        .placeholder(R.drawable.img_perfil)
                        .into(binding.imgPerfil)
                }catch (e : Exception)
                {

                }*/
            }else{
                Constantes.toastConMensaje(this,"Cancelado")
            }
        }

    private val concederPermisoAlmacenamiento =
        registerForActivityResult(ActivityResultContracts.RequestPermission())
        {
            concesion ->
            if(concesion)
            {
                imagenGaleria()
            }else{
                Constantes.toastConMensaje(this,"El permiso de almacenamiento ha sido denegado")
            }
        }

    private fun imagenGaleria() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        resultadoGaleria_ARL.launch(intent)
    }

    private val resultadoGaleria_ARL =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        {
            resultado ->
            if(resultado.resultCode == Activity.RESULT_OK)
            {
                val data = resultado.data
                imageUri = data!!.data
                subirImagenAlStorage()

                //Se ha comentado tras comprobar que funcionaba el subir la imagen desde la galería
                /*try{
                    Glide.with(this)
                        .load(imageUri)
                        .placeholder(R.drawable.img_perfil)
                        .into(binding.imgPerfil)
                }catch (e : Exception)
                {

                }*/
            }else{
                Constantes.toastConMensaje(this,"Cancelado")
            }
        }
}