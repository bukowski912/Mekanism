package mekanism.api.heat;

public interface IHeatHandler {

    interface ISingleHeatManifold extends IHeatHandler {

        @Override
        default int getHeatManifolds() {
            return hasHeatManifold() ? 1 : 0;
        }

        boolean hasHeatManifold();

        @Override
        default IHeatManifold getHeatManifold(int index) {
            if (index != 0) {
                throw new IndexOutOfBoundsException();
            }
            return getHeatManifold();
        }

        IHeatManifold getHeatManifold();
    }

    default boolean canHandleHeat() {
        return getHeatManifolds() > 0;
    }

    int getHeatManifolds();

    IHeatManifold getHeatManifold(int index);

    default int addHeatManifold() {
        throw new UnsupportedOperationException();
    }

    default void removeHeatManifold(IHeatManifold heatManifold) {
        throw new UnsupportedOperationException();
    }

    default IHeatCapacitor getHeatCapacitor(int manifoldIndex, int capacitorIndex) {
        return getHeatManifold(manifoldIndex).getCapacitor();
    }
}
