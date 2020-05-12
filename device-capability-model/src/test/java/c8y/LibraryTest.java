package c8y;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.cumulocity.model.JSONBase;
import org.apache.commons.beanutils.ConversionException;
import org.junit.Before;
import org.junit.Test;
import org.svenson.JSONParseException;
import org.svenson.JSONParser;
import org.svenson.tokenize.InputStreamSource;

import com.cumulocity.model.ManagedObject;
import com.cumulocity.model.idtype.GId;

import static org.junit.Assert.*;

public class LibraryTest {

    private ManagedObject<GId> sensorLib;
    private ManagedObject<GId> deviceMgmtLib;
    private ManagedObject<GId> incorrectFragmentLib;
    private ManagedObject<GId> incorrectFragmentLib2;

    @Before
    public void setup() {
        sensorLib = readFile("/sensor-library");
        deviceMgmtLib = readFile("/device-management-library.json");
        incorrectFragmentLib = readFile("/incorrect-fragment-library.json");
        incorrectFragmentLib2 = readFile("/incorrect-fragment-library2.json");
    }

	@SuppressWarnings("unchecked")
	private ManagedObject<GId> readFile(String file) {
		InputStream is = getClass().getResourceAsStream(file);
		InputStreamSource source = new InputStreamSource(is, true);
		return ManagedObject.getJSONParser().parse(ManagedObject.class, source);
	}

	@Test
	public void trivialCases() {
		assertTrue( sensorLib.get(AccelerationSensor.class) instanceof AccelerationSensor);
		assertTrue( sensorLib.get(MotionSensor.class) instanceof MotionSensor);
		assertTrue( sensorLib.get(SinglePhaseElectricitySensor .class) instanceof SinglePhaseElectricitySensor);
		assertTrue( sensorLib.get(ThreePhaseElectricitySensor.class) instanceof ThreePhaseElectricitySensor);
		assertTrue( sensorLib.get(TemperatureSensor.class) instanceof TemperatureSensor);

		assertTrue( deviceMgmtLib.get(Availability.class) instanceof Availability);
		assertTrue( deviceMgmtLib.get(RequiredAvailability.class) instanceof RequiredAvailability);
	}

	@Test
	public void temperatureMeasurement() {
		TemperatureMeasurement m = sensorLib.get(TemperatureMeasurement.class);
		assertEquals(23, m.getTemperature().intValue());
	}

    @Test
    public void accelerationMeasurement() {
        AccelerationMeasurement m = sensorLib.get(AccelerationMeasurement.class);
        assertEquals(new BigDecimal("8.36"), m.getAccelerationValue());
    }

    @Test
    public void availability() {
        Availability a = deviceMgmtLib.get(Availability.class);
        assertEquals(ConnectionState.CONNECTED, a.getStatus());
    }

    @Test
    public void requiredAvailability() {
        RequiredAvailability ra = deviceMgmtLib.get(RequiredAvailability.class);
        assertEquals(60, ra.getResponseInterval());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldReadArrayOnIncorrectPropertyFields() {
        // should be able to read as
        Map<String, Object> customFragment = incorrectFragmentLib.getAttrs();
        List<Object> positions = (List<Object>) customFragment.get("c8y_Position");
        assertEquals("alt", positions.get(0));
        assertEquals(67L, positions.get(1));

        assertEquals(true, customFragment.get("c8y_Relay"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldReadObjectOnIncorrectPropertyFields() {
        Map<String, Object> customFragments = incorrectFragmentLib2.getAttrs();
        Map<String, Object> position = (Map<String, Object>) customFragments.get("c8y_Position");

        assertEquals(67L, position.get("lat"));
        assertEquals("123", position.get("lng"));
        assertEquals("xxxxstr", position.get("alt"));
    }

    @Test
    public void shouldNotReadAsClassOnIncorrectPropertyFields() {
        assertNull(incorrectFragmentLib.get(Position.class));
        assertNull(incorrectFragmentLib.get(Relay.class));
        assertNull(incorrectFragmentLib2.get(Position.class));
    }

    @Test
    public void shouldThrowExceptionOnExplicitParseOnIncorrectPropertyFields() {
        try {
            incorrectFragmentLib.get("c8y_Position", Position.class);
            fail("Should throw exception");
        } catch (JSONParseException e) {
        } catch (Exception e) {
            fail("incorrect exception type " + e.getMessage());
        }

        try {
            incorrectFragmentLib.get("c8y_Relay", Relay.class);
            fail("Should throw exception");
        } catch (JSONParseException e) {
        } catch (Exception e) {
            fail("incorrect exception type " + e.getMessage());
        }

        try {
            incorrectFragmentLib2.get("c8y_Position", Position.class);
            fail("Should throw exception");
        } catch (JSONParseException | ConversionException e) {
        } catch (Exception e) {
            fail("incorrect exception type " + e.getMessage());
        }
    }
}
