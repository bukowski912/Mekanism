package mekanism.common.attachments.containers.heat;

import java.util.List;
import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.heat.IHeatCapacitor;
import mekanism.api.heat.IHeatHandler;
import mekanism.common.attachments.containers.ComponentBackedHandler;
import mekanism.common.attachments.containers.ContainerType;
import mekanism.common.capabilities.heat.BasicHeatCapacitor;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@NothingNullByDefault
public class ComponentBackedHeatHandler extends ComponentBackedHandler<HeatCapacitorData, IHeatCapacitor, AttachedHeat> implements IHeatHandler {

    public ComponentBackedHeatHandler(ItemStack attachedTo) {
        super(attachedTo);
    }

    @Override
    protected ContainerType<IHeatCapacitor, AttachedHeat, ?> containerType() {
        return ContainerType.HEAT;
    }

    @Override
    public IHeatCapacitor getCapacitor(@Nullable Direction side) {
        return getContainer();
    }

    @Override
    protected HeatCapacitorData getContents(int index) {
        AttachedHeat attached = getAttached();
        if (index < 0 || index >= attached.size()) {
            if (index > 0 && index < size()) {
                //Get the default. This isn't the cleanest way to look it up, but as we never use this method for component backed heat handlers
                // it should be fine for now
                return containerType().createNewAttachment(attachedTo).get(index);
            }
            //Allow it to fall through and cause an index out of bounds exception
        }
        return attached.get(index);
    }
}