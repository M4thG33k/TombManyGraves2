/*package com.m4thg33k.tombmanygraves.inventoryManagement.specialCases;

import com.m4thg33k.tombmanygraves.util.LogHelper;
import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.api.ICyberwareUserData;
import flaxbeard.cyberware.common.CyberwareContent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class CyberwareHandler {

    public static boolean willCyberHandleDeath(EntityPlayer player)
    {
        ItemStack test = new ItemStack(CyberwareContent.heartUpgrades, 1, 0);
        if (CyberwareAPI.isCyberwareInstalled(player, test))
        {
            LogHelper.info("Heart upgrade is installed");
            ICyberwareUserData cyberware = CyberwareAPI.getCapability(player);
            ItemStack stack = CyberwareAPI.getCyberware(player, test);
            if (!CyberwareAPI.getCyberwareNBT(stack).hasKey("used") &&
                    cyberware.getStoredPower() >= CyberwareContent.heartUpgrades.getPowerConsumption(test))
            {
                LogHelper.info("It hasn't been used and we have the power!");
                return true;
            }
            else
            {
                LogHelper.info("Used: " + CyberwareAPI.getCyberwareNBT(stack).hasKey("used"));
                LogHelper.info("Enough power: " + (cyberware.getStoredPower() >= CyberwareContent.heartUpgrades.getPowerConsumption(test)));
            }
        }
        else
        {
            LogHelper.info("Heart upgrade is NOT installed!");
        }
        return false;
    }
}
*/