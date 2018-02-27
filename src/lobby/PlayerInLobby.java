package lobby;

/**
 * Will hold all necessary information, like player Slot, connectionId, name, faction/color etc.<br>
 * Some of these informations will not be symmetrical between clients,
 * therefore they should never be used for deterministic results;<br>
 * <u>Unsymmetric and Non-Deterministic</u> will be marked with <b>NSy</b> (for Non Symmetric)
 * after the object-name
 * @author Sven T. Schneider
 */
public class PlayerInLobby {

	/**
	 * Registration-ID, given by the Server; Symmetric
	 */
	public int id;
	
	/**
	 * Player Name, Symmetric, however should not be used in calculations
	 */
	public String name;
	
	/**
	 * Connection name, used as Send-Adress, NOT SYMMETRIC!
	 */
	public String connName_NSy;
}
