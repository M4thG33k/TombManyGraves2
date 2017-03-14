package com.m4thg33k.tombmanygraves.client.gui;

import org.lwjgl.input.Mouse;

public class Scrollbar {

    private boolean canScroll = true;

    private int x;
    private int y;
    private int scrollbarWidth;
    private int scrollbarHeight;

    private float scrollDelta = 15;

    private float currentScroll;
    private boolean wasClicking = false;
    private boolean isScrolling = false;

    public Scrollbar(int x, int y, int scrollbarWidth, int scrollbarHeight)
    {
        this.x = x;
        this.y = y;
        this.scrollbarHeight = scrollbarHeight;
        this.scrollbarWidth = scrollbarWidth;
    }

    public boolean getCanScroll()
    {
        return canScroll;
    }

    public void setCanScroll(boolean bool)
    {
        this.canScroll = bool;
    }

    public float getCurrentScroll()
    {
        return currentScroll;
    }

    public void setCurrentScroll(float newCurrentScroll)
    {
        if (newCurrentScroll < 0)
        {
            newCurrentScroll = 0;
        }

        int scrollbarItselfHeight = 12;
        int max = scrollbarHeight - scrollbarItselfHeight - 3;

        if (newCurrentScroll > max)
        {
            newCurrentScroll = max;
        }

        currentScroll = newCurrentScroll;
    }

    public void setScrollDelta(float delta)
    {
        this.scrollDelta = delta;
    }

    public void draw(ModBaseGui gui)
    {
        gui.bindTexture("icons.png");
        gui.drawTexture(gui.getGuiLeft() + x,
                gui.getGuiTop() + y + (int)getCurrentScroll(),
                canScroll ? 0 : 12,
                0,
                12,
                15);
    }

    public void update(ModBaseGui gui, int mouseX, int mouseY)
    {
        if (!canScroll)
        {
            isScrolling = false;
            wasClicking = false;
            currentScroll = 0;
        } else
        {
            int wheel = Mouse.getDWheel();
            wheel = Math.max(Math.min(-wheel, 1), -1);

            if (wheel == -1)
            {
                setCurrentScroll(currentScroll - scrollDelta);
            }
            else if (wheel == 1)
            {
                setCurrentScroll(currentScroll + scrollDelta);
            }

            boolean down = Mouse.isButtonDown(0);

            if (!wasClicking && down && gui.isInBounds(x, y, scrollbarWidth, scrollbarHeight, mouseX, mouseY))
            {
                isScrolling = true;
            }

            if (!down)
            {
                isScrolling = false;
            }

            wasClicking = down;

            if (isScrolling)
            {
                setCurrentScroll(mouseY - gui.getGuiTop());
            }
        }
    }


}
