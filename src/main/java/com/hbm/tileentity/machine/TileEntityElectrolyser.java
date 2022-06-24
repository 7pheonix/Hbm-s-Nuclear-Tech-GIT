package com.hbm.tileentity.machine;

import java.util.ArrayList;
import java.util.List;

import com.hbm.blocks.BlockDummyable;
import com.hbm.interfaces.IFluidAcceptor;
import com.hbm.interfaces.IFluidSource;
import com.hbm.interfaces.Spaghetti;
import com.hbm.inventory.FluidStack;
import com.hbm.inventory.FluidTank;
import com.hbm.inventory.UpgradeManager;
import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.lib.Library;
import com.hbm.tileentity.TileEntityMachineBase;
import com.hbm.util.Tuple.Pair;
import com.hbm.inventory.recipes.ElectrolysisRecipes;
import com.hbm.inventory.recipes.ElectrolysisRecipes.*;
import com.hbm.items.ModItems;
import com.hbm.items.machine.ItemMachineUpgrade.UpgradeType;

import static com.hbm.inventory.recipes.ElectrolysisRecipes.Metals.*;

import api.hbm.energy.IEnergyUser;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityElectrolyser extends TileEntityMachineBase implements IEnergyUser, IFluidSource, IFluidAcceptor /* TODO: new fluid API */ {
	
	public long power;
	public static final long maxPower = 20000000;
	public static final int usageBase = 1000;
	public int usage;
	
	public static final int maxProgress = 1000;
	public static final int processFluidTactRate = 500;
	public int progressOre;
	public static final int processOreTimeBase = 1000;
	public int processOreTime;
	
	public float effectMultiplier;
	
	public static final int maxMetal = 256000;
	public int primaryMetalTank;
	public Metals primaryMetal = COPPER;
	public int secondaryMetalTank;
	public Metals secondaryMetal = COBALT;
	
	public static final int maxNiter = 64000;
	public int niterTank;
	
	public FluidTank[] tanks;
	List<IFluidAcceptor>[] lists = new List[] {
			new ArrayList(), new ArrayList(), new ArrayList(), new ArrayList()
		};
	
	public static final int[] slot_io = new int[] {11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23};

	public TileEntityElectrolyser() {
		super(24);
		tanks = new FluidTank[3];
		tanks[0] = new FluidTank(Fluids.WATER, 16000, 0);
		tanks[1] = new FluidTank(Fluids.HYDROGEN, 16000, 1);
		tanks[2] = new FluidTank(Fluids.OXYGEN, 16000, 2);
	}

	@Override
	public String getName() {
		return "container.machineElectrolyser";
	}
	
	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemStack) {
		
		if(i == 15 && itemStack.getItem() == ModItems.niter)
			return true;
		
		return i == 14;
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int side) {
		return slot_io;
	}


	@Override
	public void updateEntity() {

		if(!worldObj.isRemote) {
			
			for(byte x = 0; x < 3; x++) {
				this.tanks[x].updateTank(xCoord, yCoord, zCoord, worldObj.provider.dimensionId);
			}
			
			ForgeDirection dir = ForgeDirection.getOrientation(this.getBlockMetadata() - BlockDummyable.offset);
			ForgeDirection rot = dir.getRotation(ForgeDirection.UP);
			this.trySubscribe(worldObj, xCoord + dir.offsetX * 5 + rot.offsetX * 1, yCoord, zCoord + dir.offsetZ * 5 + rot.offsetZ * 1, dir);
			this.trySubscribe(worldObj, xCoord + dir.offsetX * -5 + rot.offsetX * 1, yCoord, zCoord + dir.offsetZ * -5 + rot.offsetZ * 1,  dir);
			this.trySubscribe(worldObj, xCoord + dir.offsetX * 5 + rot.offsetX * -1, yCoord, zCoord + dir.offsetZ * 5 + rot.offsetZ * -1, dir);
			this.trySubscribe(worldObj, xCoord + dir.offsetX * -5 + rot.offsetX * -1, yCoord, zCoord + dir.offsetZ * -5 + rot.offsetZ * -1, dir);
			
			this.power = Library.chargeTEFromItems(slots, 0, power, maxPower);
			
			UpgradeManager.eval(slots, 1, 2);
			int speed = Math.min(UpgradeManager.getLevel(UpgradeType.SPEED), 3);
			/*int power = Math.min(UpgradeManager.getLevel(UpgradeType.POWER), 3);
			int effect = Math.min(UpgradeManager.getLevel(UpgradeType.EFFECT), 3);

			//this.processFluidTime = processFluidTimeBase - (processFluidTimeBase / 4) * speed;
			this.processOreTime = processOreTimeBase - (processOreTimeBase / 4) * speed;
			this.usage = usageBase - (usageBase / 4) * power;
			this.effectMultiplier = 1 + (effect*0.2F);*/
			
			tanks[0].setType(3, 4, slots);
			tanks[0].loadTank(5, 6, slots);
			tanks[1].unloadTank(7, 8, slots);
			tanks[2].unloadTank(9, 10, slots);
			
			updateTanks();
			
			if(worldObj.getTotalWorldTime() % (20-(speed * 4)) == 0)
				doFluidProcessingCycle();
			
			if(slots[15] != null) {
				if(slots[15].getItem() == ModItems.niter && maxNiter - niterTank >= 100) {
					niterTank += 500;
					this.decrStackSize(15, 1);
				}
			}
			if(niterTank < 0) {
				niterTank = 0;
			} else if (niterTank > maxNiter) {
				niterTank = maxNiter;
			}
			
			fillFluidInit(tanks[1].getTankType());
			fillFluidInit(tanks[2].getTankType());
			
			NBTTagCompound data = new NBTTagCompound();
			data.setLong("power", this.power);
			data.setInteger("progressOre", progressOre);
			data.setInteger("usage", usage);
			data.setInteger("processOreTime", processOreTime);
			
			data.setInteger("primaryMetalTank", primaryMetalTank);
			data.setString("primaryMetal", primaryMetal.toString());
			data.setInteger("secondaryMetalTank", secondaryMetalTank);
			data.setString("secondaryMetal", secondaryMetal.toString());
			
			data.setInteger("niterTank", niterTank);
			
			data.setFloat("effectMultiplier", effectMultiplier);
			this.networkPack(data, 50);
			
		}

	}
	
	private void updateTanks() {
		Pair outputs = ElectrolysisRecipes.getFluidTypes(tanks[0].getTankType());
		if(outputs == null) {
			for(byte i = 0; i < 3; i ++) {
				tanks[i].setTankType(Fluids.NONE);
			}
			return;
		}
		tanks[1].setTankType((FluidType)outputs.getKey());
		tanks[2].setTankType((FluidType)outputs.getValue());
	}
	
	@Spaghetti("that was fast")
	private void doFluidProcessingCycle() {
		
		Object[] outs = ElectrolysisRecipes.getFluidOutputs(tanks[0].getTankType());
		
		if(outs == null)
			return;
		
		Pair inputData = (Pair)outs[0];
		if(tanks[0].getFill() >= (int)inputData.getKey() && power >= (int)inputData.getValue()) {
			if(!(outs[1] instanceof FluidStack) || outs[1] == null || !(outs[2] instanceof FluidStack) || outs[2] == null) {
				return;
			} else {
				if((tanks[1].getMaxFill() - tanks[1].getFill()) >= ((FluidStack)outs[1]).fill && (tanks[2].getMaxFill() - tanks[2].getFill()) >= ((FluidStack)outs[2]).fill) {
					tanks[0].setFill(tanks[0].getFill() - (int)inputData.getKey());
					tanks[1].setFill(tanks[1].getFill() + ((FluidStack)outs[1]).fill);
					tanks[2].setFill(tanks[2].getFill() + ((FluidStack)outs[2]).fill);
					power -= (int)inputData.getValue();
					for(byte i = 3; i < outs.length; i++) {
						if(outs[i] instanceof Pair) {
							if(((Pair)outs[i]).getKey() instanceof ItemStack && ((Pair)outs[i]).getValue() instanceof Float) {
								ItemStack item = (ItemStack)((Pair)outs[i]).getKey();
								float chance = (float)((Pair)outs[i]).getValue();
								if(worldObj.rand.nextInt((int)(1F / chance)) == 0) {
									if(slots[8+i] == null)
										slots[8+i] = item.copy();
									else
										slots[8+i].stackSize++;
								}
							}
						}
					}
				}
			}
		}
	}

	@Override
	public void fillFluidInit(FluidType type) {
		
		ForgeDirection dir = ForgeDirection.getOrientation(this.getBlockMetadata() - BlockDummyable.offset);
		ForgeDirection rot = dir.getRotation(ForgeDirection.UP);

		fillFluid(xCoord + dir.offsetX * 5 + rot.offsetX * 1, yCoord, zCoord + rot.offsetZ * 1 + dir.offsetZ * 5, getTact(), type);
		fillFluid(xCoord + dir.offsetX * 5 + rot.offsetX * -1, yCoord, zCoord + rot.offsetZ * -1 + dir.offsetZ * 5, getTact(), type);
		fillFluid(xCoord + dir.offsetX * -5 + rot.offsetX * 1, yCoord, zCoord + rot.offsetZ * 1 + dir.offsetZ * -5, getTact(), type);
		fillFluid(xCoord + dir.offsetX * -5 + rot.offsetX * -1, yCoord, zCoord + rot.offsetZ * -1 + dir.offsetZ * -5, getTact(), type);
		
	}
	
	AxisAlignedBB bb = null;
	
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		
		if(bb == null) {
			bb = AxisAlignedBB.getBoundingBox(
					xCoord - 3 - 0.5,
					yCoord - 0 - 0.5,
					zCoord - 5 - 0.5,
					xCoord + 3 + 0.5,
					yCoord + 4 + 0.5,
					zCoord + 5 + 0.5
					);
		}
		
		return bb;
	}
	
	@Override
	public void networkUnpack(NBTTagCompound nbt) {
		this.power = nbt.getLong("power");
		this.progressOre = nbt.getInteger("progressOre");
		this.usage = nbt.getInteger("usage");
		this.processOreTime = nbt.getInteger("processOreTime");
		
		this.primaryMetalTank = nbt.getInteger("primaryMetalTank");
		this.primaryMetal = Metals.valueOf(nbt.getString("primaryMetal"));
		this.secondaryMetalTank = nbt.getInteger("secondaryMetalTank");
		this.secondaryMetal = Metals.valueOf(nbt.getString("secondaryMetal"));
		
		this.niterTank = nbt.getInteger("niterTank");
		
		this.effectMultiplier = nbt.getFloat("effectMultiplier");
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		for(int i = 0; i < 3; i++) {
			tanks[i].readFromNBT(nbt, "tank"+(char)i);
		}
		
		this.power = nbt.getLong("power");
		this.progressOre = nbt.getInteger("progressOre");
		this.usage = nbt.getInteger("usage");
		this.processOreTime = nbt.getInteger("processOreTime");
		
		this.primaryMetalTank = nbt.getInteger("primaryMetalTank");
		this.primaryMetal = Metals.valueOf(nbt.getString("primaryMetal"));
		this.secondaryMetalTank = nbt.getInteger("secondaryMetalTank");
		this.secondaryMetal = Metals.valueOf(nbt.getString("secondaryMetal"));
		
		this.niterTank = nbt.getInteger("niterTank");
		
		this.effectMultiplier = nbt.getFloat("effectMultiplier"); 
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		for(int i = 0; i < 3; i++) {
			tanks[i].writeToNBT(nbt, "tank"+(char)i);
		}
		
		nbt.setLong("power", this.power);
		nbt.setInteger("progressOre", progressOre);
		nbt.setInteger("usage", usage);
		nbt.setInteger("processOreTime", processOreTime);
		
		nbt.setInteger("primaryMetalTank", primaryMetalTank);
		nbt.setString("primaryMetal", primaryMetal.toString());
		nbt.setInteger("secondaryMetalTank", secondaryMetalTank);
		nbt.setString("secondaryMetal", secondaryMetal.toString());
		
		nbt.setInteger("niterTank", niterTank);
		
		nbt.setFloat("effectMultiplier", effectMultiplier);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public double getMaxRenderDistanceSquared() {
		return 65536.0D;
	}

	@Override
	public long getPower() {
		return this.power;
	}

	@Override
	public long getMaxPower() {
		return maxPower;
	}

	@Override
	public void setFillForSync(int fill, int index) {
		tanks[index].setFill(fill);
		
	}

	@Override
	public void setFluidFill(int fill, FluidType type) {
		for(int i = 0; i < 3; i++) {
			if(type == tanks[i].getTankType())
				tanks[i].setFill(fill);
		}
		
	}

	@Override
	public void setTypeForSync(FluidType type, int index) {
		tanks[index].setTankType(type);
		
	}

	@Override
	public int getFluidFill(FluidType type) {
		for(int i = 0; i < 3; i++) {
			if(type == tanks[i].getTankType())
				return tanks[i].getFill();
		}
		return 0;
	}

	@Override
	public int getMaxFluidFill(FluidType type) {
		for(int i = 0; i < 3; i++) {
			if(type == tanks[i].getTankType())
				return tanks[i].getMaxFill();
		}
		return 0;
	}

	@Override
	public void fillFluid(int x, int y, int z, boolean newTact, FluidType type) {
		Library.transmitFluid(x, y, z, newTact, this, worldObj, type);
	}

	@Override
	public boolean getTact() {
		return worldObj.getTotalWorldTime() % 2 == 0;
	}

	@Override
	public List<IFluidAcceptor> getFluidList(FluidType type) {
		
		for(int i = 0; i < tanks.length; i++) {
			if(tanks[i].getTankType() == type) {
				return lists[i];
			}
		}
		
		return new ArrayList();
	}

	@Override
	public void clearFluidList(FluidType type) {
		
		for(int i = 0; i < tanks.length; i++) {
			if(tanks[i].getTankType() == type) {
				lists[i].clear();
			}
		}
	}

	@Override
	public void setPower(long power) {
		this.power = power;
	}

}
