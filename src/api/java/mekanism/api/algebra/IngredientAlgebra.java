package mekanism.api.algebra;

import dev.emi.emi.api.stack.EmiIngredient;
import mekanism.api.algebra.Algebra.HasCodec;
import mekanism.api.algebra.Algebra.Intrinsic;
import mekanism.api.recipes.ingredients.InputIngredient;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;
import java.util.stream.Stream;

public interface IngredientAlgebra<I extends InputIngredient<?>> extends Intrinsic<I, TypedIngredient<?>, IngredientAlgebra<?>>, HasCodec<I> {

    @Override
    default ResourceLocation id() {
        return form().id();
    }

    FormAlgebra<?> form();

    I empty();

    EmiIngredient toEmi(I ingredient);

    @Override
    Function<I, TypedIngredient<I>> wrapper();

    default TypedIngredient<I> wrap(I value) {
        return wrapper().apply(value);
    }

    Stream<TypedStack<?>> representations(I value);
}
