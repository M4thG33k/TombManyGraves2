package com.m4thg33k.tombmanygraves.proxy;

import javax.vecmath.Vector3f;

import com.m4thg33k.tombmanygraves.ModConfigs;
import com.m4thg33k.tombmanygraves.friends.FriendHandler;
import com.m4thg33k.tombmanygraves.network.TMGNetwork;

import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class CommonProxy {

	public void setupClient(FMLClientSetupEvent e) {
	}

	public void setup(FMLCommonSetupEvent e) {
		FriendHandler.importFriendsList();
		TMGNetwork.setup();
	}

	public void particleStream(Vector3f start, Vector3f end) {

	}

	public void pathFX(double x, double y, double z, float r, float g, float b, float size, float motionX, float motionY, float motionZ, float maxAge) {

	}

	public void toggleGraveRendering() {
	}

	public void toggleGravePositionRendering() {

	}
}
