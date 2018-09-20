package com.m4thg33k.tombmanygraves.client.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.m4thg33k.tombmanygraves.invman.GraveInventoryManager;
import com.m4thg33k.tombmanygraves.invman.InventoryHolder;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;

public class GuiDeathItems extends ModBaseGui {

    private InventoryHolder inventoryHolder;

    private Map<String, Tuple<String, List<String>>> dataMap = null;

    private List<String> header;
    private List<String> EOF;

    private Scrollbar scrollbar;
    private int numLines = 0;

    public static final String BREAK = "-----------------------------";

    public GuiDeathItems(EntityPlayer player, ItemStack deathList) {
        super(200, 150);
        this.inventoryHolder = new InventoryHolder();
        this.inventoryHolder.readFromNBT(deathList.getTagCompound());

        dataMap = this.inventoryHolder.getItemStackStringsForGui();

        createHeader();
        numLines += header.size();

        if (dataMap.size() > 0) {
//            // count blank lines between categories
//            numLines += dataMap.size() - 1;

            AtomicInteger linesWithin = new AtomicInteger();
            // for each entry in our map, we need to add 3 lines (for the inventory title) and the total number of lines
            // given to us in the mapped List
            dataMap.entrySet()
                    .forEach(
                            entry -> linesWithin.addAndGet(3 + entry.getValue().getSecond().size())
                    );

            // add the linesWithin total to the total number of lines
            numLines += linesWithin.get();
        }
        EOF = new ArrayList<>();
        EOF.add(BREAK);
        EOF.add("End Of File");
        EOF.add(BREAK);
        numLines += 3;

        scrollbar = new Scrollbar(xSize - 12, 0, 12, ySize);
        scrollbar.setScrollDelta(1.0f);
        if (numLines <= 13) {
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
        AtomicInteger text_height = new AtomicInteger(drawPortion(header, 0));

        GraveInventoryManager.getInstance()
                .getSortedGuiNames()
                .forEach(
                        name -> {
                            Tuple<String, List<String>> entry = dataMap.get(name);
                            if (entry != null) {
                                text_height.set(
                                        drawInventoryItems(
                                                text_height.get(),
                                                entry.getFirst(),
                                                entry.getSecond(),
                                                GraveInventoryManager.getGuiColorForInventory(name)
                                        )
                                );
                            }
                        }
                );

        drawStringList(text_height.get(), EOF, 0xFF0000);
    }

    private int drawPortion(List<String> lines, int startHeight) {
        return drawPortion(lines, startHeight, 0);
    }

    private int drawPortion(List<String> lines, int startHeight, int draw_color) {
        int height;
        int gLeft = getGuiLeft();
        int gTop = getGuiTop();

        int counter = 0;
        for (int i = 0; i < lines.size(); i++) {
            height = startHeight + 10 * i + (int) scrollbar.getCurrentScroll() * (- 10) + 10;
            counter += 1;
            if (height < 4 || height >= ySize - 12) {
                continue;
            }
            this.fontRenderer.drawString(lines.get(i), gLeft + 12, gTop + height, draw_color);
        }

        return startHeight + counter * 10;
    }

    private int calcHeight(int startHeight, int numLines) {
//        LogHelper.info("Start Height: " + startHeight + "; numLines: " + numLines + "; Current scroll: " + scrollbar.getCurrentScroll());
        return startHeight + 10 * numLines + ((int) scrollbar.getCurrentScroll() * (- 10)) + 10;
    }

    private void drawString(String text, int heightOffset, int color) {
//        LogHelper.info("Drawing at " + heightOffset);
        this.fontRenderer.drawString(text, getGuiLeft() + 12, getGuiTop() + heightOffset, color);
    }

    private void drawBreak(int heightOffest, int color) {
        drawString(BREAK, heightOffest, color);
    }

    private boolean drawStringList(int startHeight, List<String> strings){
        return drawStringList(startHeight, strings, 0);
    }

    private boolean drawStringList(int startHeight, List<String> strings, int color){
        int height;
        for (int i=0; i<strings.size(); i++){
            height = calcHeight(startHeight, i);
            if (height >= ySize - 12){
                return false;
            }
            if (height >= 4){
                drawString(strings.get(i), height, color);
            }
        }

        return true;
    }

    private int drawInventoryItems(int startHeight, String inventoryName, List<String> itemStrings, int color) {
        int height;
        int returnHeight = startHeight + 10 * (3 + itemStrings.size());// calcHeight(startHeight, 2 + itemStrings.size());


        height = calcHeight(startHeight, 0);
        if (height >= ySize - 12) {
            return returnHeight;
        }
        if (height >= 4) {
            drawBreak(height, color);
        }

        height = calcHeight(startHeight, 1);
        if (height >= ySize - 12) {
            return returnHeight;
        }
        if (height >= 4) {
            drawString(inventoryName, height, color);
        }

        height = calcHeight(startHeight, 2);
        if (height >= ySize - 12) {
            return returnHeight;
        }
        if (height >= 4) {
            drawBreak(height, color);
        }

        drawStringList(startHeight + 30, itemStrings);

        return returnHeight;
    }

    private void createHeader() {
        header = new ArrayList<>();

        header.add(inventoryHolder.getPlayerName());

        BlockPos pos = inventoryHolder.getPosition();
        if (pos.getY() < 0) {
            header.add("No grave exists.");
        } else {
            header.add("Grave at: (x,y,z)=(" + pos.getX() + "," + pos.getY() + "," + pos.getZ() + ")");
        }
        header.add("Timestamp: " + inventoryHolder.getTimestamp());
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
