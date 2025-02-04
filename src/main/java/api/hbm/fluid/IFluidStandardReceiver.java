package api.hbm.fluid;

import com.hbm.inventory.FluidTank;
import com.hbm.inventory.fluid.FluidType;

/**
 * Uses default implementation to make the underlying interfaces easier to use for the most common fluid users.
 * Only handles a single input tank of the same type.
 * Uses standard FluidTanks which use int32.
 * Don't use this as part of the API!
 * @author hbm
 *
 */
public interface IFluidStandardReceiver extends IFluidUser {
	
	@Override
	public default long transferFluid(FluidType type, long amount) {

		for(FluidTank tank : getReceivingTanks()) {
			if(tank.getTankType() == type) {
				tank.setFill(tank.getFill() + (int) amount);
				
				if(tank.getFill() > tank.getMaxFill()) {
					long overshoot = tank.getFill() - tank.getMaxFill();
					tank.setFill(tank.getMaxFill());
					return overshoot;
				}
				
				return 0;
			}
		}
		
		return amount;
	}

	public FluidTank[] getReceivingTanks();

	@Override
	public default long getDemand(FluidType type) {
		
		for(FluidTank tank : getReceivingTanks()) {
			if(tank.getTankType() == type) {
				return tank.getMaxFill() - tank.getFill();
			}
		}
		
		return 0;
	}
}
