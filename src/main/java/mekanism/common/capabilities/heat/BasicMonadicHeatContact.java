package mekanism.common.capabilities.heat;

import mekanism.api.heat.IHeatCapacitor;
import mekanism.api.heat.IHeatContact.MonadicHeatContact;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BasicMonadicHeatContact extends MonadicHeatContact {

    protected @NotNull IHeatCapacitor capacitor;
    protected @Nullable Direction direction;

    public BasicMonadicHeatContact(@NotNull IHeatCapacitor capacitor) {
        this(capacitor, null);
    }

    public BasicMonadicHeatContact(@NotNull IHeatCapacitor capacitor, @Nullable Direction direction) {
        this.capacitor = capacitor;
        this.direction = direction;
    }

    @Override
    public final @NotNull IHeatCapacitor getCapacitor() {
        return capacitor;
    }

    @Override
    public final @Nullable Direction getDirection() {
        return direction;
    }
}
