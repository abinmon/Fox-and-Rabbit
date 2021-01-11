import java.util.List;
import java.util.Iterator;
import java.util.Random;

public class Lynx extends Animal {

	/**
	 * A simple model of a lynx. lynxes age, move, eat rabbits, and die.
	 * 
	 * @author David J. Barnes and Michael Kölling
	 * @version 2011.07.31
	 */

	// Characteristics shared by all Lynxes (class variables).

	// The age at which a lynx can start to breed.
	private static final int BREEDING_AGE = 15;
	// The age to which a can live.
	private static final int MAX_AGE = 80;
	// The likelihood of a lynx breeding.
	private static final double BREEDING_PROBABILITY = 0.15;
	private static final double FOX_WIN_PROBABILIYTY = 0.03;
	// The maximum number of births.
	private static final int MAX_LITTER_SIZE = 3;
	// The food value of a single rabbit. In effect, this is the
	// number of steps a lynx can go before it has to eat again.
	private static final int RABBIT_FOOD_VALUE = 8;
	private static final int FOX_FOOD_VALUE = 10;

	private static final int MICE_FOOD_VALUE = 5;
	// A shared random number generator to control breeding.
	private static final Random rand = Randomizer.getRandom();

	// Individual characteristics (instance fields).
	// The lynx's age.
	private int age;
	// The lynx's food level, which is increased by eating rabbits.
	private int foodLevel;

	/**
	 * Create a lynx. A lynx can be created as a new born (age zero and not hungry)
	 * or with a random age and food level.
	 * 
	 * @param randomAge If true, the lynx will have random age and hunger level.
	 * @param field     The field currently occupied.
	 * @param location  The location within the field.
	 */
	public Lynx(boolean randomAge, Field field, Location location) {
		super(field, location);
		if (randomAge) {
			age = rand.nextInt(MAX_AGE);
			foodLevel = rand.nextInt(RABBIT_FOOD_VALUE + FOX_FOOD_VALUE + MICE_FOOD_VALUE);
		} else {
			age = 0;
			int number = rand.nextInt(3);
			if (number == 0) {
				foodLevel = rand.nextInt(RABBIT_FOOD_VALUE);
			} else if (number == 1) {
				foodLevel = rand.nextInt(FOX_FOOD_VALUE);
			} else {
				foodLevel = rand.nextInt(MICE_FOOD_VALUE);
			}
			;
		}
	}

	/**
	 * This is what the lynx does most of the time: it hunts for rabbits. In the
	 * process, it might breed, die of hunger, or die of old age.
	 * 
	 * @param field     The field currently occupied.
	 * @param newlynxes A list to return newly born lynxes.
	 */
	public void act(List<Actor> newLynx) {
		incrementAge();
		incrementHunger();
		if (isAlive()) {
			giveBirth(newLynx);
			// Move towards a source of food if found.
			Location newLocation = findFood();
			if (newLocation == null) {
				// No food found - try to move to a free location.
				newLocation = getField().freeAdjacentLocation(getLocation());
			}
			// See if it was possible to move.
			if (newLocation != null) {
				setLocation(newLocation);
			} else {
				// Overcrowding.
				setDead();
			}
		}
	}

	/**
	 * Increase the age. This could result in the lynx's death.
	 */
	private void incrementAge() {
		age++;
		if (age > MAX_AGE) {
			setDead();
		}
	}

	/**
	 * Make this lynx more hungry. This could result in the lynx's death.
	 */
	private void incrementHunger() {
		foodLevel--;
		if (foodLevel <= 0) {
			setDead();
		}
	}

	private boolean fight() {
		boolean Foxdead = false;
		Field field = getField();
		List<Location> adj = field.adjacentLocations(getLocation());
		Iterator<Location> it = adj.iterator();
		while (it.hasNext()) {

			Location where = it.next();
			Object actor = field.getObjectAt(where);
			if (actor instanceof Fox) {
				Actor fox = (Fox) actor;

				if (fox.isAlive() && rand.nextDouble() < FOX_WIN_PROBABILIYTY) {
					foodLevel = 0; // it dies as the next time it acts its dead
					Foxdead = false;
				} else {
					fox.setDead();
					Foxdead = true;

				}

			}
		}
		return Foxdead;

	}

	/**
	 * Look for rabbits and foxes and mice at adjacent to the current location. Only
	 * the first live rabbit/fox/mouse is eaten.
	 * 
	 * @return Where food was found, or null if it wasn't.
	 */
	private Location findFood() {
		Field field = getField();
		List<Location> adjacent = field.adjacentLocations(getLocation());
		Iterator<Location> it = adjacent.iterator();
		while (it.hasNext()) {
			Location where = it.next();
			Object actor = field.getObjectAt(where);
			if (actor instanceof Rabbit) {
				Rabbit rabbit = (Rabbit) actor;
				if (rabbit.isAlive()) {
					rabbit.setDead();
					foodLevel = RABBIT_FOOD_VALUE;
					// Remove the dead rabbit from the field.
					return where;
				}
			} else if (actor instanceof Fox) {

				if (fight() == true) {
					foodLevel = FOX_FOOD_VALUE;
					// Remove the dead fox from the field.
				} else {
					return null;
				}
				return where;
			} else if (actor instanceof Mouse) {
				Mouse mice = (Mouse) actor;
				if (mice.isAlive()) {
					mice.setDead();
					foodLevel = MICE_FOOD_VALUE;
					// Remove the dead mice from the field.
					return where;
				}
			}

		}
		return null;
	}

	/**
	 * Check whether or not this lynx is to give birth at this step. New births will
	 * be made into free adjacent locations.
	 * 
	 * @param newlynxes A list to return newly born lynxes.
	 */
	private void giveBirth(List<Actor> newlynxes) {
		// New lynxes are born into adjacent locations.
		// Get a list of adjacent free locations.
		Field field = getField();
		List<Location> free = field.getFreeAdjacentLocations(getLocation());
		int births = breed();
		for (int b = 0; b < births && free.size() > 0; b++) {
			Location loc = free.remove(0);
			Lynx young = new Lynx(false, field, loc);
			newlynxes.add(young);
		}
	}

	/**
	 * Generate a number representing the number of births, if it can breed.
	 * 
	 * @return The number of births (may be zero).
	 */
	private int breed() {
		int births = 0;
		if (canBreed() && rand.nextDouble() <= BREEDING_PROBABILITY) {
			births = rand.nextInt(MAX_LITTER_SIZE) + 1;
		}
		return births;
	}

	/**
	 * A lynx can breed if it has reached the breeding age.
	 */
	private boolean canBreed() {
		return age >= BREEDING_AGE;
	}
}
