/*package com.m4thg33k.tombmanygraves.inventoryManagement.specialCases;

import com.inventorypets.InventoryPets;
import com.inventorypets.capabilities.CapabilityRefs;
import com.inventorypets.capabilities.ICapabilityPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class InventoryPetsHandler {

    public static boolean isGravePetActive(EntityPlayer player)
    {
        for (int i=0; i < 10; i++)
        {
            ItemStack stack = player.inventory.getStackInSlot(i);
            if (! stack.isEmpty() && stack.getItem() == InventoryPets.petGrave && stack.getItemDamage() == 0 )
            {
                return true;
            }
        }
        return false;
    }

    public static void resetGravePet(EntityPlayer player)
    {
        ICapabilityPlayer props;
        props = CapabilityRefs.getPlayerCaps(player);
        props.setRestoreItems(false);
    }
}
*/