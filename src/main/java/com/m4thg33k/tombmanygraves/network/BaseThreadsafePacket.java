package com.m4thg33k.tombmanygraves.network;

import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public abstract class BaseThreadsafePacket extends BasePacket {

    @Override
    public IMessage handleClient(NetHandlerPlayClient netHandler) {
        FMLCommonHandler.instance().getWorldThread(netHandler).addScheduledTask(new Runnable() {
            @Override
            public void run() {
                handleClientSafe(netHandler);
            }
        });
        return null;
    }

    @Override
    public IMessage handleServer(NetHandlerPlayServer netHandler) {
        FMLCommonHandler.instance().getWorldThread(netHandler).addScheduledTask(new Runnable() {
            @Override
            public void run() {
                handleServerSafe(netHandler);
            }
        });
        return null;
    }

    public abstract void handleClientSafe(NetHandlerPlayClient netHandler);

    public abstract void handleServerSafe(NetHandlerPlayServer netHandler);
}
