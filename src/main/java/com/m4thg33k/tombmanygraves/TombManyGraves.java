package com.m4thg33k.tombmanygraves;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.m4thg33k.tombmanygraves.api.GraveRegistry;
import com.m4thg33k.tombmanygraves.api.IGraveInventory;
import com.m4thg33k.tombmanygraves.commands.ModCommands;
import com.m4thg33k.tombmanygraves.events.CommonEvents;
import com.m4thg33k.tombmanygraves.invman.GraveInventoryManager;
import com.m4thg33k.tombmanygraves.proxy.ClientProxy;
import com.m4thg33k.tombmanygraves.proxy.CommonProxy;
import com.m4thg33k.tombmanygraves.proxy.ServerProxy;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.forgespi.language.ModFileScanData;
import net.minecraftforge.forgespi.language.ModFileScanData.AnnotationData;

@Mod("tombmanygraves")
public class TombManyGraves {
	
	public static final Logger LOGGER = LogManager.getLogger();

	public static TombManyGraves INSTANCE;

	public static final CommonProxy proxy = DistExecutor.runForDist(() -> () -> new ClientProxy(), () -> () -> new ServerProxy());
	
	public TombManyGraves() {
		INSTANCE = this;
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::serverStarting);
		MinecraftForge.EVENT_BUS.register(this);
		MinecraftForge.EVENT_BUS.register(new CommonEvents());
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ModConfigs.build());
	}

	private void setup(final FMLCommonSetupEvent e) {
		ModConfigs.load();
		proxy.setup(e);
		for (ModFileScanData mfsd : ModList.get().getAllScanData()) {
			List<AnnotationData> data = mfsd.getAnnotations();
			for (AnnotationData ad : data) {
				try {
					if (ad.getAnnotationType().getClassName().equals(GraveRegistry.class.getName())) {
						Map<String, Object> annotation = ad.getAnnotationData();
						String reqMod = (String) annotation.get("reqMod");
						if (reqMod == null || ModList.get().isLoaded(reqMod)) {
							GraveInventoryManager.getInstance().registerListener((IGraveInventory) Class.forName(ad.getClassType().getClassName()).newInstance(), annotation);
						}
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}

			}
		}
		GraveInventoryManager.getInstance().finalizeListeners();
	}

	private void clientSetup(final FMLClientSetupEvent e) {
		proxy.setupClient(e);
	}

	@SubscribeEvent
	public void serverStarting(final FMLServerStartingEvent e) {
		ModCommands.initCommands(e);
	}
}
