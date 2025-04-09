package mekanism.client.recipe_viewer.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mekanism.api.MekanismAPI;
import mekanism.api.SerializationConstants;
import mekanism.api.algebra.FormAlgebra;
import mekanism.api.algebra.TypedIngredient;
import mekanism.api.algebra.TypedStack;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.attribute.ChemicalAttributes;
import mekanism.api.datamaps.IMekanismDataMapTypes;
import mekanism.api.datamaps.chemical.attribute.HeatedCoolant;
import mekanism.api.recipes.ingredients.FluidStackIngredient;
import mekanism.api.recipes.ingredients.creator.IngredientCreatorAccess;
import mekanism.client.recipe_viewer.RecipeViewerUtils;
import mekanism.client.recipe_viewer.emi.INamedRVRecipe;
import mekanism.common.Mekanism;
import mekanism.common.algebra.Forms;
import mekanism.common.algebra.Ingredients;
import mekanism.common.algebra.Stacks;
import mekanism.common.config.MekanismConfig;
import mekanism.common.content.boiler.BoilerMultiblockData;
import mekanism.common.registries.MekanismChemicals;
import mekanism.common.util.HeatUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public record BoilerRecipeViewerRecipe(
      ResourceLocation id,
      TypedIngredient<?> heatedCoolant,
      FluidStackIngredient water,
      ChemicalStack steam,
      TypedStack<?> cooledCoolant,
      double temperature) implements INamedRVRecipe {

    private static final int WATER_AMOUNT = 1;
    private static final FluidStackIngredient DEFAULT_WATER_INPUT = IngredientCreatorAccess.fluid().from(FluidTags.WATER, WATER_AMOUNT);
    private static final ChemicalStack DEFAULT_STEAM_OUTPUT = MekanismChemicals.STEAM.asStack(WATER_AMOUNT);

    public static Codec<BoilerRecipeViewerRecipe> createBaseCodec(
          FormAlgebra<?> heated, FormAlgebra<?> cooled) {
        return RecordCodecBuilder.create(instance -> instance.group(
              ResourceLocation.CODEC.fieldOf(SerializationConstants.ID).forGetter(BoilerRecipeViewerRecipe::id),
              TypedIngredient.mapCodec(heated.ingredient()).forGetter(BoilerRecipeViewerRecipe::heatedCoolant),
              FluidStackIngredient.CODEC.optionalFieldOf(SerializationConstants.FLUID_INPUT, DEFAULT_WATER_INPUT).forGetter(BoilerRecipeViewerRecipe::water),
              ChemicalStack.CODEC.optionalFieldOf(SerializationConstants.MAIN_OUTPUT, DEFAULT_STEAM_OUTPUT).forGetter(BoilerRecipeViewerRecipe::steam),
              TypedStack.codec(cooled.stack()).optionalFieldOf(SerializationConstants.SECONDARY_OUTPUT, Stacks.EMPTY_CHEMICAL).forGetter(BoilerRecipeViewerRecipe::cooledCoolant),
              Codec.DOUBLE.optionalFieldOf(SerializationConstants.TEMPERATURE, HeatUtils.BASE_BOIL_TEMP).forGetter(BoilerRecipeViewerRecipe::temperature)
        ).apply(instance, BoilerRecipeViewerRecipe::new));
    }

    public static Codec<BoilerRecipeViewerRecipe> CHEMICAL_TO_FLUID_CODEC = createBaseCodec(Forms.CHEMICAL, Forms.FLUID);

    @SuppressWarnings("removal")
    public static List<BoilerRecipeViewerRecipe> getBoilerRecipes() {
        //Note: The recipes below ignore thermal conductivity and temperature and rounds the amount of coolant
        double waterToSteamHeatNecessary = WATER_AMOUNT * HeatUtils.getWaterThermalEnthalpy() / HeatUtils.getSteamEnergyEfficiency();
        List<BoilerRecipeViewerRecipe> recipes = new ArrayList<>();
        //Special case heat only recipe
        FluidStackIngredient water = IngredientCreatorAccess.fluid().from(FluidTags.WATER, WATER_AMOUNT);
        ChemicalStack steam = MekanismChemicals.STEAM.asStack(WATER_AMOUNT);
        recipes.add(new BoilerRecipeViewerRecipe(
              RecipeViewerUtils.synthetic(Mekanism.rl("water"), "boiler"),
              null, water,
              steam, Stacks.EMPTY_CHEMICAL,
              HeatUtils.BASE_BOIL_TEMP + waterToSteamHeatNecessary / (BoilerMultiblockData.CASING_HEAT_CAPACITY * MekanismConfig.general.boilerWaterConductivity.get())
        ));
        //Add recipes for all heated coolants
        for (Map.Entry<ResourceKey<Chemical>, HeatedCoolant> entry : MekanismAPI.CHEMICAL_REGISTRY.getDataMap(IMekanismDataMapTypes.INSTANCE.heatedChemicalCoolant()).entrySet()) {
            ResourceKey<Chemical> key = entry.getKey();
            HeatedCoolant coolant = entry.getValue();
            //Amount of coolant that is actually used to
            long coolantAmount = Math.round(waterToSteamHeatNecessary / coolant.thermalEnthalpy());
            recipes.add(new BoilerRecipeViewerRecipe(
                  RecipeViewerUtils.synthetic(key.location(), "boiler", Mekanism.MODID),
                  Ingredients.chemical(key, coolantAmount), water,
                  steam, Stacks.chemical(coolant.otherChemical(), coolantAmount),
                  HeatUtils.BASE_BOIL_TEMP
            ));
        }
        //TODO - 1.22: Remove this handling of legacy attributes
        //Go through all gases and add each legacy coolant
        for (Chemical gas : MekanismAPI.CHEMICAL_REGISTRY) {
            ChemicalAttributes.HeatedCoolant heatedCoolant = gas.getLegacy(ChemicalAttributes.HeatedCoolant.class);
            if (heatedCoolant != null) {
                //If it is a cooled coolant add a recipe for it
                long coolantAmount = Math.round(waterToSteamHeatNecessary / heatedCoolant.getThermalEnthalpy());
                recipes.add(new BoilerRecipeViewerRecipe(
                      RecipeViewerUtils.synthetic(gas.toString(), "boiler", Mekanism.MODID),
                      Ingredients.chemical(gas, coolantAmount), water,
                      steam, Stacks.wrap(heatedCoolant.getCooledChemical().getStack(coolantAmount)),
                      HeatUtils.BASE_BOIL_TEMP
                ));
            }
        }
        return recipes;
    }
}