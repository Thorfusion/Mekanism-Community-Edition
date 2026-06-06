package defense.client;

import mekanism.api.EnumColor;
import mekanism.api.MekanismConfig.general;
import mekanism.common.Version;
import mekanism.common.util.LangUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import defense.common.DefenseTech;
import defense.common.Reference;

public class ClientTickHandler 
{
	public static Minecraft mc = FMLClientHandler.instance().getClient();
	
	public boolean hasNotified = false;
	
	@SubscribeEvent
	public void onTick(ClientTickEvent event)
	{
		if(event.phase == Phase.START)
		{
			if(!hasNotified && mc.theWorld != null && DefenseTech.latestVersionNumber != null && DefenseTech.recentNews != null)
			{
				try {
					if(general.updateNotifications)
					{
						if(!DefenseTech.latestVersionNumber.equals("null"))
						{
							if(Version.get(DefenseTech.latestVersionNumber).comparedState(DefenseTech.versionNumber) == 1)
							{
								mc.thePlayer.addChatMessage(new ChatComponentText(EnumColor.GREY + "------------- " + Reference.CHAT_DESC + EnumColor.GREY + " -------------"));
								mc.thePlayer.addChatMessage(new ChatComponentText(EnumColor.GREY + " " + LangUtils.localize("update.outdated") + "."));
		
								if(Version.get(DefenseTech.latestVersionNumber).comparedState(DefenseTech.versionNumber) == 1)
								{
									mc.thePlayer.addChatMessage(new ChatComponentText(EnumColor.INDIGO + " " + Reference.NAME + ": " + EnumColor.DARK_RED + DefenseTech.versionNumber));
								}
		
								mc.thePlayer.addChatMessage(new ChatComponentText(EnumColor.GREY + " " + LangUtils.localize("update.consider") + " " + EnumColor.DARK_GREY + DefenseTech.latestVersionNumber));
								mc.thePlayer.addChatMessage(new ChatComponentText(EnumColor.GREY + " " + LangUtils.localize("update.newFeatures") + ": " + EnumColor.INDIGO + DefenseTech.recentNews));
								mc.thePlayer.addChatMessage(new ChatComponentText(EnumColor.GREY + " " + LangUtils.localize("update.visit") + " " + EnumColor.DARK_GREY + "aidancbrady.com/defensetech" + EnumColor.GREY + " " + LangUtils.localize("update.toDownload") + "."));
								mc.thePlayer.addChatMessage(new ChatComponentText(EnumColor.GREY + "------------- " + EnumColor.DARK_BLUE + "[=======]" + EnumColor.GREY + " -------------"));
							}
							else if(Version.get(DefenseTech.latestVersionNumber).comparedState(DefenseTech.versionNumber) == -1)
							{
								mc.thePlayer.addChatMessage(new ChatComponentText(Reference.CHAT_DESC + " " + EnumColor.GREY + LangUtils.localize("update.devBuild") + " " + EnumColor.DARK_GREY + DefenseTech.versionNumber));
							}
						}
						else {
							DefenseTech.LOGGER.info("Minecraft is in offline mode, could not check for updates.");
						}
					}
				} catch(Exception e) {}
				
				hasNotified = true;
			}
		}
	}
}
