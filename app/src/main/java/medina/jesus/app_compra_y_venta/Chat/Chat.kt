package medina.jesus.app_compra_y_venta.Chat

import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.widget.PopupMenu
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import medina.jesus.app_compra_y_venta.Adaptadores.AdaptadorChat
import medina.jesus.app_compra_y_venta.Constantes
import medina.jesus.app_compra_y_venta.Modelo.Chat
import medina.jesus.app_compra_y_venta.R
import medina.jesus.app_compra_y_venta.databinding.ActivityChatBinding

class Chat : AppCompatActivity() {

    private lateinit var binding : ActivityChatBinding
    private lateinit var firebaseAuth : FirebaseAuth
    private lateinit var progressDialog: ProgressDialog

    private var uidVendedor = "" //Emisor
    private var uidUsuario = "" //Receptor
    private var rutaChat = ""
    private var imagenUri : Uri ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere por favor")
        progressDialog.setCanceledOnTouchOutside(false)



        uidVendedor = intent.getStringExtra("uidVendedor")!!
        uidUsuario = firebaseAuth.uid!!

        rutaChat = Constantes.rutaChat(uidVendedor, uidUsuario)

        binding.icRegresar.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        cargarInfoVendedor()
        cargarMensajes()

        binding.AdjuntarFAB.setOnClickListener {
            seleccionarImagenDialog()
        }

