package ro.pub.cs.systems.eim.practicaltest02v9

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Coordinates for Ghelmegioaia
        val ghelmegioaia = LatLng(44.1815, 23.5645)
        // Coordinates for Bucharest
        val bucharest = LatLng(44.4268, 26.1025)

        // Add a marker in Bucharest and move the camera
        mMap.addMarker(MarkerOptions().position(bucharest).title("Marker in Bucharest"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ghelmegioaia, 10f))
    }
}
