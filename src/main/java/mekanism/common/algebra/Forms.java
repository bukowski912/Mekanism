package mekanism.common.algebra;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import mekanism.api.MekanismAPI;
import mekanism.api.SerializationConstants;
import mekanism.api.algebra.*;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.recipes.ingredients.ChemicalStackIngredient;
import mekanism.api.recipes.ingredients.FluidStackIngredient;
import mekanism.api.recipes.ingredients.creator.IngredientCreatorAccess;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.function.Function;

public final class Forms {

    public static TypedForm<Fluid> wrapFluid(Holder<Fluid> fluidHolder) {
        return new FluidTypedForm(fluidHolder);
    }

    public static TypedForm<Chemical> wrapChemical(Holder<Chemical> chemicalHolder) {
        return new ChemicalTypedForm(chemicalHolder);
    }

    @SuppressWarnings("unchecked")
    public static TypedForm<?> wrap(Holder<?> holder) {
        if (FLUID.matches(holder)) {
            return wrapFluid((Holder<Fluid>) holder);
        }
        if (CHEMICAL.matches(holder)) {
            return wrapChemical((Holder<Chemical>) holder);
        }
        throw new IllegalArgumentException("Holder isn't for fluids or chemicals");
    }

    private Forms() {
    }

    public static final FormAlgebra<Fluid> FLUID = new FormAlgebra<>() {

        @Override
        public Codec<Holder<Fluid>> codec() {
            return FluidStack.FLUID_NON_EMPTY_CODEC;
        }

        @Override
        public MapCodec<Holder<Fluid>> mapCodec() {
            return codec().fieldOf(SerializationConstants.FLUID);
        }

        @Override
        public Function<Holder<Fluid>, TypedForm<Fluid>> wrapper() {
            return Forms::wrapFluid;
        }

        @Override
        public Class<Fluid> value() {
            return Fluid.class;
        }

        @Override
        public Holder<Fluid> emptyHolder() {
            return BuiltInRegistries.FLUID.wrapAsHolder(Fluids.EMPTY);
        }

        @Override
        public boolean isEmpty(Holder<Fluid> holder) {
            return holder.is(BuiltInRegistries.FLUID.getKey(Fluids.EMPTY));
        }

        @Override
        public ResourceKey<Registry<Fluid>> registryKey() {
            return Registries.FLUID;
        }

        @Override
        public StackAlgebra<FluidStack> stack() {
            return Stacks.FLUID;
        }

        @Override
        public IngredientAlgebra<FluidStackIngredient> ingredient() {
            return Ingredients.FLUID;
        }

        @Override
        public TypedIngredient<?> createIngredient(ResourceKey<Fluid> key, long amount) {
            return ingredient().wrap(IngredientCreatorAccess.fluid().fromHolder(BuiltInRegistries.FLUID.getHolderOrThrow(key), (int) amount));
        }

        @Override
        public TypedIngredient<?> createIngredient(Fluid type, long amount) {
            return ingredient().wrap(IngredientCreatorAccess.fluid().from(type, (int) amount));
        }

        @Override
        public StackFactory<FluidStack, Fluid> stackFactory() {
            return Stacks::fluid;
        }
    };

    public static final FormAlgebra<Chemical> CHEMICAL = new FormAlgebra<>() {

        @Override
        public Codec<Holder<Chemical>> codec() {
            return ChemicalStack.CHEMICAL_NON_EMPTY_HOLDER_CODEC;
        }

        @Override
        public MapCodec<Holder<Chemical>> mapCodec() {
            return codec().fieldOf(SerializationConstants.CHEMICAL);
        }

        @Override
        public Function<Holder<Chemical>, TypedForm<Chemical>> wrapper() {
            return Forms::wrapChemical;
        }

        @Override
        public Class<Chemical> value() {
            return Chemical.class;
        }

        @Override
        public Holder<Chemical> emptyHolder() {
            return MekanismAPI.EMPTY_CHEMICAL_HOLDER;
        }

        @Override
        public boolean isEmpty(Holder<Chemical> holder) {
            return holder.is(MekanismAPI.EMPTY_CHEMICAL_KEY);
        }

        @Override
        public ResourceKey<Registry<Chemical>> registryKey() {
            return MekanismAPI.CHEMICAL_REGISTRY_NAME;
        }

        @Override
        public StackAlgebra<ChemicalStack> stack() {
            return Stacks.CHEMICAL;
        }

        @Override
        public IngredientAlgebra<ChemicalStackIngredient> ingredient() {
            return Ingredients.CHEMICAL;
        }

        @Override
        public TypedIngredient<?> createIngredient(ResourceKey<Chemical> key, long amount) {
            return ingredient().wrap(IngredientCreatorAccess.chemicalStack().fromHolder(MekanismAPI.CHEMICAL_REGISTRY.getHolderOrThrow(key), amount));
        }

        @Override
        public TypedIngredient<?> createIngredient(Chemical type, long amount) {
            return ingredient().wrap(IngredientCreatorAccess.chemicalStack().from(type, amount));
        }

        @Override
        public StackFactory<ChemicalStack, Chemical> stackFactory() {
            return Stacks::chemical;
        }
    };

    private record FluidTypedForm(Holder<Fluid> holder) implements TypedForm<Fluid> {

        @Override
        public Fluid value() {
            return holder.value();
        }

        @Override
        public FormAlgebra<Fluid> algebra() {
            return FLUID;
        }
    }

    private record ChemicalTypedForm(Holder<Chemical> holder) implements TypedForm<Chemical> {

        @Override
        public Chemical value() {
            return holder.value();
        }

        @Override
        public FormAlgebra<Chemical> algebra() {
            return CHEMICAL;
        }
    }
}
