package com.m4thg33k.tombmanygraves.inventoryManagement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.ParametersAreNonnullByDefault;

import com.m4thg33k.tombmanygraves.api.inventory.specialinventories.VanillaInventory;
import com.m4thg33k.tombmanygraves.util.LogHelper;
import com.m4thg33k.tombmanygraves2api.api.IGraveInventory;
import com.m4thg33k.tombmanygraves2api.api.GraveRegistry;
import com.m4thg33k.tombmanygraves2api.api.TempInventory;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.Tuple;

public class SpecialInventoryManager {

	private static SpecialInventoryManager INSTANCE = null;

	private Map<String, IGraveInventory> listenerMap = new HashMap<>();
	private List<Map.Entry<String, IGraveInventory>> sortedListeners = new ArrayList<>();
	private Map<IGraveInventory, GraveRegistry> annotationMap = new HashMap<>();

	private List<String> sortedGuiNames = new ArrayList<>();

	private SpecialInventoryManager() {

	}

	public static SpecialInventoryManager getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new SpecialInventoryManager();
		}

		return INSTANCE;
	}

	public Stream<Map.Entry<String, IGraveInventory>> getSpecialInventoryStream() {
		return this.sortedListeners.stream();
	}

	public static int getGuiColorForInventory(String key) {
		SpecialInventoryManager instance = getInstance();
		if (instance.listenerMap.containsKey(key)) {
			return instance.annotationMap.get(instance.listenerMap.get(key)).color();
		}
		return 0; // default to black
	}

	private int clampPriority(int priority) {
		if (priority < -10) {
			return -10;
		} else if (priority > 10) {
			return 10;
		} else {
			return priority;
		}
	}

	@ParametersAreNonnullByDefault
	public void registerListener(IGraveInventory iSpecialInventory, Map<String, Object> annotation) throws Exception {
		String uniqueId = (String) annotation.get("id");
		annotationMap.put(iSpecialInventory, iSpecialInventory.getClass().getDeclaredAnnotation(GraveRegistry.class));
		LogHelper.info("Attempting to register special inventory: " + uniqueId);
		if (listenerMap.containsKey(uniqueId)) {
			// Already exists

			if (!(boolean)annotation.get("overridable")) {
				// We can't overwrite the new one
				if (annotationMap.get(listenerMap.get(uniqueId)).overridable()) {
					// The current one *is* overwritable, so replace it
					listenerMap.put(uniqueId, iSpecialInventory);
					LogHelper.info("Special inventory with id (" + uniqueId + ") already exists, but is able to be overwritten. Replacing existing inventory.");
				} else {
					// Neither is overwritable
					throw new Exception("Unable to register listener for TombManyGraves with unique id: " + uniqueId + ". A listener with that id has already been registered and cannot be overwritten!");
				}
			}
			// No "else" really - if the new one is overwritable, we use the one
			// that's already in the map!
			else {
				LogHelper.info("Special inventory with id (" + uniqueId + ") already exists, but the new one is able to be overwritten. Ignoring new inventory.");

			}
		} else {
			LogHelper.info("Added special inventory with id (" + uniqueId + ") to TombManyGraves.");
			listenerMap.put(uniqueId, iSpecialInventory);
		}
	}

	public void finalizeListeners() {
		sortedListeners = this.listenerMap.entrySet().stream().sorted((x, y) -> {
			System.out.println(annotationMap.get(x.getValue()));
			int flag = -Integer.compare(clampPriority(annotationMap.get(x.getValue()).priority()), annotationMap.get(y.getValue()).priority());
			if (flag == 0) {
				flag = x.getKey().compareTo(y.getKey());
			}
			return flag;
		}).collect(Collectors.toList());

		// also create the gui order (alphabetical apart from Main Inventory)
		sortedGuiNames = this.listenerMap.keySet().stream().sorted((x, y) -> {
			if (VanillaInventory.UNIQUE_IDENTIFIER.equals(x)) {
				return -1;
			} else if (VanillaInventory.UNIQUE_IDENTIFIER.equals(y)) {
				return 1;
			} else {
				return (annotationMap.get(listenerMap.get(x)).name()).compareToIgnoreCase((annotationMap.get(listenerMap.get(y)).name()));
			}
		}).collect(Collectors.toList());
	}

	public List<String> getSortedGuiNames() {
		return sortedGuiNames;
	}

	public HashMap<String, TempInventory> grabItemsFromPlayer(EntityPlayer player) {

		Iterator<Map.Entry<String, IGraveInventory>> iter = getSpecialInventoryStream().iterator();
		boolean shouldContinue = true;
		while (iter.hasNext()) {
			shouldContinue = iter.next().getValue().pregrabLogic(player);
			if (!shouldContinue) {
				break;
			}
		}
		HashMap<String, TempInventory> itms = new HashMap<>();
		if (!shouldContinue) {
			// a special inventory decided that the grave should not form!
			return itms;
		}
		//NBTTagCompound compound = new NBTTagCompound();
		AtomicInteger numAdded = new AtomicInteger(0);

		getSpecialInventoryStream().forEach(entry -> {
			TempInventory data = entry.getValue().getItems(player);
			if (data != null) {
				itms.put(entry.getKey(), data);
				numAdded.incrementAndGet();
			}
		});

		return itms;
	}

	public void insertInventory(EntityPlayer player, NBTTagCompound compound, boolean shouldForce) {
		if (compound != null) {
			getSpecialInventoryStream().forEach(entry -> {
				if (compound.hasKey(entry.getKey())) {
					entry.getValue().insertInventory(player, new TempInventory(compound.getTagList(entry.getKey(), 10)), shouldForce);
				}
			});
		}
	}

	public List<ItemStack> generateDrops(NBTTagCompound compound) {
		List<ItemStack> drops = new ArrayList<>();
		if (compound != null) {
			getSpecialInventoryStream().forEach(entry -> {
				if (compound.hasKey(entry.getKey())) {
					TempInventory inv = new TempInventory(compound.getTagList(entry.getKey(), 10));
					drops.addAll(inv.getListOfNonEmptyItemStacks());
				}
			});
		}

		return drops;
	}

	// Returns a map where the keys are the keys of the special inventory
	// listeners and the values are tuples
	// where the first value is the name of inventory for the gui and the second
	// value is a list of string
	// describing the items in that inventory.
	public Map<String, Tuple<String, List<String>>> createItemListForGui(NBTTagCompound compound) {
		Map<String, Tuple<String, List<String>>> theMap = new HashMap<>();

		getSpecialInventoryStream().forEach(entry -> {
			if (compound.hasKey(entry.getKey())) {
				TempInventory inv = new TempInventory(compound.getTagList(entry.getKey(), 10));
				List<ItemStack> drops = inv.getListOfNonEmptyItemStacks();
				if (drops.size() > 0) {
					theMap.put(entry.getKey(), new Tuple<>(annotationMap.get(entry.getValue()).name(), TempInventory.getGuiStringsForItemStackList(drops)));
				}
			}
		});

		return theMap;
	}

}
