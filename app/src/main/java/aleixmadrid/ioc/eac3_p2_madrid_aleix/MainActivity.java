package aleixmadrid.ioc.eac3_p2_madrid_aleix;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private static final float ZOOM_10 = 10.0f;
    private static final float ZOOM_15 = 15.0f;

    private GoogleMap mMap;
    private ImageButton btnZoomIn, btnZoomOut;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Inicialitzar botons i funcionalitat onclick() amb zoom predeterminat a x10 o x15
        btnZoomIn = findViewById(R.id.btnZoomIn);
        btnZoomIn.setOnClickListener(v -> mMap.moveCamera(CameraUpdateFactory.zoomTo(ZOOM_10)));

        btnZoomOut = findViewById(R.id.btnZoomOut);
        btnZoomOut.setOnClickListener(v -> mMap.moveCamera(CameraUpdateFactory.zoomTo(ZOOM_15)));
    }

    //Crear Menu Options
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.map_options, menu);
        return true;
    }

    //Canviar tipus de mapa
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.normal_map) {
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            return true;
        } else if (itemId == R.id.hybrid_map) {
            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            return true;
        } else if (itemId == R.id.satellite_map) {
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            return true;
        } else if (itemId == R.id.terrain_map) {
            mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    //Inicialitzar mapa
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Coordenades inicials
        LatLng calders = new LatLng(41.7886, 1.9919);

        // Coordenades escoles bressol
        LatLng ebGarrofins = new LatLng(41.8110, 2.1024);
        LatLng ebPetitCamacurt = new LatLng(41.7607, 2.1513);
        LatLng ebLespurna = new LatLng(41.7491,2.1209);

        // Marcadors estàtics escoles bressol
        mMap.addMarker(new MarkerOptions().position(ebGarrofins).title("Escola Bressol Garrofins"));
        mMap.addMarker(new MarkerOptions().position(ebPetitCamacurt).title("Escola Bressol Petit Camacurt"));
        mMap.addMarker(new MarkerOptions().position(ebLespurna).title("Escola Bressol L'espurna"));

        // Tipus de mapa
        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);

        // Habilitar localització del dispositiu en ús
        enableMyLocation(mMap);

        // Establir listener de click llarg
        setMapLongClick(mMap);

        // Habilitar botons de zoom
        mMap.getUiSettings().setZoomControlsEnabled(true);

        // Centrar la vista del mapa a la localització que hem posat per defecte
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(calders, ZOOM_10));


    }

    // Afegir marcador de color blau amb esdeveniment LongClick
    private void setMapLongClick(final GoogleMap mMap) {

        mMap.setOnMapLongClickListener(latLng -> {
            String snippet = String.format(Locale.getDefault(),
                    getString(R.string.lat_long_snippet),
                    latLng.latitude,
                    latLng.longitude);

            mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title(getString(R.string.dropped_pin))
                    .snippet(snippet)
                    .icon(BitmapDescriptorFactory.defaultMarker
                            (BitmapDescriptorFactory.HUE_BLUE)));
        });
    }

    //Mostrar ubicació actual del dispositiu al mapa
    private void enableMyLocation(GoogleMap map) {

        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this, new String[]
                            {android.Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    enableMyLocation(mMap);
                else
                    Toast.makeText(this, getString(R.string.no_permis), Toast.LENGTH_LONG).show();
                break;
        }
    }

    //Mostrar les coordenades de l'ubicació del dispositiu
    public void showMyLocation(View view) {

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED  ){
            requestPermissions(new String[]{
                            android.Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
            return ;
        }

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Location myLocation=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (myLocation == null)
        {
            myLocation = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        }

        double lat = myLocation.getLatitude();
        double lon = myLocation.getLongitude();

        String ubicacio = String.format(Locale.getDefault(),
                getString(R.string.ubicacio),
                lat, lon);
        Toast toast = Toast.makeText(getApplicationContext(), ubicacio, Toast.LENGTH_SHORT);
        toast.show();
    }
}