package com.m4thg33k.tombmanygraves.inventoryManagement;

import com.m4thg33k.tombmanygraves.api.inventory.specialInventoryImplementations.VanillaMinecraftInventory;
import com.m4thg33k.tombmanygraves.util.LogHelper;
import com.m4thg33k.tombmanygraves2api.api.inventory.ISpecialInventory;
import com.m4thg33k.tombmanygraves2api.api.inventory.TransitionInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Tuple;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SpecialInventoryManager {

    private static SpecialInventoryManager INSTANCE = null;

    private Map<String, ISpecialInventory> listenerMap = new HashMap<>();
    private List<Map.Entry<String, ISpecialInventory>> sortedListeners = new ArrayList<>();

    private List<String> sortedGuiNames = new ArrayList<>();

    private SpecialInventoryManager() {

    }

    public static SpecialInventoryManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SpecialInventoryManager();
        }

        return INSTANCE;
    }


    private Stream<Map.Entry<String, ISpecialInventory>> getSpecialInventoryStream() {
        return this.sortedListeners.stream();
    }

    public static int getGuiColorForInventory(String key) {
        if (getInstance().listenerMap.containsKey(key)) {
            return getInstance().listenerMap.get(key).getInventoryDisplayNameColorForGui();
        }
        return 0; // default to black
    }

    private int clampPriority(int priority) {
        if (priority < - 10) {
            return - 10;
        } else if (priority > 10) {
            return 10;
        } else {
            return priority;
        }
    }

    @ParametersAreNonnullByDefault
    public void registerListener(ISpecialInventory iSpecialInventory) throws Exception {
        String uniqueId = iSpecialInventory.getUniqueIdentifier();

        LogHelper.info("Attempting to register special inventory: " + uniqueId);

        if (listenerMap.containsKey(uniqueId)) {
            // Already exists

            if (! iSpecialInventory.isOverwritable()) {
                // We can't overwrite the new one

                if (listenerMap.get(uniqueId).isOverwritable()) {
                    // The current one *is* overwritable, so replace it
                    listenerMap.put(uniqueId, iSpecialInventory);

                    LogHelper.info("Special inventory with id (" + uniqueId + ") already exists, but is able to be overwritten. Replacing existing inventory.");
                } else {
                    // Neither is overwritable
                    throw new Exception("Unable to register listener for TombManyGraves with unique id: " + uniqueId + ". A listener with that id has already been registered and cannot be overwritten!");
                }
            }
            // No "else" really - if the new one is overwritable, we use the one that's already in the map!
            else {
                LogHelper.info("Special inventory with id (" + uniqueId + ") already exists, but the new one is able to be overwritten. Ignoring new inventory.");

            }
        } else {
            LogHelper.info("Added special inventory with id (" + uniqueId + ") to TombManyGraves.");
            listenerMap.put(uniqueId, iSpecialInventory);
        }
    }

    public void finalizeListeners() {
        sortedListeners = this.listenerMap
                .entrySet()
                .stream()
                .sorted(
                        (x, y) -> {
                            int flag = - Integer.compare(clampPriority(x.getValue().getPriority()), clampPriority(y.getValue().getPriority()));
                            if (flag == 0) {
                                flag = x.getKey().compareTo(y.getKey());
                            }
                            return flag;
                        }
                )
                .collect(Collectors.toList());

        // also create the gui order (alphabetical apart from Main Inventory)
        sortedGuiNames = this.listenerMap
                .keySet()
                .stream()
                .sorted(
                        (x, y) -> {
                            if (VanillaMinecraftInventory.UNIQUE_IDENTIFIER.equals(x)) {
                                return - 1;
                            } else if (VanillaMinecraftInventory.UNIQUE_IDENTIFIER.equals(y)) {
                                return 1;
                            } else {
                                return listenerMap.get(x).getInventoryDisplayNameForGui().compareToIgnoreCase(listenerMap.get(y).getInventoryDisplayNameForGui());
                            }
                        }
                )
                .collect(Collectors.toList());
    }

    public List<String> getSortedGuiNames() {
        return sortedGuiNames;
    }

    public NBTTagCompound grabItemsFromPlayer(EntityPlayer player) {

        Iterator<Map.Entry<String, ISpecialInventory>> iter = getSpecialInventoryStream().iterator();
        boolean shouldContinue = true;
        while (iter.hasNext()) {
            shouldContinue = iter.next().getValue().pregrabLogic(player);
            if (! shouldContinue) {
                break;
            }
        }

        if (! shouldContinue) {
            // a special inventory decided that the grave should not form!
            return null;
        }

        NBTTagCompound compound = new NBTTagCompound();
        AtomicInteger numAdded = new AtomicInteger(0);

        getSpecialInventoryStream()
                .forEach(
                        entry -> {
                            NBTBase data = entry.getValue().getNbtData(player);
                            if (data != null) {
                                compound.setTag(entry.getKey(), data);
                                numAdded.incrementAndGet();
                            }
                        }
                );

        if (numAdded.get() == 0) {
            return null;
        } else {
            return compound;
        }
    }

    public void insertInventory(EntityPlayer player, NBTTagCompound compound, boolean shouldForce) {
        if (compound != null) {
            getSpecialInventoryStream()
                    .forEach(
                            entry -> {
                                if (compound.hasKey(entry.getKey())) {
                                    NBTBase nbtBase = compound.getTag(entry.getKey());

                                    entry.getValue().insertInventory(player, nbtBase, shouldForce);
                                }
                            }
                    );
        }
    }

    public List<ItemStack> generateDrops(NBTTagCompound compound) {
        List<ItemStack> drops = new ArrayList<>();
        if (compound != null) {
            getSpecialInventoryStream()
                    .forEach(
                            entry -> {
                                if (compound.hasKey(entry.getKey())) {
                                    List<ItemStack> dropParts = entry.getValue().getDrops(compound.getTag(entry.getKey()));
                                    drops.addAll(dropParts);
                                }
                            }
                    );
        }

        return drops;
    }

    // Returns a map where the keys are the keys of the special inventory listeners and the values are tuples
    // where the first value is the name of inventory for the gui and the second value is a list of string
    // describing the items in that inventory.
    public Map<String, Tuple<String, List<String>>> createItemListForGui(NBTTagCompound compound) {
        Map<String, Tuple<String, List<String>>> theMap = new HashMap<>();

        getSpecialInventoryStream()
                .forEach(
                        entry -> {
                            if (compound.hasKey(entry.getKey())) {
                                List<ItemStack> drops = entry.getValue().getDrops(compound.getTag(entry.getKey()));
                                if (drops.size() > 0) {
                                    theMap.put(
                                            entry.getKey(),
                                            new Tuple<>(
                                                    entry.getValue().getInventoryDisplayNameForGui(),
                                                    TransitionInventory.getGuiStringsForItemStackList(drops)
                                            )
                                    );
                                }
                            }
                        }
                );

        return theMap;
    }

}
