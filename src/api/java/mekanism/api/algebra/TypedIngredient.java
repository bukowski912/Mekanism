package mekanism.api.algebra;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.emi.emi.api.stack.EmiIngredient;
import mekanism.api.recipes.ingredients.InputIngredient;

import java.util.Optional;
import java.util.stream.Stream;

public interface TypedIngredient<I extends InputIngredient<?>> extends Typed<I, TypedIngredient<?>, IngredientAlgebra<?>> {

    I value();

    IngredientAlgebra<I> algebra();

    static <I extends InputIngredient<?>> Codec<TypedIngredient<?>> codec(IngredientAlgebra<I> algebra) {
        return Typed.uniCodec(algebra);
    }

    static <I extends InputIngredient<?>> MapCodec<TypedIngredient<?>> mapCodec(IngredientAlgebra<I> algebra) {
        return Typed.mapCodec(algebra);
    }

    default <O extends InputIngredient<?>> Optional<O> unwrap(IngredientAlgebra<O> algebra) {
        return Typed.unwrap(this, algebra);
    }

    static <L extends InputIngredient<?>, R extends InputIngredient<?>>
    Either<L, R> unwrap(TypedIngredient<?> value, IngredientAlgebra<L> left, IngredientAlgebra<R> right) {
        return Typed.unwrap(value, left, right);
    }

    default EmiIngredient toEmi() {
        return algebra().toEmi(value());
    }

    default Stream<TypedStack<?>> representations() {
        return algebra().representations(value());
    }
}
