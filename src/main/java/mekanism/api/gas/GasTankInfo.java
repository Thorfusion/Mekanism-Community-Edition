package mekanism.api.gas;

import javax.annotation.Nullable;

public interface GasTankInfo {
	
	/**
	 * Retrieve the stored gas stack. DO NOT MODIFY.
	 * 
	 * @return the stored gas, or null
	 */
	@Nullable
	GasStack getGas();

	/**
	 * Gets the amount of gas stored by this GasTank
	 * 
	 * @return amount of gas stored
	 */
	int getStored();

	/**
	 * Gets the maximum amount of gas this tank can hold
	 * 
	 * @return - max gas
	 */
	int getMaxGas();
}
