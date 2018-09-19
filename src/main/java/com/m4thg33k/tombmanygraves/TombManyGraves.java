package com.m4thg33k.tombmanygraves;

import java.util.Map;
import java.util.Set;

import com.m4thg33k.tombmanygraves.commands.ModCommands;
import com.m4thg33k.tombmanygraves.inventoryManagement.SpecialInventoryManager;
import com.m4thg33k.tombmanygraves.lib.Names;
import com.m4thg33k.tombmanygraves.proxy.CommonProxy;
import com.m4thg33k.tombmanygraves2api.api.IGraveInventory;
import com.m4thg33k.tombmanygraves2api.api.GraveRegistry;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.discovery.ASMDataTable.ASMData;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

@Mod(modid = Names.MODID, name = Names.MODNAME, version = Names.VERSION, dependencies = TombManyGraves.DEPENDENCIES)
public class TombManyGraves {

	public static final String DEPENDENCIES = "required-after:forge@[14.23.3.2655,)";
	private static Set<ASMData> moduleASM;

	@Mod.Instance
	public static TombManyGraves INSTANCE = new TombManyGraves();

	@SidedProxy(clientSide = "com.m4thg33k.tombmanygraves.proxy.ClientProxy", serverSide = "com.m4thg33k.tombmanygraves.proxy.ServerProxy")
	public static CommonProxy proxy;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent e) {
		proxy.preinit(e);
		moduleASM = e.getAsmData().getAll(GraveRegistry.class.getName());
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent e) {
		proxy.init(e);
		for (ASMData data : moduleASM) {
			try {
				Class<?> c = Class.forName(data.getClassName());
				if (IGraveInventory.class.isAssignableFrom(c)) {
					Map<String, Object> annotation = data.getAnnotationInfo();
					String reqMod = (String) annotation.get("reqMod");
					if (reqMod == null){
					//if (reqMod == null || Loader.isModLoaded(reqMod)){
						SpecialInventoryManager.getInstance().registerListener((IGraveInventory) c.newInstance(), annotation);
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	@Mod.EventHandler
	public void postinit(FMLPostInitializationEvent e) {
		proxy.postinit(e);

		// Make sure to finalize the listeners so the mod actually works...
		SpecialInventoryManager.getInstance().finalizeListeners();
	}

	@Mod.EventHandler
	public void serverLoad(FMLServerStartingEvent event) {
		ModCommands.initCommands(event);
	}
}
