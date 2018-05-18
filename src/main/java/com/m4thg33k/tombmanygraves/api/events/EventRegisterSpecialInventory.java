package com.m4thg33k.tombmanygraves.api.events;

import com.m4thg33k.tombmanygraves.api.inventory.ISpecialInventory;
import com.m4thg33k.tombmanygraves.inventoryManagement.SpecialInventoryManager;
import net.minecraftforge.fml.common.eventhandler.Event;

import javax.annotation.ParametersAreNonnullByDefault;

public class EventRegisterSpecialInventory extends Event {

    @ParametersAreNonnullByDefault
    public void registerSpecialInventory(ISpecialInventory specialInventory) throws Exception{
        SpecialInventoryManager.getInstance().registerListener(specialInventory);
    }

}
