package com.journeygaming.menu.command;

import com.journeygaming.lib.JourneyGaming;
import com.journeygaming.lib.command.BasicCommand;
import com.journeygaming.lib.command.Command;
import com.journeygaming.menu.MenuPlugin;
import com.journeygaming.menu.data.Menu;
import com.journeygaming.menu.data.MenuConfig;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.io.File;

@Command(
		usage = "/menu reload <name> | /menu load <path> | /menu list | /menu open <name>",
		permission = "journey.menu",
		description = "Open menus",
		extendedDescription = "Open menus",
		aliases = {
				"gui",
				"menu"
		}
)
public class MenuCommand extends BasicCommand {

	private final MenuPlugin plugin;

	public MenuCommand(MenuPlugin plugin) {
		super();

		this.plugin = plugin;
	}

	public CommandResult process(CommandSource source, String arguments) {
		if (!this.testPermission(source) || !(source instanceof Player)) {
			return CommandResult.empty();
		}

		Player player = (Player) source;
		String[] args = arguments.split(" ");

		if(args.length != 2) {
			if(args.length == 1 && args[0].equalsIgnoreCase("list")) {
				if(!player.hasPermission("journey.menu.admin.list")) {
					return CommandResult.empty();
				}

				player.sendMessage(Text.of("Menus:"));
				for (String menuName : this.plugin.getMenuNames()) {
					player.sendMessage(Text.of(" - " + menuName));
				}

				return CommandResult.success();
			}

			this.sendUsage(source);
			return CommandResult.success();
		}

		if(args[0].equalsIgnoreCase("load") && player.hasPermission("journey.menu.admin.load")) {
			File file = new File(JourneyGaming.getInstance().getConfigDir() + File.separator + "JourneyGamingSponge" + File.separator + MenuConfig.PATH + File.separator + args[1]);

			if(!file.exists()) {
				player.sendMessage(Text.of("File doesn't exist! example file: 'example.conf' (JourneyGamingSponge/menus/example.conf) example directory: 'idiot/' (JourneyGamingSponge/menus/idiot/)"));
				return CommandResult.success();
			}

			if(file.isDirectory()) {
				this.plugin.handleDirectory(file);
			}else {
				String name = file.getPath().replace((JourneyGaming.getInstance().getConfigDir() + File.separator + "JourneyGamingSponge" + File.separator + MenuConfig.PATH + File.separator), "").replace(".conf", "");
				Menu menu = new Menu(name);

				this.plugin.addMenu(menu);
			}

			player.sendMessage(Text.of("Menu loaded"));
			return CommandResult.success();
		}

		Menu menu = this.plugin.getMenu(args[1]);

		if(menu == null) {
			player.sendMessage(Text.of("Menu doesn't exist!"));
			return CommandResult.empty();
		}

		if(args[0].equalsIgnoreCase("reload") && player.hasPermission("journey.menu.admin.reload")) {
			menu.reloadConfig();
			menu.loadItems();
			player.sendMessage(Text.of("Menu reloaded"));
			return CommandResult.success();
		}

		menu.open(player);

		return CommandResult.empty();
	}
}
