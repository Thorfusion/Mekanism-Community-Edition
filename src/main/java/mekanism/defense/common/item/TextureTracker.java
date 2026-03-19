package defense.common.item;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import defense.api.IItemTracker;
import defense.common.Reference;

@SideOnly(Side.CLIENT)
public class TextureTracker extends TextureAtlasSprite
{
    public double currentAngle;
    public double angleDelta;

    public TextureTracker()
    {
        super(Reference.PREFIX + "tracker");
    }

    @Override
    public void updateAnimation()
    {
        Minecraft minecraft = Minecraft.getMinecraft();
        World world = minecraft.theWorld;
        EntityPlayer player = minecraft.thePlayer;

        double angel = 0;

        if(world != null)
        {
            double xDifference = 0;
            double zDifference = 0;

            ItemStack itemStack = player.getCurrentEquippedItem();

            if(itemStack != null)
            {
                if(itemStack.getItem() instanceof IItemTracker)
                {
                    Entity trackingEntity = ((IItemTracker) itemStack.getItem()).getTrackingEntity(FMLClientHandler.instance().getClient().theWorld, itemStack);

                    if(trackingEntity != null)
                    {
                        xDifference = trackingEntity.posX - player.posX;
                        zDifference = trackingEntity.posZ - player.posZ;
                    }
                }
            }

            player.rotationYaw %= 360.0D;
            angel = -((player.rotationYaw - 90.0D) * Math.PI / 180.0D - Math.atan2(zDifference, xDifference));
        }

        double d6;

        for(d6 = angel - currentAngle; d6 < -Math.PI; d6 += (Math.PI * 2D)) {}

        while(d6 >= Math.PI)
        {
            d6 -= (Math.PI * 2D);
        }

        if(d6 < -1.0D)
        {
            d6 = -1.0D;
        }

        if(d6 > 1.0D)
        {
            d6 = 1.0D;
        }

        angleDelta += d6 * 0.1D;
        angleDelta *= 0.8D;
        currentAngle += angleDelta;

        int i;

        for(i = (int)((currentAngle / (Math.PI * 2D) + 1.0D) * framesTextureData.size()) % framesTextureData.size(); i < 0; i = (i + framesTextureData.size()) % framesTextureData.size()) {}

        if(i != frameCounter)
        {
            frameCounter = i;
            TextureUtil.uploadTextureMipmap((int[][]) this.framesTextureData.get(frameCounter), width, height, originX, originY, false, false);
        }
    }
}
