package mekanism.api.gas;

import javax.annotation.Nonnull;

/**
 * Implement this if your tile entity extends IGasHandler.
 * Backported from 1.12.2, separated from IGasHandler to maintain compatibility.
 */
public interface IGasTankInfoProvider
{

	GasTankInfo[] NONE = new GasTankInfo[0];

	/**
     * Gets the tanks present on this handler. READ ONLY. DO NOT MODIFY.
     *
     * @return an array of GasTankInfo elements corresponding to all tanks.
     */
	@Nonnull
	default GasTankInfo[] getTankInfo() {
		return NONE;
	}
}
