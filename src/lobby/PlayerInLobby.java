package lobby;

/**
 * Will hold all neccesary information, like player Slot, connectionId, name, faction/color etc.<br>
 * Some of these informations will not be Symetrical between clients,
 * therfor they should never be used for deterministic results;<br>
 * <u>Unsymetric and Non-Deterministic</u> will be marked with <b>NSy</b> (for Non Symetric)
 * after the object-name
 * @author Sven T. Schneider
 */
public class PlayerInLobby {

	/**
	 * Registration-ID, given by the Server; Symetric
	 */
	public int id;
	
	/**
	 * Player Name, Symetric, however should not be used in calculations
	 */
	public String name;
	
	/**
	 * Connection name, used as Send-Adress, NOT SYMETRIC!
	 */
	public String connName_NSy;
}
