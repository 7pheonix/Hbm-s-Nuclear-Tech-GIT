package com.hbm.inventory.recipes;

import java.util.HashMap;

import com.hbm.items.ModItems;
import com.hbm.util.Tuple.Pair;
import com.hbm.util.Tuple.Triplet;

import static com.hbm.inventory.OreDictManager.*;

import com.hbm.inventory.FluidStack;
import com.hbm.inventory.RecipesCommon.ComparableStack;
import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.fluid.Fluids;

import net.minecraft.item.ItemStack;

public class ElectrolysisRecipes {
	
	private static HashMap<Object, Object[]> oreRecipes = new HashMap();
	private static HashMap<FluidStack, Triplet<FluidStack, FluidStack, ItemStack>> fluidRecipes = new HashMap();
	
	public static void registerFluidRecipes() {
		
		fluidRecipes.put(new FluidStack(Fluids.WATER, 1000), new Triplet(
			new FluidStack(Fluids.OXYGEN, 100),
			new FluidStack(Fluids.HYDROGEN, 100),
			new ItemStack(ModItems.dust, 1)
		));
		
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
}
