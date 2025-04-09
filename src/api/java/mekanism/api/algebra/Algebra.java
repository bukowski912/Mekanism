package mekanism.api.algebra;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

/**
 * An abstract set of operations defined over a specific type of value (denoted by <code>E</code>).
 * The base interface lays the groundwork for complete algebras by specifying basic metadata & functionality.
 */
public interface Algebra<E, T extends Typed<?, T, A>, A extends Algebra<?, T, A>> {

    ResourceLocation id();

    interface HasCodec<E> {

        Codec<E> codec();

        MapCodec<E> mapCodec();
    }

    /**
     * An extension of {@link Algebra} that relies on a {@link Class} object to distinguish values.
     */
    interface Intrinsic<E, T extends Typed<?, T, A>, A extends Intrinsic<?, T, A>> extends Algebra<E, T, A> {

        Class<E> value();

        /**
         * Whether a value under the specified algebra can be converted to a value under this algebra.
         */
        @Override
        default boolean isAssignableFrom(A algebra) {
            return value().isAssignableFrom(algebra.value());
        }
    }

    boolean isAssignableFrom(A algebra);

    /**
     * Gets a function that can construct a corresponding <code>Typed</code> from a value.
     */
    Function<E, ? extends T> wrapper();

    static <R> DataResult<R> typeError(Algebra<?, ?, ?> got, Algebra<?, ?, ?> expect) {
        return DataResult.error(() -> String.format("%s expected, got %s", expect.id(), got.id()));
    }

    static <R> DataResult<R> typeError(Algebra<?, ?, ?> got, Algebra<?, ?, ?> expect1, Algebra<?, ?, ?> expect2) {
        return DataResult.error(() -> String.format("%s or %s expected, got %s", expect1.id(), expect2.id(), got.id()));
    }

}
