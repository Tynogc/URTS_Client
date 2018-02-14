package gameState;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handles a part of the Game-States, corresponding to a Client
 * @author Sven T. Schneider
 */
public class ClientState {
	
	/**
	 * Handling input of volatile commands
	 */
	private List<String> volatileCommand;

	/**
	 * A Hashmap instance for handling the Game-States
	 */
	private Map<Integer, String> state;
	
	/**
	 * A Hashmap instance for handling state-modifiers (modifying Game-States)
	 */
	private Map<Integer, List<String>> modifiers;
	
	/**
	 * Id of client, see chapter Identification for detail.
	 */
	public final int id;
	
	/**
	 * Initializes the client-state
	 * @param i the id of the client (0 - 0xff)
	 */
	public ClientState(int i){
		state = new HashMap<>(100); //Initialize Hashmap with high capacity
		id = i;
	}
}
