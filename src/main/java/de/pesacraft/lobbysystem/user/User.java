package de.pesacraft.lobbysystem.user;

import java.util.UUID;

import net.milkbowl.vault.economy.EconomyResponse.ResponseType;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;

import de.pesacraft.lobbysystem.LobbySystem;
import de.pesacraft.lobbysystem.Position;

public class User {
	/**
	 * The amount of global coins the player has. He can buy serverwide features
	 * with this.
	 */
	protected double globalCoins;
	/**
	 * The handler for the player's inventory. Handles which items the player
	 * has in his inventory and updates them if necessary.
	 */
	protected InventoryHandler invHandler;
	/**
	 * The users last location. There he will be teleported when a game ends or
	 * something else needs to teleport him back
	 */
	protected Location lastLocation;
	/**
	 * The players current position. This can be a game or a lobby etc.
	 */
	protected Position position;
	/**
	 * The tokens the user has. Tokens are the premium cash type. They can be
	 * used for special offers.
	 */
	protected int token;
	/**
	 * The experience points the user has. They are earned by winning games etc.
	 */
	protected long experience;
	/**
	 * The general state of the user.
	 */
	protected UserState userState;
	/**
	 * The users uuid
	 */
	protected final UUID uuid;

	/**
	 * Creates a new User based on the given player</br> Gets UUID and location
	 * of that player and stores it</br>
	 *
	 * @param player
	 *            The player for which the User should be created
	 */
	public User(final Player player) {
		this.uuid = player.getUniqueId();
		this.lastLocation = player.getLocation();
		this.userState = UserState.LOBBY;
		this.position = LobbySystem.MAIN_LOBBY;
	}

	public User(final User u) {
		this.uuid = u.uuid;
		this.lastLocation = u.lastLocation;
		this.userState = u.userState;
		this.position = u.position;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof User)
			if (((User) obj).uuid.compareTo(this.uuid) == 0)
				return true;
		return false;
	}

	/**
	 * Get the player which has the stored UUID.<br>
	 * Returns null if the player isn't online.
	 *
	 * @return The Player if he is online, null otherwise
	 */
	public Player getPlayer() {
		return Bukkit.getPlayer(this.uuid);
	}

	public Position getPosition() {
		return this.position;
	}
	
	public int getToken() {
		return token;
	}

	public long getExperience() {
		return experience;
	}
	
	/**
	 * Get the uuid of this user.
	 *
	 * @return The uuid.
	 */
	public UUID getUUID() {
		return this.uuid;
	}

	/**
	 * Give the user a specific amount of global money
	 *
	 * @param amount The amount to give
	 * @return true if deposing was successful, false otherwise
	 */
	public boolean giveGlobalMoney(final int amount) {
		return LobbySystem.ECONOMY.depositPlayer(Bukkit.getOfflinePlayer(this.uuid), amount).type == ResponseType.SUCCESS;
	}
	
	/**
	 * Give the user a specific amount of tokens
	 *
	 * @param amount The amount to give
	 */
	public void giveTokens(final int amount) {
		token += amount;
	}
	
	/**
	 * Takes the user a specific amount of tokens
	 *
	 * @param amount The amount to take
	 */
	public void takeTokens(final int amount) {
		token -= amount;
	}
	
	public void addExperience(long amount) {
		experience += amount;
	}
	
	/**
	 * Lets the user leave the game.</br> Teleports the user to his last
	 * location.
	 */
	public void leave() {
		getPlayer().teleport(this.lastLocation);
	}

	/**
	 * Shows the user the passed scoreboard.<br>
	 * If a long is passed that is the time after which the scoreboard
	 * disappears and the previous gets shown again.<br>
	 *
	 * @param scoreboard
	 * @param ticks
	 */
	public void showScoreboard(final Scoreboard scoreboard, final long... ticks) {
		if (!Bukkit.getOfflinePlayer(this.uuid).isOnline())
			return;

		final Player p = Bukkit.getPlayer(this.uuid);
		final Scoreboard old = p.getScoreboard();

		p.setScoreboard(scoreboard);

		if (ticks.length > 0) {
			final BukkitRunnable runnable = new BukkitRunnable() {

				@Override
				public void run() {
					p.setScoreboard(old);
				}
			};

			runnable.runTaskLater(LobbySystem.PLUGIN, ticks[0]);
		}
	}

	/**
	 * Teleports the player to the given location.<br>
	 * Should always be used to teleport the player.<br>
	 * Checks if the new location is equal to the player's current location. If
	 * it is he won't get teleported.<br>
	 *
	 * @param loc
	 *            The location where the player should be teleported.
	 * @return true if teleportation successful, false otherwise
	 */
	public boolean teleport(final Location loc) {
		final Player p = Bukkit.getPlayer(this.uuid);
		final Location current = p.getLocation();

		if (current.equals(loc))
			return false;

		this.lastLocation = current;

		p.teleport(loc, TeleportCause.PLUGIN);

		return true;
	}
	
	public InventoryHandler getInventoryHandler() {
		return invHandler;
	}

	public void sendMessage(String msg) {
		getPlayer().sendMessage(msg);
	}
}