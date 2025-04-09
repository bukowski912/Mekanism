package mekanism.api.algebra;

import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;

import java.util.Optional;

public interface TypedForm<F> extends Typed<F, TypedForm<?>, FormAlgebra<?>> {

    static <F> Codec<TypedForm<?>> codec(FormAlgebra<F> algebra) {
        return Typed.uniCodec(algebra);
    }

    static <L, R> Codec<TypedForm<?>> xorCodec(FormAlgebra<L> left, FormAlgebra<R> right) {
        return Typed.xorCodec(left, right);
    }

    static <O> Optional<Holder<O>> unwrap(TypedForm<?> typed, FormAlgebra<O> algebra) {
        return Typed.unwrap(typed, algebra);
    }

    Holder<F> holder();
}
