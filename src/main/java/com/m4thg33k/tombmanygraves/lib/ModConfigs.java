package com.m4thg33k.tombmanygraves.lib;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ModConfigs {

    public static Configuration config;

    // Common configs
    public static boolean ENABLE_GRAVES;
    public static boolean DROP_ITEMS_ON_GROUND;
    public static boolean GIVE_ITEMS_IN_GRAVE_PRIORITY;
    public static boolean DEFAULT_TO_LOCKED;

    // Grave placement configs
    public static int MAX_GRAVE_SEARCH_RADIUS;
    public static boolean START_VOID_SEARCH_AT_ONE;
    public static boolean ASCEND_LIQUID;
    public static boolean REPLACE_STILL_LAVA;
    public static boolean REPLACE_FLOWING_LAVA;
    public static boolean REPLACE_STILL_WATER;
    public static boolean REPLACE_FLOWING_WATER;
    public static boolean REPLACE_PLANTS;

    // Grave/Player interactions
    public static boolean ALLOW_GRAVE_ROBBING;
    public static boolean ALLOW_LOCKING_MESSAGES;
    public static boolean REQUIRE_SNEAKING;

    // Server related configs
    public static boolean PRINT_DEATH_LOG;

    public static void preInit(FMLPreInitializationEvent event)
    {
        config = new Configuration(event.getSuggestedConfigurationFile());
        config.load();

        handleServerConfigs();
        handleCommonConfigs();
        handleInteractionConfigs();
        handleGravePlacementConfigs();

        config.save();
    }

    private static void handleCommonConfigs()
    {
        ENABLE_GRAVES = config.get("allowedObjects", "enableGraves", true, "If set to false, graves will no longer be " +
                "spawned upon players deaths. This does not get rid of any graves currently in-world." +
                " (Defaults to true...obviously.)").getBoolean();

        DROP_ITEMS_ON_GROUND = config.get("graveConfigs", "dropItemsOnGround", false, "If set to true, instead of" +
                "attempting to place all items back in the player's original slots, the items will instead be placed " +
                "on the ground like every other grave mod. (Defaults to false.)").getBoolean();

        GIVE_ITEMS_IN_GRAVE_PRIORITY = config.get("graveConfigs", "giveItemsInGravePriority", true, "If true and an " +
                "item in the grave was originally in a slot that is now not empty, the grave will remove the current " +
                "item, place the item from the grave in that slot, and then drop the original item on the ground. If " +
                "false, any item from the grave attempting to fill a non-empty slot will instead be dropped on the " +
                "ground. (Defaults to true.)").getBoolean();

        DEFAULT_TO_LOCKED = config.get("graveConfigs", "defaultToLocked", false, "If set to true, all graves formed " +
                "will be locked. Graves can be toggled between locked & unlocked by the grave's owner/friends. " +
                "(Defaults to false.)").getBoolean();
    }

    private static void handleServerConfigs()
    {
        PRINT_DEATH_LOG = config.get("Logs", "printDeathLog", true, "If true, the server log will print the location " +
                "of a player's death each time they die. (Defaults to true)").getBoolean();
    }

    private static void handleInteractionConfigs()
    {
        ALLOW_GRAVE_ROBBING = config.get("GravePlayerInteraction", "allowGraveRobbing", false, "If set to true, " +
                "graves will be able to be accessed/manipulated by any player - not just the one who owns the " +
                "grave. (Defaults to false.)").getBoolean();

        ALLOW_LOCKING_MESSAGES = config.get("GravePlayerInteraction", "allowLockingMessages", true, "When true, " +
                "whenever a grave is (un)locked, the player will receive a chat message letting them know " +
                "the current state of the grave. (Defaults to true.)").getBoolean();

        REQUIRE_SNEAKING = config.get("GravePlayerInteractions", "requireSneaking", true, "When true, players " +
                "will be required to sneak while colliding with their grave to obtain their items. When false, " +
                "just colliding with the grave is enough to get the items back. (Defaults to true).").getBoolean();
    }

    private static void handleGravePlacementConfigs()
    {
        MAX_GRAVE_SEARCH_RADIUS = config.get("GravePlacement", "graveSearchRadius", 9, "This is the radius that " +
                "will be searched (centered around the location of death) to find an appropriate block to place " +
                "the grave. (Radius = abs(max{x,y,z}) Note: if death occurs with y <= 0, it will center its search " +
                "around y = graveSearchRadius (unless 'startVoidSearchAtOne' is true. " +
                "(Defaults to 9, max of 32.)").getInt();
        if (MAX_GRAVE_SEARCH_RADIUS < 0)
        {
            MAX_GRAVE_SEARCH_RADIUS = 0;
        }
        else if (MAX_GRAVE_SEARCH_RADIUS > 32)
        {
            MAX_GRAVE_SEARCH_RADIUS = 32;
        }

        START_VOID_SEARCH_AT_ONE = config.get("GravePlacement", "startGraveSearchAtOne", false, "If set to true, " +
                "the grave will start its search for a valid placement starting at y=1 if the player died with " +
                "y <= 0. If false, the search will start at y = graveSearchRadius. (Defaults to false).").getBoolean();

        ASCEND_LIQUID = config.get("GravePlacement", "ascendLiquid", false, "If true, graves will attempt to place " +
                "themselves above bodies of water/lava. If a valid location is not present at the top of the fluid, " +
                "the grave will attempt to find the valid location closest to the point of death while ascending " +
                "the fluid. Note: setting this to true causes more calculations to be made upon each death, possibly " +
                "slowing down tick rate if people are dying underwater a lot... (Defaults to false.)").getBoolean();

        REPLACE_FLOWING_LAVA = config.get("GravePlacement", "replaceFlowingLava", true, "If true, graves will " +
                "be able to replace FLOWING lava when created. (Defaults to true).").getBoolean();

        REPLACE_STILL_LAVA = config.get("GravePlacement", "replaceStillLava", true, "If true, graves will " +
            "be able to replace STILL lava when created. (Defaults to true).").getBoolean();

        REPLACE_FLOWING_WATER = config.get("GravePlacement", "replaceFlowingWater", true, "If true, graves will " +
                "be able to replace FLOWING water when created. (Defaults to true).").getBoolean();

        REPLACE_STILL_WATER = config.get("GravePlacement", "replaceStillLava", true, "If true, graves will " +
                "be able to replace STILL water when created. (Defaults to true).").getBoolean();

        REPLACE_PLANTS = config.get("GravePlacement", "replacePlants", true, "If true, graves will " +
                "be able to replace plants (anything that implements IPlantable: grass, crops, etc). " +
                "(Defaults to true).").getBoolean();
    }
}
