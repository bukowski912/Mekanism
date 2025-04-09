package mekanism.api.algebra;

import mekanism.api.algebra.Algebra.HasCodec;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public interface FormAlgebra<F> extends Algebra<Holder<F>, TypedForm<?>, FormAlgebra<?>>, HasCodec<Holder<F>> {

    @FunctionalInterface
    interface StackFactory<S, F> {
        TypedStack<S> create(Holder<F> holder, long amount);
    }

    @Override
    default ResourceLocation id() {
        return stack().id();
    }

    Class<F> value();

    Holder<F> emptyHolder();

    boolean isEmpty(Holder<F> holder);

    ResourceKey<? extends Registry<F>> registryKey();

    default boolean matches(@Nullable ResourceKey<?> key) {
        return key != null && key.isFor(registryKey());
    }

    default boolean matches(Holder<?> holder) {
        return matches(holder.getKey()) || holder.isBound() && value().isInstance(holder.value());
    }

    @Override
    default boolean isAssignableFrom(FormAlgebra<?> algebra) {
        return registryKey() == algebra.registryKey();
    }

    StackAlgebra<?> stack();

    IngredientAlgebra<?> ingredient();

    TypedIngredient<?> createIngredient(ResourceKey<F> key, long amount);

    @Deprecated
    TypedIngredient<?> createIngredient(F form, long amount);

    StackFactory<?, F> stackFactory();
}
