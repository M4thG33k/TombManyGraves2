package com.m4thg33k.tombmanygraves.network.packets;

import java.util.ArrayList;
import java.util.List;

import com.m4thg33k.tombmanygraves.client.gui.InventoryFileManagerGui;
import com.m4thg33k.tombmanygraves.util.LogHelper;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;

public class PacketFileNames extends BaseThreadsafePacket {

    private List<String> files = new ArrayList<>();
    private BlockPos pos;

    public PacketFileNames()
    {

    }

    public PacketFileNames(BlockPos pos, List<String> fileNames)
    {
        LogHelper.info("Created PacketFileName with " + fileNames.size() + " file names!");
        this.pos = pos;
        this.files.addAll(fileNames);
    }

    @Override
    public void handleClientSafe(NetHandlerPlayClient netHandler) {
        GuiScreen currentScreen = Minecraft.getMinecraft().currentScreen;
        if (currentScreen instanceof InventoryFileManagerGui)
        {
            LogHelper.info("Received information on the client!");
        }
    }

    @Override
    public void handleServerSafe(NetHandlerPlayServer netHandler) {
        throw new UnsupportedOperationException("Client-side only!");
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        LogHelper.info("reading PFN from buffer");
        pos = readPos(buf);
        int size = buf.readInt();
        files = new ArrayList<>();
        while (size > 0)
        {
            files.add(ByteBufUtils.readUTF8String(buf));
            size--;
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        LogHelper.info("writing PFN to buffer");
        writePos(pos, buf);
        buf.writeInt(files.size());
        for (String file : files)
        {
            ByteBufUtils.writeUTF8String(buf, file);
        }
    }
}
