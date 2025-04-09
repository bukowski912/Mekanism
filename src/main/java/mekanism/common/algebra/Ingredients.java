package mekanism.common.algebra;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.emi.emi.api.stack.EmiIngredient;
import mekanism.api.SerializationConstants;
import mekanism.api.algebra.FormAlgebra;
import mekanism.api.algebra.IngredientAlgebra;
import mekanism.api.algebra.TypedIngredient;
import mekanism.api.algebra.TypedStack;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.recipes.ingredients.ChemicalStackIngredient;
import mekanism.api.recipes.ingredients.FluidStackIngredient;
import mekanism.client.recipe_viewer.emi.recipe.MekanismEmiRecipe;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

public final class Ingredients {

    private Ingredients() {
    }

    public static TypedIngredient<?> chemical(ResourceKey<Chemical> key, long amount) {
        return Forms.CHEMICAL.createIngredient(key, amount);
    }

    @SuppressWarnings("deprecation")
    public static TypedIngredient<?> chemical(Chemical type, long amount) {
        return Forms.CHEMICAL.createIngredient(type, amount);
    }

    public static TypedIngredient<FluidStackIngredient> wrap(FluidStackIngredient value) {
        return new FluidTypedIngredient(value);
    }

    public static TypedIngredient<ChemicalStackIngredient> wrap(ChemicalStackIngredient value) {
        return new ChemicalTypedIngredient(value);
    }

    public static Either<FluidStackIngredient, ChemicalStackIngredient> unwrap(TypedIngredient<?> ingredient) {
        return Objects.requireNonNull(TypedIngredient.unwrap(ingredient, FLUID, CHEMICAL));
    }

    public static <R> R unwrap(TypedIngredient<?> ingredient, Function<FluidStackIngredient, R> fluid, Function<ChemicalStackIngredient, R> chemical) {
        return unwrap(ingredient).map(fluid, chemical);
    }

    public static final IngredientAlgebra<FluidStackIngredient> FLUID = new IngredientAlgebra<>() {

        @Override
        public FormAlgebra<Fluid> form() {
            return Forms.FLUID;
        }

        @Override
        public FluidStackIngredient empty() {
            return null; //TODO
        }

        @Override
        public EmiIngredient toEmi(FluidStackIngredient ingredient) {
            return MekanismEmiRecipe.fluidIngredient(ingredient);
        }

        @Override
        public Class<FluidStackIngredient> value() {
            return FluidStackIngredient.class;
        }

        @Override
        public Codec<FluidStackIngredient> codec() {
            return FluidStackIngredient.CODEC;
        }

        @Override
        public MapCodec<FluidStackIngredient> mapCodec() {
            return codec().optionalFieldOf(SerializationConstants.FLUID_INPUT, empty());
        }

        @Override
        public Function<FluidStackIngredient, TypedIngredient<FluidStackIngredient>> wrapper() {
            return Ingredients::wrap;
        }

        @Override
        public Stream<TypedStack<?>> representations(FluidStackIngredient value) {
            return value.getRepresentations().stream().map(Stacks::wrap);
        }
    };

    public static final IngredientAlgebra<ChemicalStackIngredient> CHEMICAL = new IngredientAlgebra<>() {

        @Override
        public FormAlgebra<Chemical> form() {
            return Forms.CHEMICAL;
        }

        @Override
        public ChemicalStackIngredient empty() {
            return null; //TODO
        }

        @Override
        public EmiIngredient toEmi(ChemicalStackIngredient ingredient) {
            return MekanismEmiRecipe.chemicalIngredient(ingredient);
        }

        @Override
        public Class<ChemicalStackIngredient> value() {
            return ChemicalStackIngredient.class;
        }

        @Override
        public Codec<ChemicalStackIngredient> codec() {
            return ChemicalStackIngredient.CODEC;
        }

        @Override
        public MapCodec<ChemicalStackIngredient> mapCodec() {
            return codec().optionalFieldOf(SerializationConstants.CHEMICAL_INPUT, empty());
        }

        @Override
        public Function<ChemicalStackIngredient, TypedIngredient<ChemicalStackIngredient>> wrapper() {
            return Ingredients::wrap;
        }

        @Override
        public Stream<TypedStack<?>> representations(ChemicalStackIngredient value) {
            return value.getRepresentations().stream().map(Stacks::wrap);
        }
    };

    private record FluidTypedIngredient(FluidStackIngredient value) implements TypedIngredient<FluidStackIngredient> {

        @Override
        public IngredientAlgebra<FluidStackIngredient> algebra() {
            return FLUID;
        }
    }

    private record ChemicalTypedIngredient(ChemicalStackIngredient value) implements TypedIngredient<ChemicalStackIngredient> {

        @Override
        public IngredientAlgebra<ChemicalStackIngredient> algebra() {
            return CHEMICAL;
        }
    }
}
