import java.util.List;

/**
 * A class representing shared characteristics of animals.
 * 
 * @author David J. Barnes and Michael Kölling
 * @version 2011.07.31
 */
public interface Actor {

	/**
	 * This abstract method needs to be implemented in its subclasses which allows
	 * the animals or the hunter to act.
	 * 
	 * @param newActors A list to receive newly born animals or hunters.
	 */
	abstract public void act(List<Actor> newActor);

	/**
	 * Check whether the animal or the hunter is alive or not.
	 * 
	 * @return true if the animal/hunter is still alive.
	 */
	abstract boolean isAlive();

	/**
	 * Indicate that the animal or hunter is no longer alive. It is removed from the
	 * field.
	 */
	abstract void setDead();

	/**
	 * Return the location of the animal or the hunter.
	 * 
	 * @return The animal/hunter's location.
	 */
	abstract Location getLocation();

	/**
	 * Place the animal or hunter at the new location in the given field.
	 * 
	 * @param newLocation The animal/hunter's new location.
	 */
	abstract void setLocation(Location newLocation);

	/**
	 * Return the field of the animal or the hunter.
	 * 
	 * @return The animal/hunter's field.
	 */
	abstract Field getField();
}
