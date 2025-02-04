package mekanism.api.heat;

import mekanism.api.IContentsListener;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A heat manifold stores all of the {@link IHeatContact}s for a single capacitor.
 * <p>
 * A given capacitor can only belong to (at most) a single {@link IHeatManifold} at a time.
 */
public interface IHeatManifold extends IContentsListener {

    @Nullable IHeatIsland getIsland();

    @NotNull IHeatCapacitor getCapacitor();

    void setCapacitor(@NotNull IHeatCapacitor heatCapacitor);

    @Nullable IHeatContact getContact(@Nullable Direction side);

    /**
     * Creates and adds a new contact to this {@link IHeatManifold}.
     *
     * @param side Optional side upon which to establish contact.
     * @return The heat contact that was created and added, or {@code null}.
     */
    boolean addContact(@NotNull IHeatContact contact);

    /**
     * Establishes a new contact between this {@link IHeatManifold} and another.
     * Automatically adds the new contact to the other manifold.
     *
     * @param side Optional side upon which to establish contact.
     * @param target Target capacitor which is opposite {@code side}.
     * @return The heat contact that was created and added, or {@code null}.
     */
    IHeatContact addAdjacent(IHeatManifold target, @Nullable Direction side);

    /*default double getTotalHeatCapacity() {
        double totalHeatCapacity = 0D;
        int heatCapacitors = getHeatCapacitors();
        for (int i = 0; i < heatCapacitors; i++) {
            totalHeatCapacity += getHeatCapacitor().getHeatCapacity();
        }
        return totalHeatCapacity;
    }

    default double getAverageTemperature() {
        double totalTemperature = 0D;
        int heatCapacitors = getHeatCapacitors();
        for (int i = 0; i < heatCapacitors; i++) {
            totalTemperature += getHeatCapacitor().getTemperature();
        }
        return totalTemperature / heatCapacitors;
    }*/
}
