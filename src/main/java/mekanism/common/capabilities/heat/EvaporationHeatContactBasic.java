package mekanism.common.capabilities.heat;

import mekanism.api.heat.HeatAPI;
import mekanism.api.heat.IHeatCapacitor;
import mekanism.common.config.MekanismConfig;
import org.jetbrains.annotations.NotNull;

import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;

public class EvaporationHeatContactBasic extends BasicMonadicHeatContact {

    private final IntSupplier activeSolarsSupplier;
    private final DoubleSupplier ambientTempSupplier;

    public EvaporationHeatContactBasic(@NotNull IHeatCapacitor heatCapacitor, IntSupplier activeSolarsSupplier, DoubleSupplier ambientTempSupplier) {
        super(heatCapacitor);
        this.activeSolarsSupplier = activeSolarsSupplier;
        this.ambientTempSupplier = ambientTempSupplier;
    }

    private double getTemperature() {
        return getCapacitor().getTemperature();
    }

    protected double getAmbientTemperature() {
        return ambientTempSupplier == null ? HeatAPI.AMBIENT_TEMP : ambientTempSupplier.getAsDouble();
    }

    @Override
    public double simulate() {
        double currentTemp = getTemperature();
        int activeSolars = activeSolarsSupplier.getAsInt();
        double ambientTemp = getAmbientTemperature();

        double heatCapacity = capacitor.getHeatCapacity();
        capacitor.handleHeat(activeSolars * MekanismConfig.general.evaporationSolarMultiplier.get() * heatCapacity);
        if (Math.abs(currentTemp - ambientTemp) < 0.001) {
            capacitor.handleHeat(ambientTemp * heatCapacity - capacitor.getHeat());
        } else {
            double incr = MekanismConfig.general.evaporationHeatDissipation.get() * Math.sqrt(Math.abs(currentTemp - ambientTemp));
            if (currentTemp > ambientTemp) {
                incr = -incr;
            }
            capacitor.handleHeat(heatCapacity * incr);
            if (incr < 0) {
                return -incr;
            }
        }
        return 0;
    }
}
