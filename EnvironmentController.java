import org.omg.CORBA.Environment;

/**
 * Created by tpesut on 6/10/14.
 */
public class EnvironmentController {
    private HVAC hvac;
    private boolean heat;
    private boolean cold;
    private boolean fan;
    private long ticks;
    private long lastHeatTick;
    private long lastColdTick;

    EnvironmentController(HVAC hvac) {
        this.hvac = hvac;

        ticks = 5;
        lastHeatTick = 0;
        lastColdTick = 0;

        hvac.Heat(false);
        hvac.Cool(false);
        hvac.Fan(false);
    }

    public void tick() {
        double temp = hvac.getTemp();
        switchHeat(temp);
        switchCool(temp);
        switchFan(temp);


        ticks++;
    }

    private void switchHeat(double temp) {
        if (lowTemp(temp)) {
            heat = true;
            lastHeatTick = ticks;
        }
        else heat = false;
        hvac.Heat(heat);
    }

    private void switchCool(double temp) {
        if(highTemp(temp) && safeToCool()) {
            cold = true;
            lastColdTick = ticks;
        }
        else cold = false;
        hvac.Cool(cold);
    }

    private void switchFan(double temp) {
        if (lowTemp(temp) || highTemp(temp) || keepFanOn())
            fan = true;
        else fan = false;
        hvac.Fan(fan);
    }

    public boolean getHeat() {
        return heat;
    }

    public boolean getCold() {
        return cold;
    }

    public boolean getFan() {
        return fan;
    }

    private boolean lowTemp(double temp) {
        return temp < 65;
    }

    private boolean highTemp(double temp) {
        return temp > 75;
    }

    private boolean safeToCool() {
        return ticks - lastColdTick > 3;
    }

    private boolean keepFanOn() {
        return ticks - lastHeatTick < 5;
    }
}
