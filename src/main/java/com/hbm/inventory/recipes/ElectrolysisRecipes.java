package com.hbm.inventory.recipes;

import java.util.HashMap;

import com.hbm.items.ModItems;
import static com.hbm.inventory.OreDictManager.*;
import com.hbm.inventory.RecipesCommon.ComparableStack;

import net.minecraft.item.ItemStack;

public class ElectrolysisRecipes {
	
	private static HashMap<Object, Object[]> recipes = new HashMap();
	
	public static void register() {
		
		recipes.put(new ComparableStack(ModItems.crystal_copper), new Object[] {
				new MetalTankOutput(Metals.COPPER, 5000),
				new MetalTankOutput(Metals.COBALT, 2000),
				
				new ItemStack(ModItems.sulfur, 2),
				new ItemStack(ModItems.powder_iron),
		});	
	}

	// perhaps condense this down?
	// yoinky sploinky
	public static class MetalTankOutput {
	
		public Metals type;
		public int volume;
		
		public MetalTankOutput(Metals type, int vol) {
			this.type = type;
			this.volume = vol;
		}
	}
	
	public static enum Metals {
		COPPER(CU, 0xFF4800),
		COBALT(CO, 0x0A25A0);
		
		DictFrame dictKey;
		int colour;
		
		private Metals(DictFrame dictKey, int colour) {
			this.dictKey = dictKey;
			//instead of using a name string, automatically jam together an unlocalised name using the name of the enum entry
			this.colour = colour;
		}
	}
}
