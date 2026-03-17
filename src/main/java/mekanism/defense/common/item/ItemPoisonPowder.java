package defense.common.item;

import defense.common.Reference;
import net.minecraft.client.renderer.texture.IIconRegister;

public class ItemPoisonPowder extends ItemBase
{
    public ItemPoisonPowder()
    {
        super("poisonPowder");
    }

    @Override
    public void registerIcons(IIconRegister iconRegister)
    {
        super.registerIcons(iconRegister);
        
        // Icon for base item.
        this.itemIcon = iconRegister.registerIcon(Reference.PREFIX + "poisonPowder");
    }
}
