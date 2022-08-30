package it.poliba.sisinflab.android.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import org.eclipse.rdf4j.model.vocabulary.DCTERMS;

import java.util.Date;

import it.poliba.sisinflab.coap.ldp.resources.LDPDataHandler;
import it.poliba.sisinflab.rdf.vocabulary.SSN_XG;

public class GenericSensorHandler extends LDPDataHandler implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mSensor;
    private double value = 0;

    private Context ctx;
    private int index;

    public GenericSensorHandler(Context c, int sensorType, int index){
        super();
        this.ctx = c;
        this.index = index;

        // Get an instance of the sensor service, and use that to get an instance of
        // a particular sensor.
        mSensorManager = (SensorManager) ctx.getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(sensorType);
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void handleData() {
        mng.updateRDFLiteralStatement(
                mng.getBaseURI() + resource,
                SSN_XG.hasValue.stringValue(),
                value);
        mng.updateRDFLiteralStatement(
                mng.getBaseURI() + resource,
                DCTERMS.CREATED.stringValue(),
                new Date());
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        value = sensorEvent.values[index];
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        // Do something here if sensor accuracy changes.
    }

    @Override
    public void stop(){
        super.stop();
        mSensorManager.unregisterListener(this);
    }

}
