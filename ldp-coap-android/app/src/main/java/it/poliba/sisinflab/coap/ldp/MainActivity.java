package it.poliba.sisinflab.coap.ldp;

import android.app.Activity;
import android.hardware.Sensor;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.config.CoapConfig;
import org.eclipse.californium.core.server.resources.Resource;
import org.eclipse.californium.elements.config.Configuration;

import java.util.ArrayList;

import it.poliba.sisinflab.coap.ldp.resources.CoAPLDPBasicContainer;
import it.poliba.sisinflab.coap.ldp.resources.CoAPLDPDirectContainer;
import it.poliba.sisinflab.coap.ldp.resources.CoAPLDPRDFSource;
import it.poliba.sisinflab.coap.ldp.sensors.GenericSensorHandler;
import it.poliba.sisinflab.coap.ldp.server.CoAPLDPServer;
import it.poliba.sisinflab.coap.ldp.server.CoAPLDPTestSuiteServer;
import it.poliba.sisinflab.rdf.vocabulary.SSN_XG;

public class MainActivity extends Activity {

    ListView listView;
    ArrayList<CoapResource> mListItems;
    ResourceListViewAdapter adapter;

    CoAPLDPServer server = null;
    String BASE_URI = "coap://192.168.0.117:5688";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_linear);

        listView = (ListView) findViewById(R.id.resourceListView);

        findViewById(R.id.btn_start_test_server).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initLDPTestServer();
            }
        });

        findViewById(R.id.btn_start_sensor_server).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initLDPServer();
            }
        });

        findViewById(R.id.btn_stop_server).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopServer();
            }
        });

    }

    private void stopServer() {
        if (server != null) {
            server.shutdown();
            server.destroy();
            server = null;

            Toast toast = Toast.makeText(getApplicationContext(),
                    "LDP-CoAP Server Stopped!",
                    Toast.LENGTH_LONG);
            toast.show();
        } else {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Server not found!",
                    Toast.LENGTH_LONG);
            toast.show();
        }

        if (mListItems != null) {
            mListItems.clear();
            adapter.notifyDataSetInvalidated();
        }
    }

    private void initLDPTestServer() {
        Configuration config = Configuration.getStandard();
        config.set(CoapConfig.MAX_RESOURCE_BODY_SIZE, 20000);

        if (server == null) {
            server = new CoAPLDPTestSuiteServer(BASE_URI, config, 5688);
        }
        server.start();

        Toast toast = Toast.makeText(getApplicationContext(),
                "LDP-CoAP Test Server Started!",
                Toast.LENGTH_LONG);
        toast.show();

        initResourceList();
    }

    private void initLDPServer() {
        Configuration config = Configuration.getStandard();
        config.set(CoapConfig.MAX_RESOURCE_BODY_SIZE, 20000);

        if (server == null) {

            server = new CoAPLDPServer(BASE_URI, config, 5688);
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
        }
        server.start();

        Toast toast = Toast.makeText(getApplicationContext(),
                "LDP-CoAP Sensor Server Started!",
                Toast.LENGTH_LONG);
        toast.show();

        initResourceList();
    }

    private void initResourceList(){
        mListItems = new ArrayList<CoapResource>();
        initListItems(server.getRoot());

        adapter = new ResourceListViewAdapter(this,
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
}