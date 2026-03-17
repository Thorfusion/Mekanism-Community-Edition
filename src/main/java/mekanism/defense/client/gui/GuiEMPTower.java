package defense.client.gui;

import java.util.ArrayList;

import mekanism.api.Coord4D;
import mekanism.common.Mekanism;
import mekanism.common.network.PacketTileEntity.TileEntityMessage;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;
import defense.common.Reference;
import defense.common.tile.TileEMPTower;

public class GuiEMPTower extends GuiBase
{
    public static final ResourceLocation TEXTURE = new ResourceLocation(Reference.DOMAIN, Reference.GUI_PATH + "gui_empty.png");

    private TileEMPTower tileEntity;
    private GuiTextField textFieldBanJing;

    private int containerWidth;
    private int containerHeight;

    public GuiEMPTower(TileEMPTower tileEntity)
    {
        super(tileEntity);
        this.tileEntity = tileEntity;
        this.ySize = 166;
    }

    /** Adds the buttons (and other controls) to the screen in question. */
    @Override
    public void initGui()
    {
        super.initGui();
        this.buttonList.clear();

        this.buttonList.add(new GuiButton(0, this.width / 2 - 77, this.height / 2 - 10, 50, 20, LangUtils.localize("gui.empTower.missiles")));
        this.buttonList.add(new GuiButton(1, this.width / 2 - 25, this.height / 2 - 10, 65, 20, LangUtils.localize("gui.empTower.elec")));
        this.buttonList.add(new GuiButton(2, this.width / 2 + 43, this.height / 2 - 10, 35, 20, LangUtils.localize("gui.empTower.both")));

        this.textFieldBanJing = new GuiTextField(fontRendererObj, 72, 28, 30, 12);
        this.textFieldBanJing.setMaxStringLength(3);
        this.textFieldBanJing.setText(this.tileEntity.empRadius + "");
    }

    /** Fired when a control is clicked. This is the equivalent of
     * ActionListener.actionPerformed(ActionEvent e). */
    @Override
    protected void actionPerformed(GuiButton par1GuiButton)
    {
        switch (par1GuiButton.id)
        {
            case 0:
                this.tileEntity.empMode = 1;
                break;
            case 1:
                this.tileEntity.empMode = 2;
                break;
            case 2:
                this.tileEntity.empMode = 0;
                break;
        }

        ArrayList data = new ArrayList();
        
        data.add(2);
        data.add(tileEntity.empMode);
        
        Mekanism.packetHandler.sendToServer(new TileEntityMessage(Coord4D.get(tileEntity), data));
    }

    /** Call this method from you GuiScreen to process the keys into textbox. */
    @Override
    public void keyTyped(char par1, int par2)
    {
        super.keyTyped(par1, par2);
        this.textFieldBanJing.textboxKeyTyped(par1, par2);

        try
        {
            int radius = Math.min(Math.max(Integer.parseInt(this.textFieldBanJing.getText()), 10), TileEMPTower.MAX_RADIUS);
            this.tileEntity.empRadius = radius;
            
            ArrayList data = new ArrayList();
            
            data.add(1);
            data.add(tileEntity.empRadius);
            
            Mekanism.packetHandler.sendToServer(new TileEntityMessage(Coord4D.get(tileEntity), data));
        }
        catch (NumberFormatException e)
        {

        }
    }

    /** Args: x, y, buttonClicked */
    @Override
    public void mouseClicked(int x, int y, int par3)
    {
        super.mouseClicked(x, y, par3);
        this.textFieldBanJing.mouseClicked(x - containerWidth, y - containerHeight, par3);
    }

    /** Draw the foreground layer for the GuiContainer (everything in front of the items) */
    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        this.fontRendererObj.drawString("\u00a77" + LangUtils.localize("gui.empTower.name"), 65, 6, 4210752);

        this.fontRendererObj.drawString(LangUtils.localize("gui.empTower.radius").replaceAll("%p", "        "), 12, 30, 4210752);
        this.textFieldBanJing.drawTextBox();

        this.fontRendererObj.drawString(LangUtils.localize("gui.empTower.effect"), 12, 55, 4210752);

        // Shows the EMP mode of the EMP Tower
        String mode = LangUtils.localize("gui.empTower.effectDebilitate");

        if (this.tileEntity.empMode == 1)
        {
            mode = LangUtils.localize("gui.empTower.effectDisrupt");
        }
        else if (this.tileEntity.empMode == 2)
        {
            mode = LangUtils.localize("gui.empTower.effectDeplete");
        }

        this.fontRendererObj.drawString(LangUtils.localize("gui.empTower.mode") + " " + mode, 12, 105, 4210752);

        // Shows the status of the EMP Tower
        String color = "\u00a74";
        String status = LangUtils.localize("gui.misc.idle");

        if (tileEntity.getEnergy() < tileEntity.energyToUse)
        {
            status = LangUtils.localize("gui.misc.nopower");
        }
        else
        {
            color = "\u00a72";
            status = LangUtils.localize("gui.empTower.ready");
        }

        this.fontRendererObj.drawString(color + LangUtils.localize("gui.misc.status") + " " + status, 12, 120, 4210752);
        //voltage
        this.fontRendererObj.drawString(MekanismUtils.getEnergyDisplay(tileEntity.getEnergy()) + "/" + MekanismUtils.getEnergyDisplay(this.tileEntity.getMaxEnergy()), 12, 150, 4210752);
    }

    /** Draw the background layer for the GuiContainer (everything behind the items) */
    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY)
    {
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(TEXTURE);

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        containerWidth = (this.width - this.xSize) / 2;
        containerHeight = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(containerWidth, containerHeight, 0, 0, this.xSize, this.ySize);
    }

    @Override
    public void updateScreen()
    {
        super.updateScreen();

        if (!this.textFieldBanJing.isFocused())
            this.textFieldBanJing.setText(this.tileEntity.empRadius + "");
    }
}
