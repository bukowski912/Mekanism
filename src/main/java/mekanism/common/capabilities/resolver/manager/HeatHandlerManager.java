package mekanism.common.capabilities.resolver.manager;

import mekanism.api.heat.IHeatCapacitor;
import mekanism.api.heat.IHeatHandler;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.capabilities.holder.heat.IHeatCapacitorHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Helper class to make reading instead of having as messy generics
 */
public class HeatHandlerManager extends CapabilityHandlerManager<IHeatCapacitorHolder, IHeatCapacitor, IHeatHandler, IHeatHandler> {

    public HeatHandlerManager(@Nullable IHeatCapacitorHolder holder, @NotNull IHeatHandler baseHandler) {
        super(holder, baseHandler, Capabilities.HEAT, IHeatCapacitorHolder::getHeatCapacitors);
    }
}