package mekanism.common.capabilities.heat;

import mekanism.api.heat.*;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class BasicHeatManifold implements IHeatManifold {

    private @Nullable IHeatIsland island = null;
    private @Nullable IHeatCapacitor capacitor = null;

    private final EnumMap<Direction, IHeatContact> sided = new EnumMap<>(Direction.class);
    private @Nullable IHeatContact unsided = null;

    public BasicHeatManifold(int heatCapacity, Thermals thermals) {
        this(new BasicHeatCapacitor(heatCapacity, thermals));
    }

    public BasicHeatManifold(@Nullable IHeatCapacitor capacitor) {
        this.capacitor = capacitor;
    }

    @Override
    public @Nullable IHeatIsland getIsland() {
        return island;
    }

    @Override
    public @NotNull IHeatCapacitor getCapacitor() {
        return Objects.requireNonNull(capacitor);
    }

    @Override
    public void setCapacitor(@NotNull IHeatCapacitor newCapacitor) {
        if (capacitor != null) {
            capacitor.setManifold(null);
        }
        if (newCapacitor.getManifold() != this) {
            throw new IllegalArgumentException("New capacitor already belongs to manifold");
        }
        capacitor = newCapacitor;
        newCapacitor.setManifold(this);
    }

    @Override
    public @Nullable IHeatContact getContact(@Nullable Direction direction) {
        return direction != null ? sided.get(direction) : unsided;
    }

//    private static <T extends IMonadicHeatContact> T createMonadic(@NotNull IHeatCapacitor capacitor, @Nullable Direction side) {}

    @Override
    public boolean addContact(@NotNull IHeatContact contact) {
        Objects.requireNonNull(capacitor);
        if (!contact.hasCapacitor(capacitor)) {
            return false;
        }
        var direction = contact.getDirection(capacitor);
        if (direction == null) {
            if (unsided != null && unsided != contact) {
                return false;
            }
            unsided = contact;
            return true;
        }
        var side = sided.putIfAbsent(direction, contact);
        return side == null || side == contact;
    }

    @Override
    public IHeatContact addAdjacent(IHeatManifold target, @Nullable Direction side) {
        IHeatContact contact = new BasicHeatContact(getCapacitor(), target.getCapacitor(), side);
        return addContact(contact) && target.addContact(contact) ? contact : null;
    }

    @Override
    public void onContentsChanged() {
    }
}
