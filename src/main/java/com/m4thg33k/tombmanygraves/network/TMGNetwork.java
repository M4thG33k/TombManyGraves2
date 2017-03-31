package com.m4thg33k.tombmanygraves.network;

import com.m4thg33k.tombmanygraves.lib.Names;
import com.m4thg33k.tombmanygraves.network.packets.*;
import com.m4thg33k.tombmanygraves.util.LogHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class TMGNetwork {

    public static TMGNetwork instance = new TMGNetwork();

    public final SimpleNetworkWrapper network;
    protected final BasePacketHandler handler;
    private int id = 0;

    public TMGNetwork(){
        network = NetworkRegistry.INSTANCE.newSimpleChannel(Names.MODID);
        handler = new BasePacketHandler();
    }

    public void registerPacket(Class<? extends BasePacket> packetClass)
    {
        registerPacketClient(packetClass);
        registerPacketServer(packetClass);
    }

    public static void registerPacketClient(Class<? extends BasePacket> packetClass)
    {
        registerPacketImp(packetClass, Side.CLIENT);
    }

    public static void registerPacketServer(Class<? extends BasePacket> packetClass)
    {
        registerPacketImp(packetClass, Side.SERVER);
    }

    public static void registerPacketImp(Class<? extends BasePacket> packetClass, Side side)
    {
        instance.network.registerMessage(instance.handler, packetClass, instance.id++, side);
    }

    public static void setup()
    {
        //register packets here
        registerPacketServer(PacketProbeFiles.class);
        registerPacketClient(GraveRenderTogglePacket.class);
        registerPacketClient(GravePosTogglePacket.class);
    }

    public static void sendToAll(BasePacket packet)
    {
        instance.network.sendToAll(packet);
    }

    public static void sendTo(BasePacket packet, EntityPlayerMP player)
    {
        instance.network.sendTo(packet, player);
    }

    public static void sendToAllAround(BasePacket packet, NetworkRegistry.TargetPoint point)
    {
        instance.network.sendToAllAround(packet, point);
    }

    public static void sendToDimension(BasePacket packet, int dimensionID)
    {
        instance.network.sendToDimension(packet, dimensionID);
    }

    public static void sendToServer(BasePacket packet)
    {
        instance.network.sendToServer(packet);
    }

    public static void sendToClients(WorldServer world, BlockPos pos, BasePacket packet)
    {
        Chunk chunk = world.getChunkFromBlockCoords(pos);
        for (EntityPlayer player : world.playerEntities)
        {
            if (!(player instanceof EntityPlayerMP))
            {
                continue;
            }
            EntityPlayerMP playerMP = (EntityPlayerMP) player;
            if (world.getPlayerChunkMap().isPlayerWatchingChunk(playerMP, chunk.xPosition, chunk.zPosition))
            {
                LogHelper.info("Sending packet to: " + player.getName());
                TMGNetwork.sendTo(packet, playerMP);
            }
        }
    }
}
