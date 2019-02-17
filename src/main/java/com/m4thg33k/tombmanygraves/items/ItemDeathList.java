package com.m4thg33k.tombmanygraves.items;

import java.util.List;

import javax.annotation.Nonnull;
import javax.vecmath.Vector3f;

import org.lwjgl.glfw.GLFW;

import com.m4thg33k.tombmanygraves.ModConfigs;
import com.m4thg33k.tombmanygraves.Names;
import com.m4thg33k.tombmanygraves.TombManyGraves;
import com.m4thg33k.tombmanygraves.client.gui.GuiDeathItems;
import com.m4thg33k.tombmanygraves.invman.InventoryHolder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class ItemDeathList extends Item{

    public ItemDeathList()
    {
        super(new Properties().maxStackSize(1));
        this.setRegistryName(Names.MODID, Names.DEATH_LIST);
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, @Nonnull  EnumHand hand) {
        ItemStack held = playerIn.getHeldItem(hand);
        if (held.getItem() != this || EnumHand.OFF_HAND == hand)
        {
            return new ActionResult<>(EnumActionResult.PASS, held);
        }
        if(worldIn.isRemote){
        	Minecraft.getInstance().displayGuiScreen(new GuiDeathItems(playerIn, held));
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, held);
    }
    
    @Override
    public void addInformation(ItemStack stack, World world, List<ITextComponent> tooltip, ITooltipFlag advanced) {
        super.addInformation(stack, world, tooltip, advanced);
        
        boolean isShifted = InputMappings.isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT) || InputMappings.isKeyDown(GLFW.GLFW_KEY_RIGHT_SHIFT);
        boolean isControlled = InputMappings.isKeyDown(GLFW.GLFW_KEY_LEFT_CONTROL) || InputMappings.isKeyDown(GLFW.GLFW_KEY_RIGHT_CONTROL);

        if (isShifted) {
            tooltip.add(new TextComponentString(TextFormatting.GOLD + "Right-click to view a list of everything"));
            tooltip.add(new TextComponentString(TextFormatting.GOLD + "you had on you when you died."));
            tooltip.add(new TextComponentString(TextFormatting.RED + "Drop from your inventory to destroy"));
        }
        else {
            tooltip.add(new TextComponentString(TextFormatting.ITALIC + "<Shift for explanation>"));
        }

        if (isControlled)
        {
            tooltip.add(new TextComponentString(TextFormatting.BLUE + "\"/tmg_deathlist [player] latest\""));
            tooltip.add(new TextComponentString(TextFormatting.BLUE + "will give you a list of everything"));
            tooltip.add(new TextComponentString(TextFormatting.BLUE + "from before your last death"));
        }
        else
        {
            tooltip.add(new TextComponentString(TextFormatting.ITALIC + "<Control for command>"));
        }
    }

    private Vector3f getEndVector(ItemStack stack)
    {
        if (stack.isEmpty() || !stack.hasTag())
        {
            return null;
        }
        else {
            NBTTagCompound compound = stack.getTag();

            if (compound.contains(InventoryHolder.TAG_NAME)) {
                compound = compound.getCompound(InventoryHolder.TAG_NAME);
                float x = compound.getInt(InventoryHolder.X) + 0.5f;
                float y = compound.getInt(InventoryHolder.Y) + 0.5f;
                float z = compound.getInt(InventoryHolder.Z) + 0.5f;

                if (y < 0)
                {
                    return null;
                }

                return new Vector3f(x, y, z);
            }

            return null;
        }
    }

    private Vector3f getDirectionalVector(ItemStack stack, Vector3f start)
    {
        Vector3f end = getEndVector(stack);
        if (end == null)
        {
            return null;
        }
        start.sub(end);
        return start;
    }
    
    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (!worldIn.isRemote || !(entityIn instanceof EntityPlayer) ||
                (((EntityPlayer) entityIn).getHeldItemMainhand() != stack && ((EntityPlayer) entityIn).getHeldItemOffhand() != stack))
        {
            return;
        }

        if (ModConfigs.REQUIRE_SNEAK_FOR_PATH && !entityIn.isSneaking())
        {
            return;
        }

        Vector3f start = new Vector3f((float)entityIn.posX, (float)entityIn.posY + 2.5f, (float)entityIn.posZ);
        Vector3f direction = getDirectionalVector(stack, new Vector3f(start));
        if (direction == null || direction.length() == 0)
        {
            return;
        }
        Vector3f normed = new Vector3f(direction);
        normed.normalize();
        float length = Math.min(100, direction.length());
        normed.scale(length);
        normed.add(start);
        TombManyGraves.proxy.particleStream(start, normed);
    }
}
