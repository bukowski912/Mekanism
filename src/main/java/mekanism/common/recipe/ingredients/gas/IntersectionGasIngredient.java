package mekanism.common.recipe.ingredients.gas;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import mekanism.api.JsonConstants;
import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.recipes.ingredients.chemical.IGasIngredient;
import mekanism.api.recipes.ingredients.chemical.IntersectionChemicalIngredient;
import mekanism.api.recipes.ingredients.creator.IngredientCreatorAccess;
import mekanism.common.registries.MekanismGasIngredientTypes;

@NothingNullByDefault
public final class IntersectionGasIngredient extends IntersectionChemicalIngredient<Gas, IGasIngredient> implements IGasIngredient {

    public static final MapCodec<IntersectionGasIngredient> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
          IngredientCreatorAccess.gas().listCodecMultipleElements().fieldOf(JsonConstants.CHILDREN).forGetter(IntersectionGasIngredient::children)
    ).apply(builder, IntersectionGasIngredient::new));

    IntersectionGasIngredient(List<IGasIngredient> children) {
        super(children);
    }

    @Override
    public MapCodec<IntersectionGasIngredient> codec() {
        return MekanismGasIngredientTypes.INTERSECTION.value();
    }
}