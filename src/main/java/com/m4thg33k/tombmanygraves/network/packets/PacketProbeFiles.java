package com.m4thg33k.tombmanygraves.network.packets;

import java.util.List;
import java.util.UUID;

import com.m4thg33k.tombmanygraves.TombManyGraves;
import com.m4thg33k.tombmanygraves.network.TMGNetwork;
import com.m4thg33k.tombmanygraves.util.LogHelper;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.network.ByteBufUtils;

public class PacketProbeFiles extends BaseThreadsafePacket{

    private BlockPos pos;
    private int dimension;
    private UUID uuid;

    public PacketProbeFiles()
    {

    }

    public PacketProbeFiles(int dimension, UUID playerUUID, BlockPos pos)
    {
        this.dimension = dimension;
        this.pos = pos;
        this.uuid = playerUUID;
    }

    @Override
    public void handleClientSafe(NetHandlerPlayClient netHandler) {
        throw new UnsupportedOperationException("Server-side only!");
    }

    @Override
    public void handleServerSafe(NetHandlerPlayServer netHandler) {
        List<String> files = TombManyGraves.proxy.probeForFiles(pos);
        LogHelper.info("I've received the files in PacketProbeFiles");
        WorldServer server = DimensionManager.getWorld(dimension);
        TMGNetwork.sendTo(new PacketFileNames(pos, files), (EntityPlayerMP)server.getPlayerEntityByUUID(uuid));
    }

    @Override
    public void toBytes(ByteBuf buf) {
        writePos(pos, buf);
        buf.writeInt(dimension);
        ByteBufUtils.writeUTF8String(buf, uuid.toString());
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        pos = readPos(buf);
        dimension = buf.readInt();
        uuid = UUID.fromString(ByteBufUtils.readUTF8String(buf));
    }
}
