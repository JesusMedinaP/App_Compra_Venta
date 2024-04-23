package medina.jesus.app_compra_y_venta.Anuncios

import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.widget.ArrayAdapter
import android.widget.PopupMenu
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import medina.jesus.app_compra_y_venta.Adaptadores.AdaptadorImagenSeleccionada
import medina.jesus.app_compra_y_venta.Constantes
import medina.jesus.app_compra_y_venta.Modelo.ImagenSeleccionada
import medina.jesus.app_compra_y_venta.R
import medina.jesus.app_compra_y_venta.databinding.ActivityCrearAnuncioBinding

class CrearAnuncio : AppCompatActivity() {

    private lateinit var binding : ActivityCrearAnuncioBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog

    private var imagenUri : Uri?= null

    private lateinit var imagenSelecArrayList : ArrayList<ImagenSeleccionada>
    private lateinit var adaptadorImagenSel : AdaptadorImagenSeleccionada
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCrearAnuncioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere por favor")
        progressDialog.setCanceledOnTouchOutside(false)


        val adaptadorCategorias = ArrayAdapter(this, R.layout.item_categoria, Constantes.categorias)
        binding.Categoria.setAdapter(adaptadorCategorias)

        val adaptadorCondiciones = ArrayAdapter(this, R.layout.item_condicion, Constantes.condiciones)
        binding.Condicion.setAdapter(adaptadorCondiciones)

        imagenSelecArrayList = ArrayList()
        cargarImagenes()
        binding.agregarImg.setOnClickListener {
            mostrarOpciones()
        }
    }

    //Campos a validar dentro de la creación del anuncio
    private var marca = ""
    private var categoria = ""
    private var condicion = ""
    private var direccion = ""
    private var precio = ""
    private var titulo = ""
    private var descripcion = ""
    private var latitud = 0.0
    private var longitud = ""

    private fun validarDatos()
    {
        marca = binding.EtMarca.text.toString().trim()
        categoria = binding.Categoria.text.toString().trim()
        condicion = binding.Condicion.text.toString().toString().trim()
        direccion = binding.Ubicacion.text.toString().trim()
        precio = binding.EtPrecio.text.toString().trim()
        titulo = binding.EtTitulo.text.toString().trim()
        descripcion = binding.EtDescripcion.text.toString().trim()
    }

    private fun mostrarOpciones() {
        val popupMenu = PopupMenu(this,binding.agregarImg)
        popupMenu.menu.add(Menu.NONE, 1, 1, "Cámara")
        popupMenu.menu.add(Menu.NONE, 2, 2, "Galería")

        popupMenu.show()

        popupMenu.setOnMenuItemClickListener { item->
            val itemId = item.itemId
            if(itemId == 1)
            {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                {
                    solicitarPermisoCamra.launch(arrayOf(android.Manifest.permission.CAMERA))
                }else{
                    solicitarPermisoCamra.launch(arrayOf(
                        android.Manifest.permission.CAMERA,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ))
                }
            }else if(itemId == 2)
            {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                {
                    imagenGaleria()
                }else{
                    solicitarPermisoAlmacenamiento.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }
            true
        }
    }

    private val solicitarPermisoAlmacenamiento = registerForActivityResult(
        ActivityResultContracts.RequestPermission()){concesion->
        if(concesion) {
            imagenGaleria()
        }else{
            Constantes.toastConMensaje(this, "El permiso de almacenamiento ha sido denegado")
    }}

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
                imagenUri = data!!.data

                val tiempo = "${Constantes.obtenerTiempoDis()}"
                val modeloImagenSeleccionada = ImagenSeleccionada(
                    tiempo, imagenUri, null, false
                )
                cargarImagenes()
            }else{
                Constantes.toastConMensaje(this,"Cancelado")
            }
        }


    private val solicitarPermisoCamra = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()){resultado->
        var todosConcedidos = true
        for(concesion in resultado.values)
        {
            todosConcedidos = todosConcedidos && concesion
        }
        if(todosConcedidos)
        {
            imagenCamara()
        }else{
            Constantes.toastConMensaje(this, "El permiso de la cámara o almacenamiento ha sido denegado")
        }
    }

    private fun imagenCamara() {
        val  contentValues = ContentValues()
        contentValues.put(MediaStore.Images.Media.TITLE, "Titulo_imagen")
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Descripcion_imagen")
        imagenUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imagenUri)
        resultadoCamara_ARL.launch(intent)
    }

    private val resultadoCamara_ARL =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        {
                resultado ->
            if(resultado.resultCode == Activity.RESULT_OK)
            {
                val tiempo = "${Constantes.obtenerTiempoDis()}"
                val modeloImagenSeleccionada = ImagenSeleccionada(
                    tiempo, imagenUri, null, false
                )
                cargarImagenes()
            }else{
                Constantes.toastConMensaje(this,"Cancelado")
            }
        }

    private fun cargarImagenes() {
        adaptadorImagenSel = AdaptadorImagenSeleccionada(this, imagenSelecArrayList)
        binding.RVImagenes.adapter = adaptadorImagenSel
    }
}