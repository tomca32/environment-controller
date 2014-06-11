/**
 * Created by tpesut on 6/10/14.
 */
public interface HVAC {
    public void Heat(boolean on);
    public double getTemp();

    public void Cool(boolean on);

    public void Fan(boolean on);
}
