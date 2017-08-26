package com.m4thg33k.tombmanygraves.lib;

import java.awt.Color;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ModConfigs {

    public static Configuration config;

    // Client configs
    public static boolean FORCE_DIRT_RENDER;
    public static int GRAVE_SKULL_RENDER_TYPE;
    public static boolean DISPLAY_GRAVE_NAME;
    public static int NAME_FORCE;
    public static int NAME_YIELD;
    public static Color NEAR_PARTICLE;
    public static Color FAR_PARTICLE;
    public static boolean ALLOW_PARTICLE_PATH;
    public static boolean REQUIRE_SNEAK_FOR_PATH;
    public static boolean GRAVE_RENDERING_ENABLED;
    public static boolean GRAVE_POS_ENABLED;

    // Common configs
    public static boolean ENABLE_GRAVES;
    public static boolean DROP_ITEMS_ON_GROUND;
    public static boolean GIVE_ITEMS_IN_GRAVE_PRIORITY;
    public static boolean DEFAULT_TO_LOCKED;
    public static boolean ALLOW_INVENTORY_SAVES;
    public static boolean ALLOW_INVENTORY_LISTS;
    public static boolean ENABLE_CHAT_MESSAGE_ON_DEATH;

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

        handleClientConfigs();
        handleServerConfigs();
        handleCommonConfigs();
        handleInteractionConfigs();
        handleGravePlacementConfigs();

        config.save();
    }

    private static void handleClientConfigs()
    {
        handleColorConfigs();

        FORCE_DIRT_RENDER = config.get("renderOptions", "forceDirtRender", false, "If true, all graves will " +
                "be rendered with a dirt base instead of using the adaptive model. (Defaults to false).").getBoolean();

        GRAVE_SKULL_RENDER_TYPE = config.get("renderOptions", "graveSkullRenderType", 3, "Changing this value " +
                "determines the type of skull that renders on the grave. 0 = Skeleton, 1 = Wither Skeleton, " +
                "2 = Zombie, 3 = The Player Who Owns The Grave, 4 = Creeper. Any value outside of this range " +
                "will be set to 3 instead. (Defaults to 3.)").getInt();
        if (GRAVE_SKULL_RENDER_TYPE < 0 || GRAVE_SKULL_RENDER_TYPE > 4)
        {
            GRAVE_SKULL_RENDER_TYPE = 3;
        }

        DISPLAY_GRAVE_NAME = config.get("renderOptions", "displayGraveName", true, "When true, this will display " +
                "both the name of the grave's owner and the current mode of the grave when looking at it. " +
                "(Defaults to true)").getBoolean();

        try {
            FAR_PARTICLE = new Color(Integer.decode(config.get("renderOptions", "farParticleColor", "0x000000",
                    "This is the color of the path particles spawned by the death inventory list when you are more " +
                            "than 100 blocks from the grave. (Defaults to 0x000000)").getString()));
        } catch (Exception e)
        {
            FAR_PARTICLE = Color.BLACK;
        }

        try {
            NEAR_PARTICLE = new Color(Integer.decode(config.get("renderOptions", "nearParticleColor", "0xFFFFFF",
                    "This is the color of the path particles spawned by the inventory list when you are within 10 " +
                            "blocks of the grave. (Defaults to 0xFFFFFF.)").getString()));
        }
        catch (Exception e)
        {
            NEAR_PARTICLE = Color.white;
        }

        ALLOW_PARTICLE_PATH = config.get("renderOptions", "allowParticlePath", true, "If set to false, the death " +
                "inventory lists will not show particle effects leading to the grave. (Defaults to true)").getBoolean();

        REQUIRE_SNEAK_FOR_PATH = config.get("renderOptions", "requireSneakForPath", false, "If set to true, the death" +
                "inventory lists will only show their particle trail if you are sneaking (and the path is turned on). " +
                "(Defaults to false)").getBoolean();

        GRAVE_RENDERING_ENABLED = config.get("renderOptions", "enableGraveRendering", true, "If set to false, graves " +
                "will not be rendered (at all), meaning there will just be an invisible block there. This is useful if " +
                "a rendering crash is consistently occuring and I haven't had a chance to post a fix yet. (Defaults " +
                "to true)").getBoolean();

        GRAVE_POS_ENABLED = config.get("renderOptions", "enableGravePositionRender", false, "If set to true, graves " +
                "will display their block position when looking at them (useful for admins/mods for debugging purposes. " +
                "There is also a command to temporarily toggle this effect in-game. (Defaults to false.)").getBoolean();
    }

    private static void handleColorConfigs()
    {
        NAME_FORCE = handleColor("nameColorWhenForced", "0xFFFFFF");
        NAME_YIELD = handleColor("nameColorWhenYielding", "0xFF0000");
    }

    private static int handleColor(String configName, String standard)
    {
        try
        {
            return Integer.decode(config.get("Colors", configName, standard, "This is one of four configs " +
                    "for how the name/mode render when looking at a grave (assuming that config is enabled. " +
                    "(Defaults to " + standard + ")").getString());
        }
        catch (Exception e)
        {
            return Integer.decode(standard);
        }
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

        ALLOW_INVENTORY_SAVES = config.get("graveConfigs", "allowInventorySaves", true, "If set to false, " +
                "inventory backups will not be kept. Graves can still form (if enabled), but the death " +
                "inventory lists will not be able to be produced. (Defaults to true.)").getBoolean();

        ALLOW_INVENTORY_LISTS = config.get("listConfigs", "allowInventoryLists", true, "If set to false, " +
                "inventory lists will not be able to be spawned. Lists already in the game will not be " +
                "affected. (Defaults to true.)").getBoolean();

        ENABLE_CHAT_MESSAGE_ON_DEATH = config.get("chatConfigs", "enableChatMessageOnDeath", false, "If set to true, " +
                "a chat message with the player's grave coordinates will be displayed in chat upon their death. " +
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
