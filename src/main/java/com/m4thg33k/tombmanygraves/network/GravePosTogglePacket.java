package com.m4thg33k.tombmanygraves.network;

import com.m4thg33k.tombmanygraves.TombManyGraves;
import com.m4thg33k.tombmanygraves.util.LogHelper;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.NetHandlerPlayServer;

public class GravePosTogglePacket extends BaseThreadsafePacket{

    public GravePosTogglePacket()
    {

    }

    @Override
    public void handleClientSafe(NetHandlerPlayClient netHandler) {
        TombManyGraves.proxy.toggleGravePositionRendering();
    }

    @Override
    public void handleServerSafe(NetHandlerPlayServer netHandler) {

        LogHelper.error("Attempting to handle rendering packet on server!");
    }

    @Override
    public void fromBytes(ByteBuf buf) {

    }

    @Override
    public void toBytes(ByteBuf buf) {

    }
}
