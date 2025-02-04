package mekanism.api.heat;

import net.minecraft.core.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class IHeatContact {

    private Set<IHeatCapacitor> capacitorSet = null;
    private Map<IHeatCapacitor, Direction> directionMap = null;

    protected abstract Set<IHeatCapacitor> createCapacitorSet();
    protected abstract Map<IHeatCapacitor, Direction> createDirectionMap();

    @FunctionalInterface
    interface IMonadicHeatContactFactory<T extends MonadicHeatContact> {

        T create(@NotNull IHeatCapacitor capacitor, @Nullable Direction side);
    }

    @FunctionalInterface
    interface IDyadicHeatContactFactory<T extends IDyadicHeatContact> {

        T create(@NotNull IHeatCapacitor firstCapacitor, @NotNull IHeatCapacitor secondCapacitor, @Nullable Direction side);
    }

    abstract static class MonadicHeatContact extends IHeatContact {

        @Override
        public final Set<IHeatCapacitor> createCapacitorSet() {
            return Set.of(getCapacitor());
        }

        @Override
        public final Map<IHeatCapacitor, Direction> createDirectionMap() {
            Direction direction = getDirection();
            if (direction != null) {
                return Map.of(getCapacitor(), direction);
            }
            return Map.of();
        }

        public abstract @NotNull IHeatCapacitor getCapacitor();

        @Override
        public final Set<Thermals> getThermalsSet() {
            return Set.of(getThermals());
        }

        public @NotNull Thermals getThermals() {
            return getCapacitor().getThermals(getDirection());
        }

        public abstract Direction getDirection();
    }

    abstract static class IDyadicHeatContact extends IHeatContact {

        @Override
        public final Set<IHeatCapacitor> createCapacitorSet() {
            return Set.of(getFirstCapacitor(), getSecondCapacitor());
        }

        @Override
        public final Map<IHeatCapacitor, Direction> createDirectionMap() {
            Direction first = getFirstDirection();
            Direction second = getSecondDirection();
            if (first != null && second != null) {
                return Map.of(getFirstCapacitor(), first, getSecondCapacitor(), second);
            }
            if (first != null) {
                return Map.of(getFirstCapacitor(), first);
            }
            if (second != null) {
                return Map.of(getSecondCapacitor(), second);
            }
            return Map.of();
        }

        public abstract @NotNull IHeatCapacitor getFirstCapacitor();
        public abstract @NotNull IHeatCapacitor getSecondCapacitor();

        @Override
        public final Set<Thermals> getThermalsSet() {
            return Set.of(getFirstThermals(), getSecondThermals());
        }

        public @NotNull Thermals getFirstThermals() {
            return getFirstCapacitor().getThermals(getFirstDirection());
        }

        public @NotNull Thermals getSecondThermals() {
            return getSecondCapacitor().getThermals(getSecondDirection());
        }

        public abstract Direction getFirstDirection();

        public @Nullable Direction getSecondDirection() {
            final Direction first = getFirstDirection();
            return first != null ? first.getOpposite() : null;
        }
    }

    public final Set<IHeatCapacitor> getCapacitorSet() {
        if (capacitorSet == null) {
            capacitorSet = Objects.requireNonNull(createCapacitorSet());
        }
        return capacitorSet;
    }

    public boolean hasCapacitor(IHeatCapacitor capacitor) {
        return getCapacitorSet().contains(capacitor);
    }

    public final Map<IHeatCapacitor, Direction> getDirectionMap() {
        if (directionMap == null) {
            directionMap = Objects.requireNonNull(createDirectionMap());
        }
        return directionMap;
    }

    public Direction getDirection(IHeatCapacitor capacitor) {
        return getDirectionMap().get(capacitor);
    }

    public abstract Set<Thermals> getThermalsSet();

    public abstract double simulate();
}
