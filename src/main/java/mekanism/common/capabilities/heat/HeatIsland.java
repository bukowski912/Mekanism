package mekanism.common.capabilities.heat;

import mekanism.api.heat.IHeatCapacitor;
import mekanism.api.heat.IHeatContact;
import mekanism.api.heat.IHeatIsland;
import mekanism.api.heat.IHeatManifold;

import java.util.HashSet;
import java.util.Set;

public class HeatIsland implements IHeatIsland {

    private final Set<IHeatCapacitor> heatCapacitors = new HashSet<>();
    private final Set<IHeatContact> heatContacts = new HashSet<>();
    private final Set<IHeatManifold> heatManifolds = new HashSet<>();

    private long lastSimulationTime = -1;
    private long lastUpdateTime = -1;

    @Override
    public boolean hasHeatCapacitor(IHeatCapacitor heatCapacitor) {
        return heatCapacitors.contains(heatCapacitor);
    }

    @Override
    public boolean hasHeatContact(IHeatContact heatContact) {
        return heatContacts.contains(heatContact);
    }

    @Override
    public boolean hasHeatManifold(IHeatManifold heatManifold) {
        return heatManifolds.contains(heatManifold);
    }

    @Override
    public void simulateAll(long currentTime) {
        if (currentTime <= lastSimulationTime) {
            return;
        }
        for (IHeatContact heatContact : heatContacts) {
            heatContact.simulate();
        }
        lastSimulationTime = currentTime;
    }

    @Override
    public void updateAll(long currentTime) {
        if (currentTime <= lastUpdateTime) {
            return;
        }
        for (IHeatCapacitor heatCapacitor : heatCapacitors) {
            heatCapacitor
        }
        lastUpdateTime = currentTime;
    }
}
