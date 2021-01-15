package me.dnorris.sponge.menu;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import me.dnorris.sponge.menu.command.MenuCommand;
import me.dnorris.sponge.menu.data.Menu;
import me.dnorris.sponge.menu.data.MenuConfig;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@Plugin(
        id = "spongemenus",
        name = "Sponge Menus",
        version = "1.0.0",
        description = "Sponge Menus Plugin"
)
public class MenuPlugin {

    private static MenuPlugin instance;

    @Inject private PluginContainer container;
    @ConfigDir(sharedRoot = false) private Path configDir;

    private Map<String, Menu> menus = Maps.newHashMap();

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        instance = this;

        new MenuCommand(this);

        File file = new File(this.configDir + File.separator + MenuConfig.PATH);

        if(!file.exists()) {
            file.mkdir();
        }

        handleDirectory(file);
    }

    public void handleDirectory(File file) {
        if(file.listFiles() == null) {
            return;
        }

        for (File listFile : file.listFiles()) {
            if(listFile.isDirectory()) {
                handleDirectory(listFile);
                continue;
            }

            if(!listFile.getName().endsWith(".conf")) {
                continue;
            }

            String name = listFile.getPath().replace((this.configDir + File.separator + MenuConfig.PATH + File.separator), "").replace(".conf", "");
            Menu menu = new Menu(name);

            this.addMenu(menu);
        }
    }

    public void addMenu(Menu menu) {
        this.menus.put(menu.getIdentifier().toLowerCase(), menu);
    }

    public static MenuPlugin getInstance() {
        return instance;
    }

    public Path getConfigDir() {
        return this.configDir;
    }

    public PluginContainer getContainer() {
        return this.container;
    }

    public List<String> getMenuNames() {
        return Lists.newArrayList(this.menus.keySet());
    }

    public Menu getMenu(String name) {
        return this.menus.get(name.toLowerCase());
    }
}
