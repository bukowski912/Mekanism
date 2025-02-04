package mekanism.api.heat;

public interface IHeatIsland {

    boolean hasHeatCapacitor(IHeatCapacitor heatCapacitor);

    boolean hasHeatContact(IHeatContact heatContact);

    boolean hasHeatManifold(IHeatManifold heatManifold);

    void simulateAll(long currentTime);

    void updateAll(long currentTime);
}
