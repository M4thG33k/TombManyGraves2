package com.m4thg33k.tombmanygraves2api.api;

import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface SpecialInventory {
	/**
	 * Returns the unique identifier for the inventory in question; Unique
	 * identifiers should be alphanumeric (including underscore "_"). Failure to
	 * follow this format will cause an error. If two special inventories with
	 * the same id are attempted to be "installed", an error will be thrown
	 **/
	String id();

	/**
	 * As of this moment, priority actually does not affect the order in which
	 * inventories are handled. In the future, this may be necessary, but I
	 * wanted this method in the API to begin with.
	 * 
	 * Return an integer between -10 and 10 (both inclusive). Larger numbers
	 * have a higher priority. Meaning that 3 > 2 => an inventory with a
	 * priority of 3 will have its methods handled before an inventory with a
	 * priority of 2. The default (vanilla) Minecraft player inventory has a
	 * priority of 0 (zero). 99.99% of the time, you should leave the priority
	 * as 0. If two inventories have the same priority, they will be handled in
	 * order of their unique identifier (using the standard lexigraphical
	 * order).
	 */
	int priority() default 0;

	/**
	 * Only loads this inventory if the specificed mod is loaded. Leave blank to
	 * always load.
	 */
	String reqMod() default "";

	/**
	 * Any implementation returning true from this method can be replaced by
	 * another implementation with the same unique identifier. This is mainly
	 * used if a mod author wants to absorb a plugin that I (M4thG33k) created
	 * into their own mod. *MY* implementations (apart from Baubles for now)
	 * will return true, allowing a break from my implementation to theirs,
	 * if/when desired.
	 */
	boolean overridable() default false;

	/**
	 * Return the color you wish the inventory display name to be within the
	 * death inventory list.
	 */
	int color() default 0;

	/**
	 * Returns the string used inside GUIs (for example, the death inventory
	 * list) for this specific inventory. For example, vanilla Minecraft uses
	 * "Main Inventory" and Baubles uses "Baubles". The vanilla Minecraft "Main
	 * Inventory" item will be displayed first, followed by all other
	 * inventories in order of the value returned by this method in
	 * lexigraphical order.
	 */
	String name();
}
