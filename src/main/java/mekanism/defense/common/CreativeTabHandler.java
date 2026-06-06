package defense.common;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class CreativeTabHandler extends CreativeTabs
{
    public static final CreativeTabHandler INSTANCE = new CreativeTabHandler();

    public static ItemStack itemStack;

    public CreativeTabHandler()
    {
        super(CreativeTabs.getNextID(), Reference.NAME);
    }

    @Override
    public ItemStack getIconItemStack()
    {
        if(itemStack == null)
        {
            itemStack = new ItemStack(Blocks.tnt);
        }

        return itemStack;
    }

	@Override
	public Item getTabIconItem() 
	{
		return getIconItemStack().getItem();
	}
}
