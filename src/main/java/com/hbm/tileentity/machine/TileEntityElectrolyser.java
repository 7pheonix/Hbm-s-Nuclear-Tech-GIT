package com.hbm.tileentity.machine;

import java.util.ArrayList;
import java.util.List;

import com.hbm.blocks.BlockDummyable;
import com.hbm.interfaces.IFluidAcceptor;
import com.hbm.interfaces.IFluidSource;
import com.hbm.inventory.FluidTank;
import com.hbm.inventory.UpgradeManager;
import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.lib.Library;
import com.hbm.tileentity.TileEntityMachineBase;
import com.hbm.inventory.recipes.ElectrolysisRecipes.*;
import com.hbm.items.ModItems;
import com.hbm.items.machine.ItemMachineUpgrade.UpgradeType;

import static com.hbm.inventory.recipes.ElectrolysisRecipes.Metals.*;

import api.hbm.energy.IEnergyUser;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityElectrolyser extends TileEntityMachineBase implements IEnergyUser, IFluidSource, IFluidAcceptor{
	
	public long power;
	public static final long maxPower = 20000000;
	public static final int usageBase = 10000;
	public int usage;
	
	public static final int maxProgress = 1000;
	public int progressFluid;
	public static final int processFluidTimeBase = 500;
	public int processFluidTime;
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
	public void updateEntity() {

		if(!worldObj.isRemote) {
			
			this.tanks[0].updateTank(xCoord, yCoord, zCoord, worldObj.provider.dimensionId);
			
			ForgeDirection dir = ForgeDirection.getOrientation(this.getBlockMetadata() - BlockDummyable.offset);
			ForgeDirection rot = dir.getRotation(ForgeDirection.UP);
			this.trySubscribe(worldObj, xCoord + dir.offsetX * 5 + rot.offsetX * 1, yCoord, zCoord + dir.offsetZ * 5 + rot.offsetZ * 1, dir);
			this.trySubscribe(worldObj, xCoord + dir.offsetX * -5 + rot.offsetX * 1, yCoord, zCoord + dir.offsetZ * -5 + rot.offsetZ * 1,  dir);
			this.trySubscribe(worldObj, xCoord + dir.offsetX * 5 + rot.offsetX * -1, yCoord, zCoord + dir.offsetZ * 5 + rot.offsetZ * -1, dir);
			this.trySubscribe(worldObj, xCoord + dir.offsetX * -5 + rot.offsetX * -1, yCoord, zCoord + dir.offsetZ * -5 + rot.offsetZ * -1, dir);
			
			this.power = Library.chargeTEFromItems(slots, 0, power, maxPower);
			
			UpgradeManager.eval(slots, 1, 2);
			int speed = Math.min(UpgradeManager.getLevel(UpgradeType.SPEED), 3);
			int power = Math.min(UpgradeManager.getLevel(UpgradeType.POWER), 3);
			int effect = Math.min(UpgradeManager.getLevel(UpgradeType.EFFECT), 3);

			this.processFluidTime = processFluidTimeBase - (processFluidTimeBase / 4) * speed;
			this.processOreTime = processOreTimeBase - (processOreTimeBase / 4) * speed;
			this.usage = usageBase - (usageBase / 4) * power;
			this.effectMultiplier = 1 + (effect*0.2F);
			
			tanks[0].setType(3, 4, slots);
			tanks[0].loadTank(5, 5, slots);
			tanks[1].unloadTank(7, 8, slots);
			tanks[2].unloadTank(9, 10, slots);
			
			updateTanks();
			
			if(slots[15] != null) {
				if(slots[15].getItem() == ModItems.niter && maxNiter - niterTank >= 100) {
					niterTank += 100;
					System.out.println(niterTank);
					//slots[9] = slots[9].splitStack(slots[9].stackSize - 1);
					this.decrStackSize(15, 1);
				}
			}
			if(niterTank < 0) {
				niterTank = 0;
			} else if (niterTank > maxNiter) {
				niterTank = maxNiter;
			}
			
			NBTTagCompound data = new NBTTagCompound();
			data.setLong("power", this.power);
			data.setInteger("progressFluid", progressFluid);
			data.setInteger("progressOre", progressOre);
			data.setInteger("usage", usage);
			data.setInteger("processFluidTime", processFluidTime);
			data.setInteger("processOreTime", processOreTime);
			
			data.setInteger("primaryMetalTank", primaryMetalTank);
			data.setString("primaryMetal", primaryMetal.toString());
			data.setInteger("secondaryMetalTank", secondaryMetalTank);
			data.setString("secondaryMetal", secondaryMetal.toString());
			
			data.setFloat("effectMultiplier", effectMultiplier);
			this.networkPack(data, 50);
			
			fillFluidInit(tanks[1].getTankType());
			fillFluidInit(tanks[2].getTankType());
		}

	}
	
	private void updateTanks() {
		//FluidType type = tanks[0].getTankType();
		
	}

	@Override
	public void fillFluidInit(FluidType type) {
		
		ForgeDirection dir = ForgeDirection.getOrientation(this.getBlockMetadata() - BlockDummyable.offset);
		ForgeDirection rot = dir.getRotation(ForgeDirection.UP);

		fillFluid(xCoord + dir.offsetX * 5 + rot.offsetX * -1, yCoord, zCoord + dir.offsetZ * 5 + rot.offsetZ * -1, getTact(), type);
		fillFluid(xCoord + dir.offsetX * 5 + rot.offsetX * -1, yCoord, zCoord + dir.offsetZ * 5 + rot.offsetZ * 1, getTact(), type);
		fillFluid(xCoord + dir.offsetX * -5 + rot.offsetX * -1, yCoord, zCoord + dir.offsetZ * 5 + rot.offsetZ * -1, getTact(), type);
		fillFluid(xCoord + dir.offsetX * -5 + rot.offsetX * -1, yCoord, zCoord + dir.offsetZ * 5 + rot.offsetZ * 1, getTact(), type);

	}
	
	AxisAlignedBB bb = null;
	
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		
		if(bb == null) {
			bb = AxisAlignedBB.getBoundingBox(
					xCoord - 3,
					yCoord - 0,
					zCoord - 5,
					xCoord + 3,
					yCoord + 4,
					zCoord + 5
					);
		}
		
		return bb;
	}
	
	@Override
	public void networkUnpack(NBTTagCompound nbt) {
		this.power = nbt.getLong("power");
		this.progressFluid = nbt.getInteger("progressFluid");
		this.progressOre = nbt.getInteger("progressOre");
		this.usage = nbt.getInteger("usage");
		this.processFluidTime = nbt.getInteger("processFluidTime");
		this.processOreTime = nbt.getInteger("processOreTime");
		
		this.primaryMetalTank = nbt.getInteger("primaryMetalTank");
		this.primaryMetal = Metals.valueOf(nbt.getString("primaryMetal"));
		this.secondaryMetalTank = nbt.getInteger("secondaryMetalTank");
		this.secondaryMetal = Metals.valueOf(nbt.getString("secondaryMetal"));
		
		this.effectMultiplier = nbt.getFloat("effectMultiplier");
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		for(int i = 0; i < 3; i++) {
			tanks[i].readFromNBT(nbt, "tank"+(char)i);
		}
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		for(int i = 0; i < 3; i++) {
			tanks[i].writeToNBT(nbt, "tank"+(char)i);
		}
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
			if(type == tanks[i].getTankType() && tanks[i].getFill() != 0)
				return tanks[i].getFill();
		}
		return 0;
	}

	@Override
	public int getMaxFluidFill(FluidType type) {
		for(int i = 0; i < 3; i++) {
			if(type == tanks[i].getTankType() && tanks[i].getMaxFill() != 0)
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
		return worldObj.getTotalWorldTime() % 20 < 10;
	}

	@Override
	public List<IFluidAcceptor> getFluidList(FluidType type) {
		return new ArrayList<IFluidAcceptor>();
	}

	@Override
	public void clearFluidList(FluidType type) {
		return;
	}

	@Override
	public void setPower(long power) {
		this.power = power;
	}

}
