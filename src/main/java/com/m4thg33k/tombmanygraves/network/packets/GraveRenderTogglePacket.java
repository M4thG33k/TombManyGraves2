package com.m4thg33k.tombmanygraves.network.packets;

import com.m4thg33k.tombmanygraves.TombManyGraves;
import com.m4thg33k.tombmanygraves.util.LogHelper;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.NetHandlerPlayServer;

public class GraveRenderTogglePacket extends BaseThreadsafePacket {

    public GraveRenderTogglePacket()
    {

    }

    @Override
    public void handleClientSafe(NetHandlerPlayClient netHandler) {
        TombManyGraves.proxy.toggleGraveRendering();
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
