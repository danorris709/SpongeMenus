package me.dnorris.sponge.menu.data;

import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;
import me.dnorris.sponge.menu.ui.GenericUI;
import com.mcsimonflash.sponge.teslalibs.inventory.Element;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.item.EnchantmentData;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.enchantment.Enchantment;
import org.spongepowered.api.item.enchantment.EnchantmentTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Menu {

    @SuppressWarnings("UnstableApiUsage")
    private static final TypeToken<String> STRING_TYPE_TOKEN = TypeToken.of(String.class);

    private final String fileIdentifier;

    private MenuConfig config;
    private String identifier;
    private String name;
    private int height;
    private List<String> closeCommands;
    private Map<Integer, Element> items;

    public Menu(String fileIdentifier) {
        this.fileIdentifier = fileIdentifier;
        this.reloadConfig();
        this.loadItems();
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public void reloadConfig() {
        this.config = new MenuConfig(fileIdentifier + ".conf");
    }

    public void loadItems() {
        this.identifier = this.config.getNode().getNode("inventory", "identifier").getString();
        this.name = this.config.getNode().getNode("inventory", "name").getString();
        this.height = this.config.getNode().getNode("inventory", "height").getInt();
        this.closeCommands = this.getCloseCommands();
        this.items = Maps.newHashMap();

        for (CommentedConfigurationNode value : this.config.getNode().getNode("inventory", "items").getChildrenMap().values()) {
            int position = value.getNode("position").getInt(1);
            ItemType itemType = Sponge.getRegistry().getType(ItemType.class, value.getNode("type").getString("minecraft:dirt")).orElse(ItemTypes.DIRT);
            int amount = value.getNode("amount").getInt(1);
            String name = value.getNode("name").getString();
            boolean glow = value.getNode("glow").getBoolean();
            int damage = value.getNode("damage").getInt();
            List<String> lore = this.getStringList(value, "lore");
            List<String> commands = this.getStringList(value, "commands");

            ItemStack itemStack = ItemStack.builder()
                    .itemType(itemType)
                    .quantity(amount)
                    .add(Keys.DISPLAY_NAME, Text.of(name))
                    .add(Keys.ITEM_LORE, lore.stream().map(Text::of).collect(Collectors.toList()))
                    .add(Keys.HIDE_ENCHANTMENTS, glow)
                    .build();

            if (glow) {
                EnchantmentData enchantmentData = itemStack.getOrCreate(EnchantmentData.class).get();
                enchantmentData.set(enchantmentData.enchantments().add(Enchantment.of(EnchantmentTypes.AQUA_AFFINITY, 1)));
                itemStack.offer(enchantmentData);
            }

            DataView containers = itemStack.toContainer();
            containers.set(DataQuery.of("UnsafeDamage"), damage);
            itemStack.setRawData(containers);

            if (value.getNode("nbt") != null) {
                for (CommentedConfigurationNode nbt : value.getNode("nbt").getChildrenMap().values()) {
                    String key = (String) nbt.getKey();
                    DataView container = itemStack.toContainer();
                    container.set(DataQuery.of("UnsafeData", key), nbt.getValue());
                    itemStack.setRawData(container);
                }
            }

            this.items.put(position, Element.of(itemStack, click -> {
                for (String command : commands) {
                    if(command.equalsIgnoreCase("%close%")) {
                        click.getPlayer().closeInventory();
                        continue;
                    }

                    if (command.isEmpty() || !(command.contains("player:") || command.contains("console:"))) {
                        continue;
                    }

                    command = command.replace("%player%", click.getPlayer().getName());

                    if (command.startsWith("console:")) {
                        command = command.split("console:")[1];

                        Sponge.getCommandManager().process(Sponge.getServer().getConsole(), command);
                    } else {
                        command = command.split("player:")[1];

                        Sponge.getCommandManager().process(click.getPlayer(), command);
                    }
                }
            }));
        }
    }

    private List<String> getCloseCommands() {
        try {
            return this.config.getNode().getNode("inventory", "close-commands").getList(STRING_TYPE_TOKEN);
        } catch (ObjectMappingException e) {
            e.printStackTrace();
        }

        return Collections.emptyList();
    }

    private List<String> getStringList(CommentedConfigurationNode node, Object... parent) {
        try {
            return node.getNode(parent).getList(STRING_TYPE_TOKEN);
        } catch (ObjectMappingException e) {
            e.printStackTrace();
        }

        return Collections.emptyList();
    }

    public void open(Player player) {
        new GenericUI(player, this.name, this.height, this.items, this.closeCommands);
    }
}
