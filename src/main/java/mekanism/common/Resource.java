package mekanism.common;

import mekanism.api.MekanismConfig;

import java.util.Objects;

public enum Resource
{
	IRON("Iron"),
	GOLD("Gold"),
	OSMIUM("Osmium"),
	COPPER("Copper"),
	TIN("Tin"),
	SILVER("Silver"),
	LEAD("Lead");

	private String name;

	private Resource(String s)
	{
		name = s;
	}

	/**
	 * Returns resource from a String.
	 * Used to convert Oregas into their physical output
	 *
	 */
	public static Resource getFromName(String s)
	{
		for(Resource r : values())
		{
			String resourceName = r.getOredictName().toLowerCase();
			if(resourceName.equals(s.toLowerCase()))
			{
				return r;
			}
		}

		return null;
	}

	/**
	 * Returns the name of a resource.
	 *
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Returns the oredictionary name of a resource.
	 *
	 */
	public String getOredictName() {
		String oreName = name;
		if (MekanismConfig.mekce.PlatReplacement && Objects.equals(oreName, "Osmium"))
			oreName = "Platinum";

		return oreName;
	}
}
