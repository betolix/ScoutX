package io.h3llo.scoutx;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.data.geojson.GeoJsonFeature;
import com.google.maps.android.data.geojson.GeoJsonLayer;
import com.google.maps.android.data.geojson.GeoJsonPolygonStyle;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// DEUDA TECNICA - INFO WINDOW
// https://developers.google.com/maps/documentation/android-sdk/infowindows

public class ExposureMapFragment extends Fragment {

    GoogleMap mapa;
    private ClusterManager<MyItem> mClusterManager;
    double latt, lonn;
    int checkseguir=0;
    ArrayList<MyItem> myItems = new ArrayList<>();

//    public void Seguir(View view) {
//        Button btnseguir = findViewById(R.id.btnseguir);
//        if (checkseguir==0) {
//            checkseguir = 1;
//            btnseguir.setText("OFF seguir");
//            Toast.makeText(this, "Se Activo el seguimiento", Toast.LENGTH_SHORT).show();
//        }
//        else{
//            checkseguir = 0;
//            btnseguir.setText("ON seguir");
//            Toast.makeText(this, "Se desactivo el seguimiento", Toast.LENGTH_SHORT).show();
//        }
//    }

    public void miubicacion(View view) {
        if (mapa.getMyLocation() != null)
            mapa.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(mapa.getMyLocation().getLatitude(),
                            mapa.getMyLocation().getLongitude()), 17));
    }

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
//            LatLng sydney = new LatLng(-34, 151);
//            googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//            googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

            mapa = googleMap;

            /// GEOJSON START
            try{
                GeoJsonLayer layerPeru = new GeoJsonLayer(mapa, R.raw.peru, getContext());
                GeoJsonLayer layerLima = new GeoJsonLayer(mapa, R.raw.limalove, getContext());
                GeoJsonLayer layerMiraflores = new GeoJsonLayer(mapa, R.raw.miraflores, getContext());
                GeoJsonLayer layerExpo = new GeoJsonLayer(mapa, R.raw.exposicionesmiraf, getContext());


                GeoJsonPolygonStyle polyStylePeru = layerPeru.getDefaultPolygonStyle();
                GeoJsonPolygonStyle polyStyleLima = layerLima.getDefaultPolygonStyle();
                GeoJsonPolygonStyle polyStyleMiraflores = layerMiraflores.getDefaultPolygonStyle();

                polyStylePeru.setStrokeColor(Color.RED);
                polyStyleLima.setStrokeColor(Color.YELLOW);
                polyStyleMiraflores.setStrokeColor(Color.GREEN);


                polyStylePeru.setStrokeWidth(4);
                polyStyleLima.setStrokeWidth(8);
                polyStyleMiraflores.setStrokeWidth(10);

                layerPeru.addLayerToMap();
                layerLima.addLayerToMap();
                layerMiraflores.addLayerToMap();
                //layerExpo.addLayerToMap();
                //mapa.data.addGeoJson(); // https://developers.google.com/maps/documentation/javascript/datalayer

                for (GeoJsonFeature feature : layerExpo.getFeatures()) {
                    // do something to the feature
                    if(feature.getGeometry() != null) {
                        //LatLng position = feature.getGeometry().getGeometryObject();

                        Log.i("Prop ", String.valueOf(feature.getProperty("Cliente")));
                        Log.i("Prop ", String.valueOf(feature.getProperty("Codigo Producto")));
                        Log.i("Prop ", String.valueOf(feature.getProperty("Codigo Ramo")));
                        Log.i("Prop ", String.valueOf(feature.getProperty("Codigo Moneda")));
                        Log.i("Prop ", String.valueOf(feature.getProperty("Tipo de Georeferenciacion")));
                        Log.i("Prop ", String.valueOf(feature.getProperty("VD 11-Total USD")));
                        Log.i("Coords ", String.valueOf(feature.getProperty("Latitud")));
                        Log.i("Coords ", String.valueOf(feature.getProperty("Longitud")));
                        Log.i("Geom ", String.valueOf(feature.getGeometry().toString()));

                        latt=Double.parseDouble(feature.getProperty("Latitud"));
                        lonn=Double.parseDouble(feature.getProperty("Longitud"));
                        //OJO SE DEBERIA TOMAR DEL GEOMETRY NO DE LA PROPIEDAD LAT Y LON
                        // Set the title and snippet strings.
                        String title = feature.getProperty("Cliente");
                        String snippet =
                                " PRODUCTO " +  feature.getProperty("Codigo Producto") +" "+//System.lineSeparator()+
                                        " RAMO " + feature.getProperty("Codigo Ramo") + " " + //+System.lineSeparator()+
                                        " # POLIZA " + feature.getProperty("Numero Poliza")+ " " +System.lineSeparator()+
                                        "SUMA ASEGURADA "+ feature.getProperty("Codigo Moneda") + " "+ //+System.lineSeparator()+
                                        feature.getProperty("VD 11-Total USD") + " ";

                        MyItem item = new MyItem(latt,lonn, title, snippet);
                        myItems.add(item);
                        //  mClusterManager.addItem(item);

                    }
                    //items.add(new MyItem(Double.parseDouble(feature.getProperty("Latitud")),Double.parseDouble(feature.getProperty("Longitud")))); // convertir a double
                    //}
                    //else{break;}
                }

            } catch (IOException e){
                // String mLogTag;
                // Log.e(mLogTag, "GeoJSON file could not be read");
            } catch (JSONException e){
                // String mLogTag;
                // Log.e(mLogTag, "GeoJSON file could not be converted to JSONObject");
            }
            mapa.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            mapa.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-12.121510, -77.001477), 5)); // POSITION THE MAP

            // mapa.setOnMapClickListener(this);
            List<MyItem> items = null;

