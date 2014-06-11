import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class EnvironmentControllerTest {
    private EnvironmentController controller;

    @Mock
    private HVAC hvacMock;

    @Before
    public void setup() {
        initMocks(this);
        controller = new EnvironmentController(hvacMock);

        assertHVACState(false, false, false);

    }

    @Test
    public void initializedControllerShouldSetEverythingToOff() {
        verify(hvacMock).Heat(false);
        verify(hvacMock).Cool(false);
        verify(hvacMock).Fan(false);
    }

    @Test
    public void controllerShouldCallGetTempOnTick() {
        controller.tick();
        verify(hvacMock).getTemp();
    }

    @Test
    public void controllerShouldTurnOnHeatingAndFanWhenItsCold() {
        assertEnvironmentProducesState(30.0, true, false, true);
    }

    @Test
    public void controllerTurnsOnCoolingWhenItsHot() {
        assertEnvironmentProducesState(90.0, false, true, true);
    }

    @Test
    public void controllerTurnsEverythingOFfWhenTheTempIsIdeal() {
        assertEnvironmentProducesState(70.0, false, false, false);
    }

    @Test
    public void controllerTurnsOffFanAfterHeatingHasBeenOffForFiveMinutes() {
        when(hvacMock.getTemp()).thenReturn(30.0);

        controller.tick();
        assertHVACState(true, false, true);

        when(hvacMock.getTemp()).thenReturn(70.0);

        controller.tick();
        assertHVACState(false, false, true);

        controller.tick();
        assertHVACState(false, false, true);

        controller.tick();
        assertHVACState(false, false, true);

        controller.tick();
        assertHVACState(false, false, true);

        controller.tick();
        assertHVACState(false, false, false);
    }

    @Test
    public void controllerDoesNotTurnOnCoolerTwiceInThreeMinutes() {
        assertEnvironmentProducesState(80.0, false, true, true);

        assertEnvironmentProducesState(70.0, false, false, false);

        assertEnvironmentProducesState(80.0, false, false, true);
        assertEnvironmentProducesState(80.0, false, false, true);
        assertEnvironmentProducesState(80.0, false, true, true);
    }

    private void assertEnvironmentProducesState(double temperature, boolean heat, boolean cold, boolean fan) {
        when(hvacMock.getTemp()).thenReturn(temperature);
        controller.tick();
        assertHVACState(heat, cold, fan);
    }

    private void assertHVACState(boolean heat, boolean cold, boolean fan) {
        assertSame(heat, controller.getHeat());
        assertSame(cold, controller.getCold());
        assertSame(fan, controller.getFan());
    }
}
