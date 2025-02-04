package mekanism.common.tier;

import mekanism.api.heat.HeatAPI;
import mekanism.api.heat.Thermals;
import mekanism.api.tier.BaseTier;
import mekanism.api.tier.ITier;
import mekanism.common.config.value.CachedDoubleValue;
import mekanism.common.lib.Color;
import mekanism.common.util.EnumUtils;

public enum ConductorTier implements ITier {
    BASIC(BaseTier.BASIC, 5, HeatAPI.DEFAULT_HEAT_CAPACITY, 10, Color.rgbad(0.2, 0.2, 0.2, 1)),
    ADVANCED(BaseTier.ADVANCED, 5, HeatAPI.DEFAULT_HEAT_CAPACITY, 400, Color.rgbad(0.2, 0.2, 0.2, 1)),
    ELITE(BaseTier.ELITE, 5, HeatAPI.DEFAULT_HEAT_CAPACITY, 8_000, Color.rgbad(0.2, 0.2, 0.2, 1)),
    ULTIMATE(BaseTier.ULTIMATE, 5, HeatAPI.DEFAULT_HEAT_CAPACITY, 100_000, Color.rgbad(0.2, 0.2, 0.2, 1));

    private final Color baseColor;
    private final Thermals baseThermals;
    private final BaseTier baseTier;

    ConductorTier(BaseTier tier, double conduction, double heatCapacity, double insulation, Color color) {
        baseThermals = new Thermals(conduction, insulation, heatCapacity);
        baseColor = color;
        baseTier = tier;
    }

    public static ConductorTier get(BaseTier tier) {
        for (ConductorTier transmitter : EnumUtils.CONDUCTOR_TIERS) {
            if (transmitter.getBaseTier() == tier) {
                return transmitter;
            }
        }
        return BASIC;
    }

    @Override
    public BaseTier getBaseTier() {
        return baseTier;
    }

    public Thermals getThermalProfile() {
        return new Thermals(getInverseConduction(), getInverseInsulation(), getHeatCapacity());
    }

    private double getInverseConduction() {
        return getBaseThermals().inverseConduction();
    }

    private double getInverseInsulation() {
        return getBaseThermals().inverseInsulation();
    }

    private double getHeatCapacity() {
        return getBaseThermals().heatCapacity();
    }

    public Color getBaseColor() {
        return baseColor;
    }

    public Thermals getBaseThermals() { return baseThermals; }
}