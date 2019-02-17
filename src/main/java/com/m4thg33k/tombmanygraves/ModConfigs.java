package com.m4thg33k.tombmanygraves;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.HashMap;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;

public class ModConfigs {

	// Client configs
	@TMGConfig(path = "client.forceDirtRender", comment = "If true, all graves will be rendered with a dirt base instead of using the adaptive model. (Defaults to false).")
	public static boolean FORCE_DIRT_RENDER = false;
	@TMGConfig(path = "client.displayGraveName", comment = "When true, this will display both the name of the grave's owner and the current mode of the grave when looking at it. (Defaults to true)")
	public static boolean DISPLAY_GRAVE_NAME = true;
	@TMGConfig(path = "client.allowParticlePath", comment = "If set to false, the death inventory lists will not show particle effects leading to the grave. (Defaults to true)")
	public static boolean ALLOW_PARTICLE_PATH = true;
	@TMGConfig(path = "client.requireSneakForPath", comment = "If set to true, the death inventory lists will only show their particle trail if you are sneaking (and the path is turned on). (Defaults to false)")
	public static boolean REQUIRE_SNEAK_FOR_PATH = false;
	@TMGConfig(path = "client.enableGraveRendering", comment = "If set to false, graves will not be rendered (at all), meaning there will just be an invisible block there. This is useful if a rendering crash is consistently occuring and I haven't had a chance to post a fix yet. (Defaults to true)")
	public static boolean GRAVE_RENDERING_ENABLED = true;
	@TMGConfig(path = "client.enableGravePositionRender", comment = "If set to true, graves will display their block position when looking at them (useful for admins/mods for debugging purposes.")
	public static boolean GRAVE_POS_ENABLED = false;

	// Grave configs
	@TMGConfig(path = "grave.enableGraves", comment = "If set to false, graves will no longer be spawned upon players deaths. This does not get rid of any graves currently in-world. (Defaults to true...obviously.)")
	public static boolean ENABLE_GRAVES = true;
	@TMGConfig(path = "grave.dropItemsOnGround", comment = "If set to true, instead of attempting to place all items back in the player's original slots, the items will instead be placed on the ground like every other grave mod. (Defaults to false.)")
	public static boolean DROP_ITEMS_ON_GROUND = false;
	@TMGConfig(path = "grave.giveItemsInGravePriority", comment = "If true and an item in the grave was originally in a slot that is now not empty, the grave will remove the current item, place the item from the grave in that slot, and then drop the original item on the ground. If false, any item from the grave attempting to fill a non-empty slot will instead be dropped on the ground. (Defaults to true.)")
	public static boolean GIVE_ITEMS_IN_GRAVE_PRIORITY = true;
	@TMGConfig(path = "grave.defaultToLocked", comment = "If set to true, all graves formed will be locked. Graves can be toggled between locked & unlocked by the grave's owner/friends. (Defaults to false.)")
	public static boolean DEFAULT_TO_LOCKED = false;
	@TMGConfig(path = "grave.allowInventorySaves", comment = "If set to false, inventory backups will not be kept. Graves can still form (if enabled), but the death inventory lists will not be able to be produced. (Defaults to true.)")
	public static boolean ALLOW_INVENTORY_SAVES = true;
	@TMGConfig(path = "grave.allowInventoryLists", comment = "If set to false, inventory lists will not be able to be spawned. Lists already in the game will not be affected. (Defaults to true.)")
	public static boolean ALLOW_INVENTORY_LISTS = true;
	@TMGConfig(path = "grave.enableChatMessageOnDeath", comment = "If set to true, a chat message with the player's grave coordinates will be displayed in chat upon their death. (Defaults to false.)")
	public static boolean ENABLE_CHAT_MESSAGE_ON_DEATH = true;

