package com.journeygaming.menu.ui;

import com.journeygaming.menu.MenuPlugin;
import com.mcsimonflash.sponge.teslalibs.inventory.Action;
import com.mcsimonflash.sponge.teslalibs.inventory.Element;
import com.mcsimonflash.sponge.teslalibs.inventory.View;
import net.minecraft.inventory.ContainerChest;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;
import org.spongepowered.api.item.inventory.InventoryArchetypes;
import org.spongepowered.api.item.inventory.property.InventoryDimension;
import org.spongepowered.api.item.inventory.property.InventoryTitle;
import org.spongepowered.api.text.Text;

import java.util.List;
import java.util.Map;

public class GenericUI {

    private View.Builder basicView = View.builder()
            .archetype(InventoryArchetypes.DOUBLE_CHEST);

    private Player player;
    private View view;

    public GenericUI(Player player, String name, int height, Map<Integer, Element> elements, List<String> closeCommands) {
        this.player = player;
        this.basicView.property(InventoryTitle.of(Text.of(name)));
        this.basicView.property(new InventoryDimension(9, height));
        this.basicView.onClose(action -> this.handleClose(action, closeCommands));
        this.view = basicView.build(MenuPlugin.getInstance().getContainer());
        this.placeElements(elements);
        this.view.open(player);
    }

    private void handleClose(Action<InteractInventoryEvent.Close> closeAction, List<String> commands) {
        Sponge.getScheduler().createTaskBuilder().execute(() -> {
            if(closeAction.getPlayer().getOpenInventory().isPresent() && closeAction.getPlayer().getOpenInventory().get() instanceof ContainerChest) {
                return;
            }

            for (String command : commands) {
                command = command.replace("%player%", this.player.getName());

                if (command.startsWith("console:")) {
                    command = command.split("console:")[1];

                    Sponge.getCommandManager().process(Sponge.getServer().getConsole(), command);
                } else {
                    command = command.split("player:")[1];

                    Sponge.getCommandManager().process(this.player, command);
                }
            }
        }).delayTicks(5).submit(MenuPlugin.getInstance().getContainer());
    }

    private void placeElements(Map<Integer, Element> elements) {
        for (Map.Entry<Integer, Element> integerElementEntry : elements.entrySet()) {
            this.view.setElement(integerElementEntry.getKey(), integerElementEntry.getValue());
        }
    }
}
