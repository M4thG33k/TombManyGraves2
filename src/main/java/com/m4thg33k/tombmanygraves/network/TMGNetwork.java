package com.m4thg33k.tombmanygraves.network;

import java.util.function.Predicate;

import com.m4thg33k.tombmanygraves.Names;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;


public class TMGNetwork {

    public static TMGNetwork instance = new TMGNetwork();

    public final SimpleChannel network;
    private int id = 0;

    public TMGNetwork(){
    	String version = "1.0.0";
    	Predicate<String> pred = (ver) -> {return ver.equals(version);};
        network = NetworkRegistry.newSimpleChannel(new ResourceLocation(Names.MODID, "main"), () -> {return version;}, pred, pred);
    }

    public static void setup()
    {
        //register packets here
    	instance.network.<GraveRenderTogglePacket>registerMessage(instance.id++, GraveRenderTogglePacket.class, null, null, (a, b) -> a.execute());
    	instance.network.<GravePosTogglePacket>registerMessage(instance.id++, GravePosTogglePacket.class, null, null, (a, b) -> a.execute());
    }

    public static <MSG> void sendTo(MSG packet, EntityPlayerMP player)
    {
        instance.network.sendTo(packet, player.connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
    }

    public static <MSG> void sendToServer(MSG packet)
    {
        instance.network.sendToServer(packet);
    }

    public static <MSG> void sendToClients(WorldServer world, BlockPos pos, MSG packet)
    {
        Chunk chunk = world.getChunk(pos);
        for (EntityPlayer player : world.playerEntities)
        {
            if (!(player instanceof EntityPlayerMP))
            {
                continue;
            }
            EntityPlayerMP playerMP = (EntityPlayerMP) player;
            if (world.getPlayerChunkMap().isPlayerWatchingChunk(playerMP, chunk.x, chunk.z))
            {
//                LogHelper.info("Sending packet to: " + player.getName());
                TMGNetwork.sendTo(packet, playerMP);
            }
        }
    }
}
