package mekanism.common.algebra;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.emi.emi.api.neoforge.NeoForgeEmiStack;
import dev.emi.emi.api.stack.EmiStack;
import mekanism.api.MekanismAPI;
import mekanism.api.SerializationConstants;
import mekanism.api.algebra.StackAlgebra;
import mekanism.api.algebra.TypedStack;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.client.recipe_viewer.emi.ChemicalEmiStack;
import mekanism.client.recipe_viewer.jei.MekanismJEI;
import mekanism.client.recipe_viewer.jei.MekanismJEIHelper;
import mezz.jei.api.gui.builder.IIngredientAcceptor;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.runtime.IIngredientManager;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.Objects;
import java.util.function.Function;

public final class Stacks {

    private Stacks() {
    }

    public static TypedStack<FluidStack> fluid(Holder<Fluid> holder, long amount) {
        return wrap(new FluidStack(holder, (int) amount));
    }

    public static TypedStack<ChemicalStack> chemical(Holder<Chemical> holder, long amount) {
        return wrap(new ChemicalStack(holder, amount));
    }

    public static TypedStack<FluidStack> wrap(FluidStack value) {
        return new FluidTypedStack(value);
    }

    public static TypedStack<ChemicalStack> wrap(ChemicalStack value) {
        return new ChemicalTypedStack(value);
    }

    public static Either<FluidStack, ChemicalStack> unwrap(TypedStack<?> stack) {
        return Objects.requireNonNull(TypedStack.unwrap(stack, FLUID, CHEMICAL));
    }

    public static <R> R unwrap(TypedStack<?> stack, Function<FluidStack, R> fluid, Function<ChemicalStack, R> chemical) {
        return unwrap(stack).map(fluid, chemical);
    }

    public static final StackAlgebra<FluidStack> FLUID = new StackAlgebra<>() {

        @Override
        public ResourceLocation id() {
            return BuiltInRegistries.FLUID.key().location();
        }

        @Override
        public Class<FluidStack> value() {
            return FluidStack.class;
        }

        @Override
        public boolean notEmpty(FluidStack stack) {
            return !stack.isEmpty();
        }

        @Override
        public boolean sameType(FluidStack first, FluidStack second) {
            return FluidStack.isSameFluid(first, second);
        }

        @Override
        public FluidStack empty() {
            return FluidStack.EMPTY;
        }

        @Override
        public FluidStack copy(FluidStack source) {
            return source.copy();
        }

        @Override
        public FluidStack copyWithAmount(FluidStack source, long amount) {
            return source.copyWithAmount((int) amount);
        }

        @Override
        public long volume(FluidStack stack) {
            return stack.getAmount();
        }

        @Override
        public void grow(FluidStack stack, long amount) {
            stack.grow((int) amount);
        }

        @Override
        public void shrink(FluidStack stack, long amount) {
            stack.shrink((int) amount);
        }

        @Override
        public EmiStack toEmi(FluidStack stack) {
            return NeoForgeEmiStack.of(stack);
        }

        @Override
        public Codec<FluidStack> codec() {
            return FluidStack.CODEC;
        }

        @Override
        public MapCodec<FluidStack> mapCodec() {
            return codec().fieldOf(SerializationConstants.FLUID);
        }

        @Override
        public void addIngredient(FluidStack stack, IIngredientAcceptor<?> builder) {
            builder.addIngredient(NeoForgeTypes.FLUID_STACK, stack);
        }

        @Override
        public Function<FluidStack, TypedStack<FluidStack>> wrapper() {
            return Stacks::wrap;
        }
    };

    public static final StackAlgebra<ChemicalStack> CHEMICAL = new StackAlgebra<>() {

        @Override
        public ResourceLocation id() {
            return MekanismAPI.CHEMICAL_REGISTRY_NAME.location();
        }

        @Override
        public Class<ChemicalStack> value() {
            return ChemicalStack.class;
        }

        @Override
        public boolean notEmpty(ChemicalStack stack) {
            return !stack.isEmpty();
        }

        @Override
        public boolean sameType(ChemicalStack first, ChemicalStack second) {
            return ChemicalStack.isSameChemical(first, second);
        }

        @Override
        public ChemicalStack empty() {
            return ChemicalStack.EMPTY;
        }

        @Override
        public ChemicalStack copy(ChemicalStack source) {
            return source.copy();
        }

        @Override
        public ChemicalStack copyWithAmount(ChemicalStack source, long amount) {
            return source.copyWithAmount(amount);
        }

        @Override
        public long volume(ChemicalStack stack) {
            return stack.getAmount();
        }

        @Override
        public void grow(ChemicalStack stack, long amount) {
            stack.grow(amount);
        }

        @Override
        public void shrink(ChemicalStack stack, long amount) {
            stack.shrink(amount);
        }

        @Override
        public EmiStack toEmi(ChemicalStack stack) {
            return new ChemicalEmiStack(stack);
        }

        @Override
        public Codec<ChemicalStack> codec() {
            return ChemicalStack.CODEC;
        }

        @Override
        public MapCodec<ChemicalStack> mapCodec() {
            return codec().fieldOf(SerializationConstants.CHEMICAL);
        }

        @Override
        public void addIngredient(ChemicalStack stack, IIngredientAcceptor<?> builder) {
            builder.addIngredient(MekanismJEI.TYPE_CHEMICAL, stack);
        }

        @Override
        public Function<ChemicalStack, TypedStack<ChemicalStack>> wrapper() {
            return Stacks::wrap;
        }
    };

    public static final TypedStack<FluidStack> EMPTY_FLUID = wrap(FLUID.empty());

    public static final TypedStack<ChemicalStack> EMPTY_CHEMICAL = wrap(CHEMICAL.empty());

    private record FluidTypedStack(FluidStack value) implements TypedStack<FluidStack> {

        @Override
        public StackAlgebra<FluidStack> algebra() {
            return FLUID;
        }
    }

    private record ChemicalTypedStack(ChemicalStack value) implements TypedStack<ChemicalStack> {

        @Override
        public StackAlgebra<ChemicalStack> algebra() {
            return CHEMICAL;
        }
    }
}
