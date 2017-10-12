package com.m4thg33k.tombmanygraves.items;

import java.util.List;

import javax.annotation.Nonnull;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

import com.m4thg33k.tombmanygraves.TombManyGraves;
import com.m4thg33k.tombmanygraves.gui.ModGuiHandler;
import com.m4thg33k.tombmanygraves.inventoryManagement.InventoryHolder;
import com.m4thg33k.tombmanygraves.lib.ModConfigs;
import com.m4thg33k.tombmanygraves.lib.Names;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class ItemDeathList extends Item {

    public ItemDeathList()
    {
        super();

        this.setUnlocalizedName(Names.DEATH_LIST);

        this.setMaxStackSize(1);
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
        playerIn.openGui(TombManyGraves.INSTANCE,
                ModGuiHandler.DEATH_ITEMS_GUI,
                worldIn,
                playerIn.getPosition().getX(),
                playerIn.getPosition().getY(),
                playerIn.getPosition().getZ());
        return new ActionResult<>(EnumActionResult.SUCCESS, held);
    }

    @Override
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag advanced) {
        super.addInformation(stack, world, tooltip, advanced);

        boolean isShifted = Keyboard.isKeyDown(Keyboard.KEY_RSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_LSHIFT);
        boolean isControlled = Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL);

        if (isShifted) {
            tooltip.add(TextFormatting.GOLD + "Right-click to view a list of everything");
            tooltip.add(TextFormatting.GOLD + "you had on you when you died.");
            tooltip.add(TextFormatting.RED + "Drop from your inventory to destroy");
        }
        else {
            tooltip.add(TextFormatting.ITALIC + "<Shift for explanation>");
        }

        if (isControlled)
        {
            tooltip.add(TextFormatting.BLUE + "\"/tmg_deathlist [player] latest\"");
            tooltip.add(TextFormatting.BLUE + "will give you a list of everything");
            tooltip.add(TextFormatting.BLUE + "from before your last death");
        }
        else
        {
            tooltip.add(TextFormatting.ITALIC + "<Control for command>");
        }
    }

    private Vector3f getEndVector(ItemStack stack)
    {
        if (stack.isEmpty() || !stack.hasTagCompound())
        {
            return null;
        }
        else {
            NBTTagCompound compound = stack.getTagCompound();

            if (compound.hasKey(InventoryHolder.TAG_NAME)) {
                compound = compound.getCompoundTag(InventoryHolder.TAG_NAME);
                float x = compound.getInteger(InventoryHolder.X) + 0.5f;
                float y = compound.getInteger(InventoryHolder.Y) + 0.5f;
                float z = compound.getInteger(InventoryHolder.Z) + 0.5f;

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

        return Vector3f.sub(start, end, null);
    }

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
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
        Vector3f direction = getDirectionalVector(stack, start);
        if (direction == null || direction.length() == 0)
        {
            return;
        }

        Vector3f normed = direction.normalise(null);
        float length = Math.min(100, direction.length());
        Vector3f end = Vector3f.add(new Vector3f(normed.x * length, normed.y * length, normed.z * length), start, null);

        TombManyGraves.proxy.particleStream(start, end);
    }
}
