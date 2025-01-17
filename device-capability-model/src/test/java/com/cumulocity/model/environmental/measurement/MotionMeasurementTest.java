package com.cumulocity.model.environmental.measurement;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;
import org.svenson.JSON;
import org.svenson.JSONParser;

import c8y.SpeedMeasurement;

import com.cumulocity.model.measurement.MeasurementValue;
import com.cumulocity.model.measurement.ValueType;

public class MotionMeasurementTest {

    private SpeedMeasurement measurement;
    
    @Before
    public void setUp() throws Exception {
        
        MeasurementValue mValue= new MeasurementValue();
        mValue.setValue(new BigDecimal(1.0));
        mValue.setUnit("-");
        mValue.setType(ValueType.BOOLEAN);
        
        MeasurementValue sValue= new MeasurementValue();
        sValue.setValue(new BigDecimal(9.8));
        sValue.setUnit("m/s");

        measurement = new SpeedMeasurement();
        measurement.setMotion(mValue);
        measurement.setSpeed(sValue);
    }

    @Test
    public final void shouldSerializeAndDeserializeCorrectly() {

        String serialized = JSON.defaultJSON().forValue(measurement);
        SpeedMeasurement newMeasurement = JSONParser.defaultJSONParser().parse(SpeedMeasurement.class, serialized);
        assertTrue(serialized.contains("\"motionDetected\":"));    // According to wiki page
        assertTrue(serialized.contains("\"speed\":"));    // According to wiki page
        assertEquals(measurement.getSpeed().getValue(), newMeasurement.getSpeed().getValue());
        assertEquals(measurement.getMotion().getValue(), newMeasurement.getMotion().getValue());
    }
}