        binding.EnviarFAB.setOnClickListener {
            validarInfo()
        }
    }

    private fun cargarMensajes() {
        val mensajes = ArrayList<Chat>()
        val ref = Constantes.obtenerReferenciaChatsDB()
        ref.child(rutaChat)
            .addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    mensajes.clear()
                    for(ds : DataSnapshot in snapshot.children){
                        try {
                            val modeloChat = ds.getValue(Chat::class.java)
                            mensajes.add(modeloChat!!)
                        }catch (e : Exception){
                            println(e.message)
                            Constantes.toastConMensaje(this@Chat, "Ha habido un error al acceder a los mensajes")
                        }
                    }
                    val adaptadorChat = AdaptadorChat(this@Chat, mensajes)
                    binding.RvChats.adapter = adaptadorChat

                    binding.RvChats.setHasFixedSize(true)
                    var linearLayoutManager = LinearLayoutManager(this@Chat)
                    linearLayoutManager.stackFromEnd = true
                    binding.RvChats.layoutManager = linearLayoutManager
                }

                override fun onCancelled(error: DatabaseError) {
                    println(error.message)
                    Constantes.toastConMensaje(this@Chat, "Ha habido un problema al acceder a la base de datos")
                }
            })
    }

    private fun validarInfo() {
        val mensaje = binding.EtMensajeChat.text.toString().trim()
        val tiempo = Constantes.obtenerTiempoDis()

        if(mensaje.isEmpty()){
            Constantes.toastConMensaje(this, "No se puede enviar un mensaje vacío")
        }else{
            enviarMensaje(Constantes.MENSAJE_TIPO_TEXTO, mensaje, tiempo)
        }
    }

    private fun cargarInfoVendedor()
    {
        val ref = Constantes.obtenerReferenciaUsuariosDB()
        ref.child(uidVendedor)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        val nombre = "${snapshot.child("nombres").value}"
                        val imagen = "${snapshot.child("urlImagenPerfil").value}"

                        binding.TxtNombreVendedorChat.text = nombre
                        try {

                            Glide.with(this@Chat)
                                .load(imagen)
                                .placeholder(R.drawable.img_perfil)
                                .into(binding.ToolbarIv)

                        }catch (e : Exception){
                            println("Error al cargar la imagne con Glide " + e.message)
                        }
                    }catch (e : Exception){
                        println("Error al acceder al vendedor " + e.message)
                        Constantes.toastConMensaje(this@Chat, "Ha habido un problema al cargar la info " +
                                "del vendedor")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    println("Error en la base de datos " + error.message)
                    Constantes.toastConMensaje(this@Chat, "Ha habido un problema al acceder a la info " +
                            "del vendedor")
                }
            })
    }

    private fun seleccionarImagenDialog(){
        val popupMenu = PopupMenu(this, binding.AdjuntarFAB)

        popupMenu.menu.add(Menu.NONE, 1, 1, "Cámara")
        popupMenu.menu.add(Menu.NONE, 2, 2, "Galería")

        popupMenu.show()

        popupMenu.setOnMenuItemClickListener { menuItem->
            val itemId = menuItem.itemId
            if(itemId == 1){
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                    concederPermisoCamara.launch(arrayOf(android.Manifest.permission.CAMERA))
                }else{
                    concederPermisoCamara.launch(arrayOf(
                        android.Manifest.permission.CAMERA,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ))
                }
            }else if(itemId == 2){
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                    imagenGaleria()
                }else{
                    concederPermisoAlmacenamiento.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }
            true
        }
    }

    private fun imagenGaleria()
    {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        resultadoGaleriaARL.launch(intent)
    }

    private val resultadoGaleriaARL =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){resultado->
            if(resultado.resultCode == Activity.RESULT_OK){
                val data = resultado.data
                imagenUri = data!!.data
                subirImagenStorage()
            }else{
                Constantes.toastConMensaje(this, "Cancelado")
            }
        }

    private val concederPermisoAlmacenamiento =
        registerForActivityResult(ActivityResultContracts.RequestPermission()){concesion->
            if(concesion){
                imagenGaleria()
            }else{
                Constantes.toastConMensaje(this, "El permiso de almacenamiento ha sido denegado")
            }
        }

    private fun abrirCamara()
    {
        val contentValues = ContentValues()
        contentValues.put(MediaStore.Images.Media.TITLE, "Titulo_imagen")
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Descripcion_imagen")

        imagenUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imagenUri)
        resultadoCamaraARL.launch(intent)
    }

    private val resultadoCamaraARL =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){resultado->
            if(resultado.resultCode == Activity.RESULT_OK){
                subirImagenStorage()
            }else{
                Constantes.toastConMensaje(this, "Cancelado")
            }
        }

    private val concederPermisoCamara =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){resultado->
            var concedidoTodos  = true
            for(concesion in resultado.values){
                concedidoTodos = concedidoTodos && concesion
            }
            if(concedidoTodos){
                abrirCamara()
            }else{
                Constantes.toastConMensaje(this, "El permiso de la cámara o almacenamiento ha sido denegado")
            }
        }

    private fun subirImagenStorage()
    {
        progressDialog.setMessage("Subiendo imagen")
        progressDialog.show()

        val tiempo = Constantes.obtenerTiempoDis()
        val nombreRutaImagen = "ImagenesChat/${tiempo}"

        val storageref = FirebaseStorage.getInstance().getReference(nombreRutaImagen)
        storageref.putFile(imagenUri!!)
            .addOnSuccessListener {taskSnapShot->
                val uriTask = taskSnapShot.storage.downloadUrl
                while(!uriTask.isSuccessful);

                val urlImagen = uriTask.result.toString()
                if(uriTask.isSuccessful){
                    enviarMensaje(Constantes.MENSAJE_TIPO_IMAGEN, urlImagen, tiempo)
                }
            }
            .addOnFailureListener { e->
                println(e.message)
                Constantes.toastConMensaje(this, "No se pudo subir la imagen debido a un error")
            }
    }

    private fun enviarMensaje(tipoMensaje: String, mensaje: String, tiempo: Long) {
        progressDialog.setMessage("Enviando mensaje")
        progressDialog.show()

        val refChat = Constantes.obtenerReferenciaChatsDB()
        val keyId = "${refChat.push().key}"
        val hashMap = HashMap<String, Any>()

        hashMap["idMensaje"] = "$keyId"
        hashMap["tipoMensaje"] = "$tipoMensaje"
        hashMap["mensaje"] = "$mensaje"
        hashMap["emisorUid"] = "$uidUsuario"
        hashMap["receptorUid"] = "$uidVendedor"
        hashMap["tiempo"] = tiempo

        refChat.child(rutaChat)
            .child(keyId)
            .setValue(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                binding.EtMensajeChat.setText("")
            }
            .addOnFailureListener { e->
                progressDialog.dismiss()
                println(e.message)
                Constantes.toastConMensaje(this, "No se pudo enviar el mensaje")
            }
    }
}