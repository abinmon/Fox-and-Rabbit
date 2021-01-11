import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.Color;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * A simple predator-prey simulator, based on a rectangular field containing
 * rabbits and foxes.
 * 
 * @author David J. Barnes and Michael Kölling
 * @version 2011.07.31
 */
public class Simulator {
	// Constants representing configuration information for the simulation.
	// The default width for the grid.
	private static final int DEFAULT_WIDTH = 120;
	// The default depth of the grid.
	private static final int DEFAULT_DEPTH = 80;
	// The probability that a fox will be created in any given grid position.
	private static final double FOX_CREATION_PROBABILITY = 0.02;
	// The probability that a rabbit will be created in any given grid position.
	private static final double RABBIT_CREATION_PROBABILITY = 0.06;
	private static final double LYNX_CREATION_PROBABILITY = 0.01;
	private static final double MICE_CREATION_PROBABILITY = 0.06;
	private static final double HUNTER_CREATION_PROBABILITY = 0.06;
	// List of actors in the field.
	private List<Actor> actors;
	// The current state of the field.
	private Field field;
	// The current step of the simulation.
	private int step;
	// A graphical view of the simulation.
	private SimulatorView view;

	/**
	 * Construct a simulation field with default size.
	 */
	public Simulator() {
		this(DEFAULT_DEPTH, DEFAULT_WIDTH);
	}

	/**
	 * Create a simulation field with the given size.
	 * 
	 * @param depth Depth of the field. Must be greater than zero.
	 * @param width Width of the field. Must be greater than zero.
	 */
	public Simulator(int depth, int width) {
		if (width <= 0 || depth <= 0) {
			System.out.println("The dimensions must be greater than zero.");
			System.out.println("Using default values.");
			depth = DEFAULT_DEPTH;
			width = DEFAULT_WIDTH;
		}

		actors = new ArrayList<Actor>();
		field = new Field(depth, width);

		// Create a view of the state of each location in the field.
		view = new SimulatorView(depth, width);
		view.setColor(Rabbit.class, Color.green);
		view.setColor(Fox.class, Color.orange);
		view.setColor(Lynx.class, Color.red);
		view.setColor(Mouse.class, Color.black);
		view.setColor(Hunter.class, Color.blue);
		// Setup a valid starting point.
		reset();
	}

	/**
	 * Run the simulation from its current state for a reasonably long period, (4000
	 * steps).
	 */
	public void runLongSimulation() {
		simulate(4000);
	}

	/**
	 * Run the simulation from its current state for the given number of steps. Stop
	 * before the given number of steps if it ceases to be viable.
	 * 
	 * @param numSteps The number of steps to run for.
	 */
	public void simulate(int numSteps) {
		for (int step = 1; step <= numSteps && view.isViable(field); step++) {
			view.setTotal();
			simulateOneStep();
		}
	}

	public String getStats() {
		return view.getStats();
	}

	/** return the total number in the population */
	public int getSimTotal() {
		return view.getViewTotal();
	}

	/** return the step as a string */
	public String getSimStep() {

		return view.getViewStep();
	}

	/** sets the total to 0 after every step */
	public void setTotal() {
		view.setTotal();
	}

	public void Save() {
		try (BufferedWriter bw = new BufferedWriter(new FileWriter("Saved.txt", true))) {
			bw.write(this.getStats());
			bw.newLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Run the simulation from its current state for a single step. Iterate over the
	 * whole field updating the state of each fox and rabbit.
	 */
	public void simulateOneStep() {
		step++;
		view.setTotal();
		// Provide space for newborn actors.
		List<Actor> newActors = new ArrayList<Actor>();
		// Let all rabbits act.
		for (Iterator<Actor> it = actors.iterator(); it.hasNext();) {
			Actor actor = it.next();
			actor.act(newActors);
			if (!actor.isAlive()) {
				it.remove();
			}
		}

		// Add the newly born foxes and rabbits to the main lists.
		actors.addAll(newActors);

		view.showStatus(step, field);
	}

	/**
	 * Reset the simulation to a starting position.
	 */
	public void reset() {
		step = 0;
		actors.clear();
		populate();

		// Show the starting state in the view.
		view.showStatus(step, field);
	}

	/**
	 * Randomly populate the field with foxes and rabbits.
	 */
	private void populate() {
		Random rand = Randomizer.getRandom();
		field.clear();
		for (int row = 0; row < field.getDepth(); row++) {
			for (int col = 0; col < field.getWidth(); col++) {
				if (rand.nextDouble() <= FOX_CREATION_PROBABILITY) {
					Location location = new Location(row, col);
					Fox fox = new Fox(true, field, location);
					actors.add(fox);
				} else if (rand.nextDouble() <= RABBIT_CREATION_PROBABILITY) {
					Location location = new Location(row, col);
					Rabbit rabbit = new Rabbit(true, field, location);
					actors.add(rabbit);
				} else if (rand.nextDouble() <= LYNX_CREATION_PROBABILITY) {
					Location location = new Location(row, col);
					Lynx lynx = new Lynx(true, field, location);
					actors.add(lynx);
				} else if (rand.nextDouble() <= MICE_CREATION_PROBABILITY) {
					Location location = new Location(row, col);
					Mouse mice = new Mouse(true, field, location);
					actors.add(mice);

				} else if (rand.nextDouble() <= HUNTER_CREATION_PROBABILITY) {
					Location location = new Location(row, col);
					Hunter hunter = new Hunter(true, field, location);
					actors.add(hunter);
				}
			}
		}
	}
}