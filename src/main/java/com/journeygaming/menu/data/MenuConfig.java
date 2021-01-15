package com.journeygaming.menu.data;

import com.google.common.collect.Lists;
import com.journeygaming.lib.config.Configurable;
import org.spongepowered.api.item.ItemTypes;

public class MenuConfig extends Configurable {

    public static final String PATH = "menus";

    public MenuConfig(String name) {
        super(PATH + "//" + name);
    }

    @Override
    public void populate() {
        this.getNode().getNode("inventory", "identifier").setValue("example_menu");
        this.getNode().getNode("inventory", "name").setValue("Example");
        this.getNode().getNode("inventory", "height").setValue(2);
        this.getNode().getNode("inventory", "close-commands").setValue(Lists.newArrayList(
                "console:msg %player% WELL DONE!",
                "console:msg %player% YOU SUCK!",
                "player:help",
                "player:pay Envyful 1"
        ));
        this.getNode().getNode("inventory", "items", "one", "position").setValue(0);
        this.getNode().getNode("inventory", "items", "one", "type").setValue(ItemTypes.ACACIA_BOAT.getName());
        this.getNode().getNode("inventory", "items", "one", "amount").setValue(1);
        this.getNode().getNode("inventory", "items", "one", "name").setValue("This is an item name! §bBLUE");
        this.getNode().getNode("inventory", "items", "one", "glow").setValue(true);
        this.getNode().getNode("inventory", "items", "one", "lore").setValue(Lists.newArrayList(
                "§bLore line 1!",
                "§bLore line 2!"
        ));
        this.getNode().getNode("inventory", "items", "one", "commands").setValue(Lists.newArrayList(
                "console:kill %player%",
                "console:ban %player% You're dumb!"
        ));
    }
}
