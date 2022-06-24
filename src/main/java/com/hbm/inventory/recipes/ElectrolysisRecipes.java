package com.hbm.inventory.recipes;

import java.util.HashMap;

import com.hbm.items.ModItems;
import com.hbm.util.Tuple.Pair;
import com.hbm.util.Tuple.Triplet;
import com.hbm.util.WeightedRandomObject;

import static com.hbm.inventory.OreDictManager.*;

import com.hbm.inventory.FluidStack;
import com.hbm.inventory.RecipesCommon.ComparableStack;
import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.fluid.Fluids;

import net.minecraft.item.ItemStack;

public class ElectrolysisRecipes {
	
	private static HashMap<Object, Object[]> oreRecipes = new HashMap();
	private static HashMap<FluidType, Object[]> fluidRecipes = new HashMap();
	
	public static void registerFluidRecipes() {
		
		//recipes will process once every half a second
		//first pair contains the input fluid volume for the recipe and the power consumed per operation
		//fluidstacks are outputs
		//additional pairs are Output items + their chance of appearing between 0 and 1
		
		fluidRecipes.put(Fluids.WATER, new Object[] {
			new Pair(500, 1000),
			new FluidStack(Fluids.OXYGEN, 50),
			new FluidStack(Fluids.HYDROGEN, 100),
			new Pair(new ItemStack(ModItems.powder_bromine), 0.01F),
			new Pair(new ItemStack(ModItems.powder_iodine), 0.01F),
			new Pair(new ItemStack(ModItems.powder_lithium), 0.005F)
		});
		
		fluidRecipes.put(Fluids.HEAVYWATER, new Object[] {
			new Pair(500, 1000),
			new FluidStack(Fluids.OXYGEN, 50),
			new FluidStack(Fluids.DEUTERIUM, 100),
			new Pair(new ItemStack(ModItems.powder_bromine), 0.04F),
			new Pair(new ItemStack(ModItems.powder_iodine), 0.04F),
			new Pair(new ItemStack(ModItems.powder_lithium), 0.02F)
		});
		
		fluidRecipes.put(Fluids.SULFURIC_ACID, new Object[] {
			new Pair(500, 1000),
			new FluidStack(Fluids.WATER, 250),
			new FluidStack(Fluids.NONE, 0),
			new Pair(new ItemStack(ModItems.sulfur), 1F)
		});
		
	}
	
	public static void registerOreRecipes() {
		
		oreRecipes.put(new ComparableStack(ModItems.crystal_copper), new Object[] {
			new Pair(Metals.COPPER, 5000),
			new Pair(Metals.COBALT, 2000),
				
			new ItemStack(ModItems.sulfur, 2),
			new ItemStack(ModItems.powder_iron),
		});	
	}
	
	public static enum Metals {
		COPPER(CU, 0xFF4800),
		COBALT(CO, 0x0A25A0);
		
		public DictFrame dictKey;
		public int colour;
		
		private Metals(DictFrame dictKey, int colour) {
			this.dictKey = dictKey;
			//instead of using a name string, automatically jam together an unlocalised name using the name of the enum entry
			this.colour = colour;
		}
	}
	
	public static Pair<FluidStack, FluidStack> getFluidTypes(FluidType type) {
		Object[] outputs = fluidRecipes.get(type);
		if(outputs == null)
			return null;
		FluidType fluid1 = ((FluidStack)outputs[1]).type;
		FluidType fluid2 = ((FluidStack)outputs[2]).type;
		return new Pair(fluid1, fluid2);
	}
	
	public static Object[] getFluidOutputs(FluidType type) {
		return fluidRecipes.get(type);
	}
}
