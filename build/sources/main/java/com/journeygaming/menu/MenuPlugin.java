package com.journeygaming.menu;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.journeygaming.lib.JourneyGaming;
import com.journeygaming.menu.command.MenuCommand;
import com.journeygaming.menu.data.Menu;
import com.journeygaming.menu.data.MenuConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;

import java.io.File;
import java.util.List;
import java.util.Map;

@Plugin(
        id = "journeymenu",
        name = "Journey Menus",
        version = "1.0.0",
        description = "Journey Menus Plugin",
        dependencies = {
                @Dependency(id = "journeygaminglib")
            }
    )
public class MenuPlugin {

    private static MenuPlugin instance;

    @Inject private PluginContainer container;

    private Map<String, Menu> menus = Maps.newHashMap();

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        instance = this;

        new MenuCommand(this);

        File file = new File(JourneyGaming.getInstance().getConfigDir() + File.separator + "JourneyGamingSponge" + File.separator + MenuConfig.PATH);

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

            String name = listFile.getPath().replace((JourneyGaming.getInstance().getConfigDir() + File.separator + "JourneyGamingSponge" + File.separator + MenuConfig.PATH + File.separator), "").replace(".conf", "");
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
