package it.poliba.sisinflab.android;

import android.app.AlertDialog;
import android.hardware.Sensor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.network.config.NetworkConfig;
import org.eclipse.californium.core.server.resources.Resource;

import java.io.File;
import java.util.ArrayList;

import it.poliba.sisinflab.android.sensors.GenericSensorHandler;
import it.poliba.sisinflab.coap.ldp.R;
import it.poliba.sisinflab.coap.ldp.resources.CoAPLDPBasicContainer;
import it.poliba.sisinflab.coap.ldp.resources.CoAPLDPDirectContainer;
import it.poliba.sisinflab.coap.ldp.resources.CoAPLDPRDFSource;
import it.poliba.sisinflab.coap.ldp.server.CoAPLDPServer;
import it.poliba.sisinflab.rdf.vocabulary.SSN_XG;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ListView listView;
    ArrayList<CoapResource> mListItems;

    CoAPLDPServer server;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listView.invalidateViews();
                initResourceList();
                Snackbar.make(view, "Resource List refreshed!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onPostCreate (Bundle savedInstanceState){
        super.onPostCreate(savedInstanceState);

        initLDPServer();

        listView = (ListView) findViewById(R.id.listRes);
        initResourceList();

        View parentLayout = findViewById(R.id.fab);
        Snackbar.make(parentLayout, "LDP-CoAP Started!", Snackbar.LENGTH_LONG).show();
    }

    private void initResourceList(){
        mListItems = new ArrayList<CoapResource>();
        initListItems(server.getRoot());

        ResourceListViewAdapter adapter = new ResourceListViewAdapter(this,
                R.layout.list_item, mListItems);
        listView.setAdapter(adapter);
    }

    private void initListItems(Resource root){
        for(Resource r : root.getChildren()){
            if(r instanceof CoapResource) {
                mListItems.add((CoapResource) r);
                initListItems(r);
            }
        }
    }

    private void initLDPServer() {

        //used to avoid Read Only File System IOException
        String filePath = this.getBaseContext().getFilesDir().getPath().toString() + "/Californium.properties";
        File f = new File(filePath);
        NetworkConfig config = NetworkConfig.createStandardWithFile(f);

        String BASE_URI = "coap://192.168.2.210:5683";
        server = new CoAPLDPServer(BASE_URI, config, 5683);
        server.addHandledNamespace(SSN_XG.PREFIX, SSN_XG.NAMESPACE + "#");

        /*** Environmental Sensors Container ***/
        CoAPLDPBasicContainer env = server.createBasicContainer("environment");

        CoAPLDPRDFSource light = env.createRDFSource("light");
        light.setDataHandler(new GenericSensorHandler(this.getBaseContext(), Sensor.TYPE_LIGHT, 0));

        CoAPLDPRDFSource pressure = env.createRDFSource("pressure", SSN_XG.SensingDevice.toString());
        pressure.setDataHandler(new GenericSensorHandler(this.getBaseContext(), Sensor.TYPE_PRESSURE, 0));

        /*** Motion Sensors Container ***/
        CoAPLDPBasicContainer motion = server.createBasicContainer("motion");
        motion.setRDFDescription("Monitors the motion of the device");

        CoAPLDPDirectContainer acc = motion.createDirectContainer("accelerometer", "axis", SSN_XG.Sensor.toString(), SSN_XG.hasSubSystem.toString(), null);
        acc.setRDFDescription("Measures the acceleration force along the x-y-z axis (including gravity)");

        CoAPLDPRDFSource acc_x_axis = acc.createRDFSource("x-axis");
        acc_x_axis.setDataHandler(new GenericSensorHandler(this.getBaseContext(), Sensor.TYPE_ACCELEROMETER, 0));
        CoAPLDPRDFSource acc_y_axis = acc.createRDFSource("y-axis");
        acc_y_axis.setDataHandler(new GenericSensorHandler(this.getBaseContext(), Sensor.TYPE_ACCELEROMETER, 1));
        CoAPLDPRDFSource acc_z_axis = acc.createRDFSource("z-axis");
        acc_z_axis.setDataHandler(new GenericSensorHandler(this.getBaseContext(), Sensor.TYPE_ACCELEROMETER, 2));

        CoAPLDPDirectContainer gyro = motion.createDirectContainer("gyroscope", "axis", SSN_XG.Sensor.toString(), SSN_XG.hasSubSystem.toString(), null);
        CoAPLDPRDFSource gyro_x_axis = gyro.createRDFSource("x-axis");
        gyro_x_axis.setDataHandler(new GenericSensorHandler(this.getBaseContext(), Sensor.TYPE_GYROSCOPE, 0));
        CoAPLDPRDFSource gyro_y_axis = gyro.createRDFSource("y-axis");
        gyro_y_axis.setDataHandler(new GenericSensorHandler(this.getBaseContext(), Sensor.TYPE_GYROSCOPE, 1));
        CoAPLDPRDFSource gyro_z_axis = gyro.createRDFSource("z-axis");
        gyro_z_axis.setDataHandler(new GenericSensorHandler(this.getBaseContext(), Sensor.TYPE_GYROSCOPE, 2));

        CoAPLDPRDFSource step = motion.createRDFSource("step-counter");
        step.setDataHandler(new GenericSensorHandler(this.getBaseContext(), Sensor.TYPE_STEP_COUNTER, 0));

        /*** Position Sensors Container ***/
        CoAPLDPBasicContainer pos = server.createBasicContainer("position");
        pos.setRDFDescription("Determines the position of the device");

        CoAPLDPRDFSource prox = pos.createRDFSource("proximity");
        prox.setDataHandler(new GenericSensorHandler(this.getBaseContext(), Sensor.TYPE_PROXIMITY, 0));

        CoAPLDPDirectContainer orient = pos.createDirectContainer("orientation", "dimensions", SSN_XG.Sensor.toString(), SSN_XG.hasSubSystem.toString(), null);
        CoAPLDPRDFSource azimuth = orient.createRDFSource("azimuth");
        azimuth.setDataHandler(new GenericSensorHandler(this.getBaseContext(), Sensor.TYPE_ORIENTATION, 0));
        CoAPLDPRDFSource pitch = orient.createRDFSource("pitch");
        pitch.setDataHandler(new GenericSensorHandler(this.getBaseContext(), Sensor.TYPE_ORIENTATION, 1));
        CoAPLDPRDFSource roll = orient.createRDFSource("roll");
        roll.setDataHandler(new GenericSensorHandler(this.getBaseContext(), Sensor.TYPE_ORIENTATION, 2));

        server.start();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

        server.shutdown();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_info) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Test App for ldp-coap-core library on Android")
                    .setIcon(android.R.drawable.sym_def_app_icon)
                    .setTitle("About");
            AlertDialog dialog = builder.create();
            dialog.show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
