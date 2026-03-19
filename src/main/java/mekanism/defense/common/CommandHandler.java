package defense.common;

import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import defense.common.entity.EntityExplosion;
import defense.common.entity.EntityFlyingBlock;
import defense.common.entity.EntityMissile;
import defense.common.explosive.blast.BlastEMP;

public class CommandHandler extends CommandBase
{
    @Override
    public String getCommandName()
    {
        return Reference.DOMAIN;
    }

    @Override
    public String getCommandUsage(ICommandSender par1ICommandSender)
    {
        return "/" + Reference.DOMAIN + " help";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args)
    {
        try {
            EntityPlayer entityPlayer = (EntityPlayer)sender;
            int dimension = entityPlayer.worldObj.provider.dimensionId;
            
            if(args == null || args.length == 0 || args[0].equalsIgnoreCase("help"))
            {
                sender.addChatMessage(new ChatComponentText("/" + Reference.DOMAIN + " help"));
                sender.addChatMessage(new ChatComponentText("/" + Reference.DOMAIN + " lag <radius>"));
                sender.addChatMessage(new ChatComponentText("/" + Reference.DOMAIN + " remove <All/Missile/Explosion> <radius>"));
                sender.addChatMessage(new ChatComponentText("/" + Reference.DOMAIN + " emp <radius>"));
                
                return;
            }
            else if(args.length >= 2 && args[0].equalsIgnoreCase("lag"))
            {
                int radius = parseInt(sender, args[1]);

                if(radius > 0)
                {
                    AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox(entityPlayer.posX - radius, entityPlayer.posY - radius, entityPlayer.posZ - radius, entityPlayer.posX + radius, entityPlayer.posY + radius, entityPlayer.posZ + radius);
                    List<Entity> entitiesNearby = entityPlayer.worldObj.getEntitiesWithinAABB(Entity.class, bounds);

                    for(Entity entity : entitiesNearby)
                    {
                        if(entity instanceof EntityFlyingBlock)
                        {
                            ((EntityFlyingBlock) entity).setBlock();
                        }
                        else if(entity instanceof EntityMissile)
                        {
                            entity.setDead();
                        }
                        else if(entity instanceof EntityExplosion)
                        {
                            entity.setDead();
                        }
                    }

                    sender.addChatMessage(new ChatComponentText("Removed all mod-related lag sources within " + radius + " radius."));
                    
                    return;
                }
                else {
                    throw new WrongUsageException("Radius needs to be higher than zero");
                }
            }
            else if(args.length >= 3 && args[0].equalsIgnoreCase("remove"))
            {
                int radius = parseInt(sender, args[2]);
                boolean all = args[1].equalsIgnoreCase("all");
                boolean missile = args[1].equalsIgnoreCase("missiles");
                boolean explosion = args[1].equalsIgnoreCase("explosion");
                String str = "entities";
                
                if(missile)
                {
                    str = "missiles";
                } 
                else if(explosion)
                {
                    str = "explosions";
                }

                if(radius > 0)
                {
                    EntityPlayer player = (EntityPlayer)sender;

                    AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox(player.posX - radius, player.posY - radius, player.posZ - radius, player.posX + radius, player.posY + radius, player.posZ + radius);
                    List<Entity> entitiesNearby = player.worldObj.getEntitiesWithinAABB(Entity.class, bounds);

                    for(Entity entity : entitiesNearby)
                    {
                        if((all || explosion) && entity instanceof EntityFlyingBlock)
                        {
                            ((EntityFlyingBlock) entity).setBlock();
                        }
                        else if((all || missile) && entity instanceof EntityMissile)
                        {
                            entity.setDead();
                        }
                        else if((all || explosion) && entity instanceof EntityExplosion)
                        {
                            entity.setDead();
                        }
                    }

                    sender.addChatMessage(new ChatComponentText("Removed all mod-related " + str + " within " + radius + " radius."));
                   
                    return;
                }
                else {
                    throw new WrongUsageException("Radius needs to be higher than zero");
                }
            }
            else if(args.length >= 2 && args[0].equalsIgnoreCase("emp"))
            {
                int radius = parseInt(sender, args[1]);
                
                if(radius > 0)
                {
                    new BlastEMP(entityPlayer.worldObj, null, entityPlayer.posX, entityPlayer.posY, entityPlayer.posZ, radius).setEffectBlocks().setEffectEntities().doExplode();
                    
                    switch(entityPlayer.worldObj.rand.nextInt(20))
                    {
                        case 0:
                            sender.addChatMessage(new ChatComponentText("Did you pay the power bill?"));
                            return;
                        case 1:
                            sender.addChatMessage(new ChatComponentText("See them power their toys now!"));
                            return;
                        case 2:
                            sender.addChatMessage(new ChatComponentText("Hey who turned the lights out."));
                            return;
                        case 3:
                            sender.addChatMessage(new ChatComponentText("Ha! I run on steam power!"));
                            return;
                        case 4:
                            sender.addChatMessage(new ChatComponentText("The power of lighting at my finger tips!"));
                            return;
                        default:
                            sender.addChatMessage(new ChatComponentText("Zap!"));
                            return;
                    }
                }
                else {
                    throw new WrongUsageException("Radius needs to be higher than zero");
                }
            }
        } catch(Exception e) {}

        throw new WrongUsageException(getCommandUsage(sender));
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 2;
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        return args.length == 1 ? getListOfStringsMatchingLastWord(args, new String[] {"lag"}) : null;
    }

    @Override
    public int compareTo(Object par1Obj)
    {
        return compareTo((ICommand)par1Obj);
    }
}