	// Placement configs
	@TMGConfig(path = "placement.graveSearchRadius", comment = "This is the radius that will be searched (centered around the location of death) to find an appropriate block to place the grave. (Radius = abs(max{x,y,z}) Note: if death occurs with y <= 0, it will center its search around y = graveSearchRadius (unless 'startVoidSearchAtOne' is true. (Defaults to 9, max of 32.)")
	public static int MAX_GRAVE_SEARCH_RADIUS = 9;
	@TMGConfig(path = "placement.startGraveSearchAtOne", comment = "If set to true, the grave will start its search for a valid placement starting at y=1 if the player died with y <= 0. If false, the search will start at y = graveSearchRadius. (Defaults to false).")
	public static boolean START_VOID_SEARCH_AT_ONE = false;
	@TMGConfig(path = "placement.ascendLiquid", comment = "If true, graves will attempt to place themselves above bodies of water/lava. If a valid location is not present at the top of the fluid, the grave will attempt to find the valid location closest to the point of death while ascending the fluid. Note: setting this to true causes more calculations to be made upon each death, possibly slowing down tick rate if people are dying underwater a lot... (Defaults to false.)")
	public static boolean ASCEND_LIQUID = false;
	@TMGConfig(path = "placement.replaceStillLava", comment = "If true, graves will be able to replace STILL lava when created. (Defaults to true).")
	public static boolean REPLACE_STILL_LAVA = true;
	@TMGConfig(path = "placement.replaceFlowingLava", comment = "If true, graves will be able to replace FLOWING lava when created. (Defaults to true).")
	public static boolean REPLACE_FLOWING_LAVA = true;
	@TMGConfig(path = "placement.replaceStillWater", comment = "If true, graves will be able to replace STILL water when created. (Defaults to true).")
	public static boolean REPLACE_STILL_WATER = true;
	@TMGConfig(path = "placement.replaceFlowingWater", comment = "If true, graves will be able to replace FLOWING water when created. (Defaults to true).")
	public static boolean REPLACE_FLOWING_WATER = true;
	@TMGConfig(path = "placement.replacePlants", comment = "If true, graves will be able to replace plants (anything that implements IPlantable: grass, crops, etc). (Defaults to true).")
	public static boolean REPLACE_PLANTS = true;

	// Interaction configs
	@TMGConfig(path = "interaction.allowGraveRobbing", comment = "If set to true, graves will be able to be accessed/manipulated by any player - not just the one who owns the grave. (Defaults to false.)")
	public static boolean ALLOW_GRAVE_ROBBING = false;
	@TMGConfig(path = "interaction.allowLockingMessages", comment = "When true, whenever a grave is (un)locked, the player will receive a chat message letting them know the current state of the grave. (Defaults to true.)")
	public static boolean ALLOW_LOCKING_MESSAGES = true;
	@TMGConfig(path = "interaction.requireSneaking", comment = "When true, players will be required to sneak while colliding with their grave to obtain their items. When false, just colliding with the grave is enough to get the items back. (Defaults to true).")
	public static boolean REQUIRE_SNEAKING = true;

	// Log configs
	@TMGConfig(path = "log.printDeathLog", comment = "If true, the server log will print the location of a player's death each time they die. (Defaults to true)")
	public static boolean PRINT_DEATH_LOG = true;

	public static ForgeConfigSpec CONFIG_SPEC;

	private static HashMap<String, BooleanValue> BOOL_MAP = new HashMap<>();
	private static HashMap<String, IntValue> INT_MAP = new HashMap<>();
	
	public static ForgeConfigSpec build() {
		ForgeConfigSpec.Builder b = new ForgeConfigSpec.Builder();
		try {
			Field[] fields = ModConfigs.class.getDeclaredFields();
			for (Field field : fields) {
				TMGConfig config = field.getAnnotation(TMGConfig.class);
				if (config != null) {
					if (field.getType() == int.class) {
						INT_MAP.put(config.path(), b.comment(config.comment()).defineInRange(config.path(), field.getInt(null), config.rangeMin(), config.rangeMax()));
					} else if (field.getType() == boolean.class) {
						BOOL_MAP.put(config.path(), b.comment(config.comment()).define(config.path(), field.getBoolean(null)));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return CONFIG_SPEC = b.build();
	}

	public static void load() {
		try {
			Field[] fields = ModConfigs.class.getDeclaredFields();
			for (Field field : fields) {
				TMGConfig config = field.getAnnotation(TMGConfig.class);
				if (config != null) {
					if (field.getType() == int.class) {
						field.setInt(null, INT_MAP.get(config.path()).get());
					} else if (field.getType() == boolean.class) {
						field.setBoolean(null, BOOL_MAP.get(config.path()).get());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	private @interface TMGConfig {
		String path();

		int rangeMin() default 0;

		int rangeMax() default Integer.MAX_VALUE;

		String comment() default "";
	}
}
