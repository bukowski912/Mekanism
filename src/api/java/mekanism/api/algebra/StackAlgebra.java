package mekanism.api.algebra;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.emi.emi.api.stack.EmiStack;
import mekanism.api.algebra.Algebra.HasCodec;
import mekanism.api.algebra.Algebra.Intrinsic;
import mezz.jei.api.gui.builder.IIngredientAcceptor;

import java.util.function.Function;

/**
 * A set of stack (as in item stack) operations defined over values of a stack-<em>like</em> type <code>S</code>.
 * Accordingly, the values compatible with an algebra are said to be <em>under</em> that algebra.
 */
public interface StackAlgebra<S> extends Intrinsic<S, TypedStack<?>, StackAlgebra<?>>, HasCodec<S> {

    @Override
    Function<S, ? extends TypedStack<S>> wrapper();

    default TypedStack<S> wrap(S value) {
        return wrapper().apply(value);
    }

    boolean notEmpty(S stack);

    boolean sameType(S first, S second);

    /**
     * Retrieves an object representing the empty stack. This is permitted to be a singleton and thus should be checked before mutating operations.
     */
    S empty();

    /**
     * Copies a stack object such that it is safe to mutate without affecting the original.
     */
    S copy(S source);

    S copyWithAmount(S source, long amount);

    /**
     * Gets the quantity of the thing in a stack object (whether the no. of items, or the mB of fluid/chemical, etc.).
     */
    long volume(S stack);

    /**
     * Merge two stacks into a third, only if each is (respectively) the same type.
     */
    default void mergeInto(S first, S second, S result) {
        if (notEmpty(first) && sameType(result, first)) {
            grow(result, volume(first));
        }
        if (notEmpty(second) && sameType(result, second)) {
            grow(result, volume(second));
        }
    }

    /**
     * Grow the given stack by an amount (or shrink it if that amount is negative).
     * This is permitted to change the stack volume by less than what was asked.
     */
    void grow(S stack, long amount);

    /**
     * Shrink the given stack by an amount (or grow it if that amount is negative).
     * This is permitted to change the stack volume by less than what was asked.
     */
    void shrink(S stack, long amount);

    /**
     * Change the volume of a stack by a given amount.
     * @return The true amount by which the stack changed.
     */
    default long change(S stack, long amount) {
        final long initial = volume(stack);
        if (amount > 0) {
            grow(stack, amount);
        } else if (amount < 0) {
            shrink(stack, amount);
        }
        return volume(stack) - initial;
    }

    default S add(S stack, long amount) {
        final S result = copy(stack);
        change(result, amount);
        return result;
    }

    EmiStack toEmi(S stack);

    Codec<S> codec();

    MapCodec<S> mapCodec();

    void addIngredient(S stack, IIngredientAcceptor<?> builder);
}