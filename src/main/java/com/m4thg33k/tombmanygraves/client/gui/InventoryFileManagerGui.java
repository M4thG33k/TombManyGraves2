package com.m4thg33k.tombmanygraves.client.gui;

import java.util.ArrayList;
import java.util.List;

import com.m4thg33k.tombmanygraves.gui.containers.ContainerInventoryFileManager;
import com.m4thg33k.tombmanygraves.network.TMGNetwork;
import com.m4thg33k.tombmanygraves.network.packets.PacketProbeFiles;
import com.m4thg33k.tombmanygraves.util.LogHelper;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;

public class InventoryFileManagerGui extends ModBaseContainerGui {

    protected EntityPlayer player;
    protected GuiButton testButton;
    protected ContainerInventoryFileManager container;
    protected List<String> files;

    public InventoryFileManagerGui(EntityPlayer player, ContainerInventoryFileManager container)
    {
        super(200, 150, container);
        this.player = player;
        this.container = container;
//        files = container.getFileNames();
        files = new ArrayList<>();

        LogHelper.info("Sending packet to server");
        TMGNetwork.sendToServer(new PacketProbeFiles(player.dimension, player.getUniqueID(), player.getPosition()));

        this.buttonList.add(new GuiButton(0, getGuiLeft()+1, getGuiTop()+1, "Button"));
    }

    @Override
    public void initGui() {
        super.initGui();

        this.buttonList.add(this.testButton = new GuiButton(0, this.width / 2 - 100, this.height / 2 -24, 10,10,"Button"));
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        //super.drawScreen(mouseX, mouseY, partialTicks);
        bindTexture("deathlistbackground.png");
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        drawTexture(getGuiLeft(), getGuiTop(),0,0,xSize,ySize);

        if (isInBounds(0, 0, this.xSize, this.ySize, mouseX, mouseY))
        {
            List<String> lines = new ArrayList<>();
            lines.add("Testing!");
            lines.addAll(files);
            this.drawHoveringText(lines, mouseX, mouseY);
        }


        for (GuiButton button : this.buttonList)
        {
            button.visible = true;
            button.drawButton(mc, mouseX, mouseY, partialTicks);
        }
    }
}
