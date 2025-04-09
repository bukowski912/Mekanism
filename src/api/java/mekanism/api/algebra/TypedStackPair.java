package mekanism.api.algebra;

import java.util.function.Supplier;

/**
 * A generic pair of stacks of the same type, that can interact with each other more directly than if they were maintained as seperate <code>TypedStack</code>s.
 */
public interface TypedStackPair<S> {

    enum TransferStatus {
        COMPLETE,
        PARTIAL
    }

    static <S> TransferStatus transfer(StackAlgebra<S> algebra, S source, S destination, long amount) {
        // Three steps:
        // 1. Measure how much is in reserve (in the source) based on the desired amount
        // 2. Extract that much from the source, tracking how much was actually extracted
        // 3. Insert into the destination the amount actually extracted
        if (amount < 0) {
            throw new IllegalArgumentException("Amount to transfer cannot be negative");
        }
        if (amount == 0) {
            return TransferStatus.COMPLETE;
        }
        final long reserved = Math.min(amount, algebra.volume(source));
        final long extracted = -algebra.change(source, -reserved);
        final long inserted = algebra.change(destination, extracted);
        return inserted == amount ? TransferStatus.COMPLETE : TransferStatus.PARTIAL;
    }

    record Container<S>(Supplier<S> firstSupplier, Supplier<S> secondSupplier, StackAlgebra<S> algebra) implements TypedStackPair<S> {

        @Override
        public S first() {
            return firstSupplier().get();
        }

        @Override
        public S second() {
            return secondSupplier().get();
        }
    }

    S first();

    S second();

    StackAlgebra<S> algebra();

    /*
     * Transfer an amount between the pair of stacks, from the first into the second.
     */
    default TransferStatus transferFirstToSecond(long amount) {
        return transfer(algebra(), first(), second(), amount);
    }

    /*
     * Transfer an amount between the pair of stacks, from the second into the first.
     */
    default TransferStatus transferSecondToFirst(long amount) {
        return transfer(algebra(), second(), first(), amount);
    }
}
