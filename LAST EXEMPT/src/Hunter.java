import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * A model of grass. Grass spreads every day in all directions depending on the
 * growth rate
 *
 * @author Alexander Eilert Berg
 */
public class Hunter implements Actor {

	// The amount the Hunter spread each cycle
	private static final int BREED_AGE = 20;
	private static final int MAX_AGE = 100;
	private static final double BREEDING_PROBABILITY = 0.08;
	private static final int MAX_BIRTH = 3;
	private static final int RABBIT_FOOD_VALUE = 8;
	private static final int FOX_FOOD_VALUE = 14;
	private static final int LYNX_FOOD_VALUE = 6;
	private static final Random rand = Randomizer.getRandom();

	// Time between each growth cycle
	private int growth;
	private int count;
	// Holds current step
	private int food;
	private boolean alive;
	private Location location;
	private Field field;

	/**
	 * Creates the Hunter
	 *
	 * @param field
	 * @param location
	 */
	public Hunter(boolean randAge, Field field, Location location) {
		alive = true;
		this.field = field;
		setLocation(location);
		if (randAge) {
			growth = rand.nextInt(MAX_AGE);
			food = rand.nextInt(RABBIT_FOOD_VALUE);
		} else {
			growth = 0;
			food = RABBIT_FOOD_VALUE;
		}
	}

	/**
	 * The Hunter grows
	 *
	 * @param newHunter
	 */

	/**
	 * The Hunter checks the area around itself, then grows on all the free spaces
	 * as many times as the growth rate allows.
	 *
	 * @param newHunter
	 */
	@Override
	public void act(List<Actor> newHunter) {
		incrementAge();
		incrementHunger();

		if (isAlive()) {
			giveBirth(newHunter);
			// Move towards a source of food if found.
			Location newLocation = hunt();
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

	private void incrementHunger() {
		food--;
		if (food <= 0) {
			setDead();
		}

	}

	private Location hunt() {
		Field field = getField();
		List<Location> adjacent = field.adjacentLocations(getLocation());
		Iterator<Location> it = adjacent.iterator();
		while (it.hasNext()) {
			Location where = it.next();
			Object animal = field.getObjectAt(where);
			if (animal instanceof Rabbit) {
				Rabbit rabbit = (Rabbit) animal;
				if (rabbit.isAlive()) {
					rabbit.setDead();
					food = food + RABBIT_FOOD_VALUE;
					// Remove the dead rabbit from the field.
					return where;
				} else if (animal instanceof Fox) {
					Fox fox = (Fox) animal;
					if (fox.isAlive()) {
						fox.setDead();
						food = FOX_FOOD_VALUE + food;
						// Remove the dead fox from the field.
						return where;
					}
				} else if (animal instanceof Lynx) {
					Lynx lynx = (Lynx) animal;
					if (lynx.isAlive()) {
						lynx.setDead();
						food = food + LYNX_FOOD_VALUE;
						// Remove the dead lynx from the field.
						return where;
					}
				} else if (animal instanceof Mouse) {
					Mouse mice = (Mouse) animal;
					if (mice.isAlive()) {
						mice.setDead();
						// Remove the dead mouse from the field.
						return where;
					}
				}

			}
		}
		return null;
	}

	private void giveBirth(List<Actor> newHunter) {
		Field field = getField();
		List<Location> free = field.getFreeAdjacentLocations(getLocation());
		int births = breed();
		for (int b = 0; b < births && free.size() > 0; b++) {
			Location loc = free.remove(0);
			Hunter young = new Hunter(false, field, loc);
			newHunter.add(young);
		}

	}

	private int breed() {
		int birth = 0;
		if ((BREED_AGE <= growth) && rand.nextDouble() <= BREEDING_PROBABILITY) {
			birth = rand.nextInt(MAX_BIRTH) + 1;
		}
		return birth;
	}

	private void incrementAge() {
		growth++;
		if (growth > MAX_AGE) {
			setDead();
		}

	}

	@Override
	public boolean isAlive() {

		return alive;
	}

	@Override
	public void setDead() {
		alive = false;
		if (location != null) {
			field.clear(location);
			location = null;
			field = null;
		}

	}

	@Override
	public Location getLocation() {

		return location;
	}

	@Override
	public void setLocation(Location newLocation) {
		if (location != null) {
			field.clear(location);
		}
		location = newLocation;
		field.place(this, newLocation);

	}

	@Override
	public Field getField() {

		return field;
	}
}