package defense.common;

import java.util.List;

import mekanism.api.Pos3D;
import mekanism.common.inventory.container.ContainerNull;
import mekanism.common.tile.TileEntityContainerBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import defense.api.IItemFrequency;
import defense.common.entity.EntityMissile;
import defense.common.explosive.Explosive;
import defense.common.explosive.ExplosiveRegistry;
import defense.common.inventory.container.ContainerCruiseLauncher;
import defense.common.tile.TileCruiseLauncher;
import defense.common.tile.TileEMPTower;
import defense.common.tile.TileExplosive;
import defense.common.tile.TileLauncherBase;
import defense.common.tile.TileLauncherFrame;
import defense.common.tile.TileLauncherScreen;
import defense.common.tile.TileRadarStation;

public class CommonProxy implements IGuiHandler
{
    public void preInit() {}

    public void init()
    {
    	GameRegistry.registerTileEntity(TileCruiseLauncher.class, "CruiseLauncher");
        GameRegistry.registerTileEntity(TileLauncherBase.class, "LauncherBase");
        GameRegistry.registerTileEntity(TileLauncherScreen.class, "LauncherScreen");
        GameRegistry.registerTileEntity(TileLauncherFrame.class, "LauncherFrame");
        GameRegistry.registerTileEntity(TileRadarStation.class, "RadarStation");
        GameRegistry.registerTileEntity(TileEMPTower.class, "EMPTower");
        GameRegistry.registerTileEntity(TileExplosive.class, "Explosive");
    }
    
    public void loadConfiguration()
    {
    	Settings.initiate();
    	
    	for(Explosive explosive : ExplosiveRegistry.getExplosives())
    	{
    		explosive.isDisabled = !Settings.CONFIGURATION.get("explosives", explosive.getUnlocalizedName(), true).getBoolean(true);
    	}
    	
    	if(Settings.CONFIGURATION.hasChanged())
		{
			Settings.CONFIGURATION.save();
		}
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        return null;
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        
        if(player.inventory.getCurrentItem() != null && player.inventory.getCurrentItem().getItem() instanceof IItemFrequency)
        {
            return new ContainerNull(player, null);
        }
        else if(tileEntity instanceof TileCruiseLauncher)
        {
            return new ContainerCruiseLauncher(player.inventory, (TileCruiseLauncher)tileEntity);
        }
        else if(tileEntity instanceof TileLauncherScreen || tileEntity instanceof TileRadarStation || tileEntity instanceof TileEMPTower || tileEntity instanceof TileLauncherBase)
        {
            return new ContainerNull(player, (TileEntityContainerBlock)tileEntity);
        }

        return null;
    }
    
    public boolean isFancyGraphicsEnabled()
    {
        return false;
    }

    public void spawnParticle(String name, World world, Pos3D position, float scale, double distance)
    {
        spawnParticle(name, world, position, 0, 0, 0, scale, distance);
    }

    public void spawnParticle(String name, World world, Pos3D position, double motionX, double motionY, double motionZ, float scale, double distance)
    {
        spawnParticle(name, world, position, motionX, motionY, motionZ, 1, 1, 1, scale, distance);
    }

    public void spawnParticle(String name, World world, Pos3D position, double motionX, double motionY, double motionZ, float red, float green, float blue, float scale, double distance) {}
    
    public void playSound(EntityMissile missile) {}

    public int getParticleSetting()
    {
        return -1;
    }

    public List<Entity> getEntityFXs()
    {
        return null;
    }

    public void spawnShock(World world, Pos3D position, Pos3D target) {}

    public void spawnShock(World world, Pos3D startVec, Pos3D targetVec, int duration) {}
}
