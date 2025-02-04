package mekanism.common.capabilities.heat;

import java.util.function.DoubleSupplier;
import mekanism.api.IContentsListener;
import mekanism.api.SerializationConstants;
import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.heat.HeatAPI;
import mekanism.api.heat.IHeatCapacitor;
import mekanism.api.heat.IHeatManifold;
import mekanism.api.heat.Thermals;
import mekanism.common.Mekanism;
import mekanism.common.util.NBTUtils;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@NothingNullByDefault
public class BasicHeatCapacitor implements IHeatCapacitor {

    private @Nullable IHeatManifold heatManifold;

    private double heatCapacity;
    private Thermals thermals;

    private double storedHeat;
    private double heatToHandle;

    private final @Nullable IContentsListener listener;
    private final @Nullable DoubleSupplier ambientTempSupplier;

    public BasicHeatCapacitor(double heatCapacity) {
        this(heatCapacity, Thermals.AMBIENT);
    }

    public BasicHeatCapacitor(double heatCapacity, Thermals thermals) {
        this(heatCapacity, thermals, null, null);
    }

    public BasicHeatCapacitor(double heatCapacity, Thermals thermals, @Nullable IContentsListener listener, @Nullable DoubleSupplier ambientTempSupplier) {
        this.heatCapacity = HeatAPI.validateHeatCapacity(heatCapacity);
        this.thermals = HeatAPI.validateThermals(thermals);
        this.storedHeat = getAmbientTemperature();
        this.listener = listener;
        this.ambientTempSupplier = ambientTempSupplier;
    }

    protected double getAmbientTemperature() {
        return ambientTempSupplier == null ? HeatAPI.AMBIENT_TEMP : ambientTempSupplier.getAsDouble();
    }

    @Override
    public void onContentsChanged() {
        if (listener != null) {
            listener.onContentsChanged();
        }
    }

    @Override
    public void handleHeat(double transfer) {
        heatToHandle += transfer;
    }

    @Override
    public void updateHeat() {
        if (Math.abs(heatToHandle) > HeatAPI.EPSILON) {
            storedHeat += heatToHandle;
            //notify listeners
            onContentsChanged();
            // reset our handling heat
            heatToHandle = 0;
        }
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        NBTUtils.setDoubleIfPresent(nbt, SerializationConstants.STORED, this::setHeat);
        NBTUtils.setDoubleIfPresent(nbt, SerializationConstants.HEAT_CAPACITY, this::setHeatCapacity);
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag nbt = new CompoundTag();
        nbt.putDouble(SerializationConstants.STORED, getHeat());
        nbt.putDouble(SerializationConstants.HEAT_CAPACITY, getHeatCapacity());
        return nbt;
    }

    @Override
    public final double getHeat() {
        if (storedHeat <= 0) {
            Mekanism.logger.error("Negative stored heat: {}", storedHeat);
            return getAmbientTemperature() * getHeatCapacity();
        }
        return storedHeat;
    }

    @Override
    public final void setHeat(double heat) {
        if (getHeat() != heat) {
            storedHeat = heat;
            onContentsChanged();
        }
    }

    @Override
    public @Nullable IHeatManifold getManifold() {
        return heatManifold;
    }

    @Override
    public final double getHeatCapacity() {
        return heatCapacity;
    }

    @Override
    public void setManifold(@Nullable IHeatManifold heatManifold) {
        this.heatManifold = heatManifold;
    }

    @Override
    public void setHeatCapacity(double newCapacity, boolean updateHeat) {
        if (updateHeat && storedHeat != -1) {
            setHeat(storedHeat + (newCapacity - heatCapacity) * getAmbientTemperature());
        }
        setHeatCapacity(newCapacity);
    }

    private final void setHeatCapacity(double heatCapacity) {
        this.heatCapacity = HeatAPI.validateHeatCapacity(heatCapacity);
    }

    @Override
    public @NotNull Thermals getThermals(@Nullable Direction side) {
        return getThermals();
    }

    public final @NotNull Thermals getThermals() {
        return thermals;
    }

    @Override
    public void setThermals(Thermals thermals, @Nullable Direction side) {
        setThermals(thermals);
    }

    @Override
    public final void setThermals(Thermals thermals) {
        this.thermals = HeatAPI.validateThermals(thermals);
    }
}
