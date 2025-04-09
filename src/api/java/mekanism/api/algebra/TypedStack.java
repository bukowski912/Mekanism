package mekanism.api.algebra;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import dev.emi.emi.api.stack.EmiStack;

import java.util.Optional;
import java.util.function.Supplier;

public interface TypedStack<S> extends Typed<S, TypedStack<?>, StackAlgebra<?>> {

    static <S> Codec<TypedStack<?>> codec(StackAlgebra<S> algebra) {
        return Typed.uniCodec(algebra);
    }

    static <L, R> Codec<TypedStack<?>> codec(StackAlgebra<L> left, StackAlgebra<R> right) {
        return Typed.xorCodec(left, right);
    }

    static <L, R> Either<L, R> unwrap(TypedStack<?> value, StackAlgebra<L> left, StackAlgebra<R> right) {
        return Typed.unwrap(value, left, right);
    }

    record Container<S>(Supplier<S> valueSupplier, StackAlgebra<S> algebra) implements TypedStack<S> {

        @Override
        public S value() {
            return valueSupplier.get();
        }
    }

    S value();

    StackAlgebra<S> algebra();

    default TypedStack<S> wrap(S value) {
        return algebra().wrap(value);
    }

    default <O> Optional<O> unwrap(StackAlgebra<O> algebra) {
        return Typed.unwrap(this, algebra);
    }

    //Now we can get down to business

    default TypedStack<S> copy() {
        return wrap(algebra().copy(value()));
    }

    default TypedStack<S> copyWithAmount(long amount) {
        return wrap(algebra().copyWithAmount(value(), amount));
    }

    default long amount() {
        return algebra().volume(value());
    }

    default EmiStack toEmi() {
        return algebra().toEmi(value());
    }
}
