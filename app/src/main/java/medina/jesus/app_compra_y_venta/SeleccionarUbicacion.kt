package medina.jesus.app_compra_y_venta

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import medina.jesus.app_compra_y_venta.databinding.ActivitySeleccionarUbicacionBinding

class SeleccionarUbicacion : AppCompatActivity() , OnMapReadyCallback {

    private lateinit var binding: ActivitySeleccionarUbicacionBinding
    private companion object{
        private const val DEFAULT_ZOOM = 15
    }

    private var map: GoogleMap?=null
    private var placeClient : PlacesClient?=null
    private var fusedLocationProviderClient : FusedLocationProviderClient?=null

    private var ultimaUbicacion : Location?=null
    private var latitudSeleccionada : Double?=null
    private var longitudSeleccionada : Double?=null
    private var direccion = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySeleccionarUbicacionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.listoLL.visibility = View.GONE

        val mapFragment = supportFragmentManager.findFragmentById(R.id.MapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        Places.initialize(this, getString(R.string.google_maps_api_key))
        placeClient = Places.createClient(this)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        val autoCompleteSupportMapFragment  = supportFragmentManager.findFragmentById(R.id.autocompletar_fragment)
        as AutocompleteSupportFragment

        val placeList = arrayOf(
            Place.Field.ID,
            Place.Field.NAME,
            Place.Field.ADDRESS,
            Place.Field.LAT_LNG
        )

        autoCompleteSupportMapFragment.setPlaceFields(listOf(*placeList))

        autoCompleteSupportMapFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener{
            override fun onPlaceSelected(place: Place) {
                val id = place.id
                val nombre = place.name
                val latLng = place.latLng

                latitudSeleccionada = latLng?.latitude
                longitudSeleccionada = latLng?.longitude
                direccion = place.address?: ""

                agregarMarcador(latLng, nombre, direccion)
            }
            override fun onError(p0: Status) {
                Constantes.toastConMensaje(applicationContext, "Búsqueda cancelada")
            }
        })

        binding.IbGPS.setOnClickListener {
            if(GpsActivado())
            {
                solicitarPermisoUbicacion.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }else{
                Constantes.toastConMensaje(this, "¡La ubicación no está activada!")
            }
        }

        binding.BtnListo.setOnClickListener {
            val intent = Intent()
            intent.putExtra("latitud", latitudSeleccionada)
            intent.putExtra("longitud", longitudSeleccionada)
            intent.putExtra("direccion", direccion)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }

        binding.IbRegresar.setOnClickListener {
            finish()
        }
    }

    private fun elegirLugarActual(){
        if(map == null){
            return
        }
        detectAndShowDeviceLocationMap()
    }
    @SuppressLint("MissingPermission")
    private fun detectAndShowDeviceLocationMap()
    {
        try {
            val locationResult = fusedLocationProviderClient!!.lastLocation
            locationResult.addOnSuccessListener {ubicacion ->
                if(ubicacion!=null){
                    ultimaUbicacion = ubicacion
                    latitudSeleccionada = ubicacion.latitude
                    longitudSeleccionada = ubicacion.longitude

                    val latLng = LatLng(latitudSeleccionada!!, longitudSeleccionada!!)
                    map!!.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM.toFloat()))
                    map!!.animateCamera(CameraUpdateFactory.zoomTo(DEFAULT_ZOOM.toFloat()))

                    direccionLatLng(latLng)
                }

            }.addOnFailureListener {
                e->Constantes.toastConMensaje(this, "${e.message}")
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    private fun GpsActivado(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var gpsEnable = false
        var networkEnable = false
        try{
            gpsEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        }catch (e:Exception){
            e.printStackTrace()
        }
        try{
            networkEnable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        }catch (e:Exception){
            e.printStackTrace()
        }
        return !(!gpsEnable && !networkEnable)
    }

    @SuppressLint("MissingPermission")
    private val solicitarPermisoUbicacion : ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission())
        {concesion ->
            if(concesion)
            {
                map!!.isMyLocationEnabled = true
                elegirLugarActual()
            }else{
                Constantes.toastConMensaje(this, "Permiso denegado")
            }
        }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        solicitarPermisoUbicacion.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        map!!.setOnMapClickListener {latLng->
            latitudSeleccionada = latLng.latitude
            longitudSeleccionada = latLng.longitude

            direccionLatLng(latLng)
        }
    }

    private fun direccionLatLng(latLng: LatLng) {
        val geoCoder = Geocoder(this)
        try{
            val listaDirecciones = geoCoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            val address = listaDirecciones!![0]
            val addressLine = address.getAddressLine(0)
            val subLocalidad = address.subLocality
            direccion = "$addressLine"
            agregarMarcador(latLng, "$subLocalidad", "$addressLine")
        }catch (e:Exception)
        {

        }
    }

    private fun agregarMarcador(latLng: LatLng, titulo : String, direccion : String)
    {
        map!!.clear()
        try {
            val markerOptions = MarkerOptions()
            markerOptions.position(latLng)
            markerOptions.title("$titulo")
            markerOptions.snippet("$direccion")
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))

            map!!.addMarker(markerOptions)
            map!!.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM.toFloat()))

            binding.listoLL.visibility = View.VISIBLE
            binding.TvLugarSeleccionado.text = direccion
        }catch(e:Exception)
        {

        }
    }
}