package me.dnorris.sponge.api.command;

import com.google.common.collect.Lists;
import me.dnorris.sponge.menu.MenuPlugin;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public abstract class BasicCommand implements CommandExecutor, CommandCallable {

    private final String permission;
    private final String usage;
    public final Text description;
    public final Text extendedDescription;

    public BasicCommand() {
        Command command = this.getClass().getAnnotation(Command.class);

        if(command == null) {
            throw new UnsupportedOperationException("Command must be annoted by @Command (" + this.getClass().getSimpleName() + ")");
        }

        this.registerCommand(command);
        this.usage= command.usage();
        this.permission = command.permission();
        this.description = Text.of(command.description());
        this.extendedDescription = Text.of(command.extendedDescription());
    }

    private void registerCommand(Command command) {
        Sponge.getCommandManager().register(MenuPlugin.getInstance(), this, command.aliases());
    }

    @Override
    public CommandResult process(CommandSource source, String arguments) {
        return CommandResult.success();
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        return CommandResult.success();
    }

    @Override
    public boolean testPermission(CommandSource source) {
        if (!source.hasPermission(this.permission)) {
            source.sendMessage(Text.of("§c§l(!) §cYou do not have permission to do this!"));
            return false;
        } else {
            return true;
        }
    }

    @Override
    public List<String> getSuggestions(CommandSource source, String arguments, @Nullable Location<World> targetPosition) {
        List<String> playerNames = Lists.newArrayList();

        for (Player player : Sponge.getServer().getOnlinePlayers()) {
            playerNames.add(player.getName());
        }

        return playerNames;
    }

    public void sendUsage(CommandSource source) {
        source.sendMessage(Text.of("§c" + this.usage));
    }

    @Override
    public Optional<Text> getShortDescription(CommandSource source) {
        return Optional.of(this.description);
    }

    @Override
    public Optional<Text> getHelp(CommandSource source) {
        return Optional.empty();
    }

    @Override
    public Text getUsage(CommandSource source) {
        return Text.EMPTY;
    }
}
