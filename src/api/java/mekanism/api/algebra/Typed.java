package mekanism.api.algebra;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import mekanism.api.algebra.Algebra.HasCodec;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/*
 * The base interface for Typed (Algebra + value) classes. This reduces repetition for implementation-specific stuff like codecs.
 */
public interface Typed<E, T extends Typed<?, T, A>, A extends Algebra<?, T, A>> {

    E value();

    A algebra();

    static <T extends Typed<?, T, A>, // Base typed
          A extends Algebra<?, T, A>, // Base algebra
          IA extends Algebra<I, T, A> & HasCodec<I>, // Input algebra
          I> // Input algebra's underlying type
    Codec<T> uniCodec(IA algebra) {
        return algebra.codec().flatComapMap(
              algebra.wrapper(),
              typed -> Typed.mapIf(typed, algebra, DataResult::success,
                    () -> Algebra.typeError(typed.algebra(), algebra)));
    }

    static <T extends Typed<?, T, A>, // Base typed
          A extends Algebra<?, T, A>, // Base algebra
          IA extends Algebra<I, T, A> & HasCodec<I>, // Input algebra
          I> // Input algebra's underlying type
    MapCodec<T> mapCodec(IA algebra) {
        return algebra.mapCodec().flatXmap(
              algebra.wrapper().andThen(DataResult::success),
              typed -> Typed.mapIf(typed, algebra, DataResult::success,
                    () -> Algebra.typeError(typed.algebra(), algebra)));
    }

    static <T extends Typed<?, T, A>, // Base typed
          A extends Algebra<?, T, A>, // Base algebra
          LA extends Algebra<L, T, A> & HasCodec<L>, // Left algebra
          RA extends Algebra<R, T, A> & HasCodec<R>, // Right algebra
          L, // Left algebra's underlying type
          R> // Right algebra's underlying type
    Codec<T> xorCodec(LA left, RA right) {
        return Codec.xor(left.codec(), right.codec()).flatComapMap(
              either -> either.map(left.wrapper(), right.wrapper()),
              stack -> Optional.ofNullable(Typed.unwrap(stack, left, right)).map(DataResult::success).orElseGet(
                    () -> Algebra.typeError(stack.algebra(), left, right)));
    }

    static <T extends Typed<?, T, A>, // Base typed
          A extends Algebra<?, T, A>, // Base algebra
          OA extends Algebra<O, T, A>, // Other algebra
          O> // Other algebra's underlying type
    Optional<O> unwrap(T typed, OA algebra) {
        return Typed.mapIf(typed, algebra, Optional::of, Optional::empty);
    }

    static <T extends Typed<?, T, A>, // Base typed
          A extends Algebra<?, T, A>, // Base algebra
          LA extends Algebra<L, T, A>, // Left algebra
          RA extends Algebra<R, T, A>, // Right algebra
          L, // Left algebra's underlying type
          R> // Right algebra's underlying type
    @Nullable Either<L, R> unwrap(T stack, LA left, RA right) {
        return mapIf(stack, left, Either::left, () -> mapIf(stack, right, Either::right, () -> null));
    }

    @SuppressWarnings("unchecked")
    static <T extends Typed<?, T, A>, // Base typed
          A extends Algebra<?, T, A>, // Base algebra
          OA extends Algebra<O, T, A>, // Target algebra
          O, // Target algebra's underlying type
          R> // Return type
    R mapIf(T stack, OA target, Function<O, R> function, Supplier<R> fallback) {
        return target.isAssignableFrom(stack.algebra()) ? function.apply((O) stack.value()) : fallback.get();
    }
}
