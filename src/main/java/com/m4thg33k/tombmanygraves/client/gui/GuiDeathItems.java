package com.m4thg33k.tombmanygraves.client.gui;

import java.util.ArrayList;
import java.util.List;

import com.m4thg33k.tombmanygraves.inventoryManagement.InventoryHolder;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public class GuiDeathItems extends ModBaseGui{

    private EntityPlayer player;

    private ItemStack deathList;
    private InventoryHolder inventoryHolder;

    private List<String> header;
    private List<String> playerItems;
    private List<String> baubleItems;
    private List<String> wearableBackpackItems;
    private List<String> cosmeticArmorItems;
    private List<String> EOF;

    private Scrollbar scrollbar;
    private int numLines = 0;

    public static final String BREAK = "------------------------------";

    public GuiDeathItems(EntityPlayer player, ItemStack deathList)
    {
        super(200, 150);
        this.player = player;
        this.deathList = deathList.copy();

        this.inventoryHolder = new InventoryHolder();
        this.inventoryHolder.readFromNBT(deathList.getTagCompound());

        createHeader();
        numLines += header.size();
        this.playerItems = this.inventoryHolder.createListOfItemsInMainInventory();
        numLines += playerItems.size();
        this.baubleItems = this.inventoryHolder.getListOfBaubles();
        numLines += baubleItems.size();
        this.wearableBackpackItems = this.inventoryHolder.getListOfItemsInWearableBackpack();
        numLines += wearableBackpackItems.size();
        this.cosmeticArmorItems = this.inventoryHolder.getListOfItemsInCosmeticArmor();
        numLines += cosmeticArmorItems.size();

        EOF = new ArrayList<>();
        EOF.add(BREAK);
        EOF.add("End Of File");
        EOF.add(BREAK);
        numLines += 1;

        scrollbar = new Scrollbar(xSize - 12, 0, 12, ySize);
        scrollbar.setScrollDelta(1f);
        if (numLines <= 13)
        {
            scrollbar.setCanScroll(false);
        }
    }



    @Override
    public void drawBackground(int tint) {
        bindTexture("deathlistbackground.png");
        drawTexture(getGuiLeft(), getGuiTop(), 0, 0, xSize, ySize);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawBackground(0);
        scrollbar.update(this, mouseX, mouseY);
        scrollbar.draw(this);
//
        int text_height = drawPortion(header, 0);
        text_height = drawPortion(playerItems, text_height);
        text_height = drawPortion(baubleItems, text_height, 0x5E8FFF);
        text_height = drawPortion(wearableBackpackItems, text_height, 0x87703A);
        text_height = drawPortion(cosmeticArmorItems, text_height, 0x228800);

        drawPortion(EOF, text_height, 0xFF0000);
    }

    private int drawPortion(List<String> lines, int startHeight)
    {
        return drawPortion(lines, startHeight, 0);
    }

    private int drawPortion(List<String> lines, int startHeight, int draw_color)
    {
        int height;
        int gLeft = getGuiLeft();
        int gTop = getGuiTop();

        int counter = 0;
        for (int i=0; i < lines.size(); i++)
        {
            height = startHeight + 10 * i + (int)scrollbar.getCurrentScroll()*(-10) + 10;
            counter += 1;
            if (height < 4 || height >= ySize - 12)
            {
                continue;
            }
            this.fontRenderer.drawString(lines.get(i), gLeft + 12, gTop + height, draw_color);
        }

        return startHeight + counter * 10;
    }

    private void createHeader()
    {
        header = new ArrayList<>();

        header.add(inventoryHolder.getPlayerName());

        BlockPos pos = inventoryHolder.getPosition();
        if (pos.getY() < 0)
        {
            header.add("No grave exists.");
        }
        else
        {
            header.add("Grave at: (x,y,z)=(" + pos.getX() + "," + pos.getY() + "," + pos.getZ() + ")");
        }
        header.add("Timestamp: " + inventoryHolder.getTimestamp());
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
