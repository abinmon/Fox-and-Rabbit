import java.util.List;
import java.util.Random;

/**
 * A simple model of a Mouse. Mice age, move, breed, and die.
 * 
 * @author David J. Barnes and Michael Kölling
 * @version 2011.07.31
 */
public class Mouse extends Animal {
	// Characteristics shared by all Mice (class variables).

	// The age at which a Mouse can start to breed.
	private static final int BREEDING_AGE = 5;
	// The age to which a Mouse can live.
	private static final int MAX_AGE = 20;
	// The likelihood of a Mouse breeding.
	private static final double BREEDING_PROBABILITY = 0.05;
	private static final double DISEASE_PROBABILITY = 0.01;
	// The maximum number of births.
	private static final int MAX_LITTER_SIZE = 4;
	// A shared random number generator to control breeding.
	private static final Random rand = Randomizer.getRandom();

	// Individual characteristics (instance fields).

	// The Mouse's age.
	private int age;

	/**
	 * Create a new Mouse. A Mouse may be created with age zero (a new born) or with
	 * a random age.
	 * 
	 * @param randomAge If true, the Mouse will have a random age.
	 * @param field     The field currently occupied.
	 * @param location  The location within the field.
	 */
	public Mouse(boolean randomAge, Field field, Location location) {
		super(field, location);
		age = 0;
		if (randomAge) {
			age = rand.nextInt(MAX_AGE);
		}
	}

	/**
	 * This is what the Mouse does most of the time - it runs around. Sometimes it
	 * will breed or die of old age.
	 * 
	 * @param newMice A list to return newly born Mice.
	 */
	public void act(List<Actor> newMice) {
		incrementAge();
		if (isAlive()) {
			giveBirth(newMice);
			// Try to move into a free location.
			Location newLocation = getField().freeAdjacentLocation(getLocation());
			if (newLocation != null) {
				setLocation(newLocation);
			} else {
				// Overcrowding.
				setDead();
			}
		}
	}

	/**
	 * Increase the age. This could result in the Mouse's death.
	 */
	private void incrementAge() {
		age++;
		if (age > MAX_AGE) {
			setDead();
		} else if (rand.nextDouble() <= DISEASE_PROBABILITY) {
			setDead();
		}
	}

	/**
	 * Check whether or not this Mouse is to give birth at this step. New births
	 * will be made into free adjacent locations.
	 * 
	 * @param newMice A list to return newly born Mice.
	 */
	private void giveBirth(List<Actor> newMice) {
		// New Mice are born into adjacent locations.
		// Get a list of adjacent free locations.
		Field field = getField();
		List<Location> free = field.getFreeAdjacentLocations(getLocation());
		int births = breed();
		for (int b = 0; b < births && free.size() > 0; b++) {
			Location loc = free.remove(0);
			Mouse young = new Mouse(false, field, loc);
			newMice.add(young);
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
	 * A Mouse can breed if it has reached the breeding age.
	 * 
	 * @return true if the Mouse can breed, false otherwise.
	 */
	private boolean canBreed() {
		return age >= BREEDING_AGE;
	}
}