//            //  INICIO COARSE LOCATION
//            if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//                mapa.setMyLocationEnabled(true);
//                mapa.getUiSettings().setZoomControlsEnabled(false);
//                mapa.getUiSettings().setCompassEnabled(true);
//            }else {
//                Button btnMiPos=(Button) findViewById(R.id.btnmiubi);
//                btnMiPos.setEnabled(false);
//            }
//            // FIN DE COARSE LOCATION



            // INICIO MAP CLUSTERING
            mClusterManager = new ClusterManager<MyItem>(getContext(), mapa);

            // Point the map's listeners at the listeners implemented by the cluster
            // manager.
            mapa.setOnCameraIdleListener(mClusterManager);
            mapa.setOnMarkerClickListener(mClusterManager);

            mClusterManager.setOnClusterClickListener(
                    new ClusterManager.OnClusterClickListener<MyItem>() {
                        @Override public boolean onClusterClick(Cluster<MyItem> cluster) {
                            Toast.makeText(getActivity(), "Cluster pulsado", Toast.LENGTH_SHORT).show();
                            return false;
                        }
                    });
            mClusterManager.setOnClusterItemClickListener(
                    new ClusterManager.OnClusterItemClickListener<MyItem>() {
                        @Override public boolean onClusterItemClick(MyItem clusterItem) {
                            Toast.makeText(getActivity(), "Cluster item pulsado", Toast.LENGTH_SHORT).show();
                            return false;
                        }
                    });
            mClusterManager.getMarkerCollection()
                    .setInfoWindowAdapter(new CustomInfoViewAdapter(LayoutInflater.from(getActivity())));
            mapa.setInfoWindowAdapter(mClusterManager.getMarkerManager());

            mClusterManager.setOnClusterItemInfoWindowClickListener(
                    new ClusterManager.OnClusterItemInfoWindowClickListener<MyItem>() {
                        @Override public void onClusterItemInfoWindowClick(MyItem stringClusterItem) {
                            Toast.makeText(getActivity(), "Pulsaste el infowindow: " + stringClusterItem.getPosition().toString(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
            mapa.setOnInfoWindowClickListener(mClusterManager);

            mClusterManager.addItems(myItems);



            LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
                Toast.makeText(getContext(), "GPS available", Toast.LENGTH_LONG).show();
            else Toast.makeText(getContext(), "GPS not available", Toast.LENGTH_LONG).show();
            LocationListener locationListener = new LocationListener() {
                public void onLocationChanged(Location location) {
                    if (checkseguir == 1) {
                        Toast.makeText(getContext(), "Se cambio de posicion", Toast.LENGTH_SHORT).show();
                        Double latitude = location.getLatitude();
                        Double longitude = location.getLongitude();
                        Toast.makeText(getContext(), "latitud: " + latitude.toString() +
                                " longitud: " + longitude.toString(), Toast.LENGTH_SHORT).show();
                        mapa.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 15));
                    }
                }
                public void onStatusChanged(String provider, int status, Bundle extras) { }
                public void onProviderEnabled(String provider) { }
                public void onProviderDisabled(String provider) { }
            };


            mapa.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    // AQUIIIIII
                }
            });

            // Enable the location layer. Request the location permission if needed.
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                mapa.setMyLocationEnabled(true);
            }




        }




    };




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_exposure_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapa);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

//    // @Override
//    public void onMapClick(LatLng latLng) {
//        if (mClusterManager == null)
//            mClusterManager = new ClusterManager<MyItem>(this.getContext(), mapa);
//        mapa.setOnCameraIdleListener(mClusterManager);
//        mapa.setOnMarkerClickListener(mClusterManager);
//
//        Add cluster items (markers) to the cluster manager.
//        addItems();
//
//        Marker Mymarker = mapa.addMarker(new MarkerOptions()
//                .position(latLng)
//        );
//
//
//        markerList.add(Mymarker);
//    }



}