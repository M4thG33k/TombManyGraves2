package com.m4thg33k.tombmanygraves.api.events;

import com.m4thg33k.tombmanygraves.api.inventory.ISpecialInventory;
import com.m4thg33k.tombmanygraves.inventoryManagement.SpecialInventoryManager;
import net.minecraftforge.fml.common.eventhandler.Event;

public class EventRegisterSpecialInventory extends Event {

    public void registerSpecialInventory(ISpecialInventory specialInventory) throws Exception{
        SpecialInventoryManager.getInstance().registerListener(specialInventory);
    }

}
