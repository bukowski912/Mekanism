package mekanism.api.heat;

import mekanism.api.heat.IHeatContact.IDyadicHeatContact;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

public record Thermals(double inverseConduction, double inverseInsulation) {

    public static Thermals AMBIENT = new Thermals();

    public Thermals() {
        this(HeatAPI.DEFAULT_INVERSE_CONDUCTION, HeatAPI.DEFAULT_INVERSE_INSULATION);
    }

    public double environmentConduction() {
        return HeatAPI.AIR_INVERSE_COEFFICIENT + inverseInsulation() + inverseConduction();
    }

    public static double environmentConduction(IHeatCapacitor capacitor, @Nullable Direction side) {
        return capacitor.getThermals(side).environmentConduction();
    }

    public static double adjacentConduction(Thermals first, Thermals second) {
        return first.inverseConduction() + second.inverseConduction();
    }

    public static double adjacentConduction(IDyadicHeatContact target) {
        return adjacentConduction(target.getFirstThermals(), target.getSecondThermals());
    }
}
