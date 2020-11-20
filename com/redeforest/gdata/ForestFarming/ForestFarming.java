package com.redeforest.gdata.forestfarming;

import com.redeforest.gdata.forestfarming.listeners.BasicListeners;
import com.redeforest.gdata.forestfarming.listeners.CommandListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class ForestFarming extends JavaPlugin {
  // Eventos privados
  private static ForestFarming plugin;
  private static Economy econ = null;
  
  // Eventos publicos
  public static Map<String, Double> crops = new HashMap<>();
  public static List<Location> locs = new ArrayList<>();
  public static Map<String, List<Integer>> playerdata = new HashMap<>();
  public static Map<Player, Double> boosters = new HashMap<>();

  public void onEnable() {
    plugin = this;
    saveDefaultConfig();
    final ConfigurationSection category = getConfig().getConfigurationSection("heads");
    for (String id : getConfig().getConfigurationSection("heads").getKeys(false)) {
      if (id.equals("undefined"))
        continue; 
      final double x = category.getConfigurationSection(id).getDouble("x");
      final double y = category.getConfigurationSection(id).getDouble("y");
      final double z = category.getConfigurationSection(id).getDouble("z");
      final World world = Bukkit.getWorld(category.getConfigurationSection(id).getString("mundo"));
      if (world == null) {
        (new BukkitRunnable() {
            public void run() {
              World mundo = Bukkit.getWorld(category.getConfigurationSection(id).getString("mundo"));
              if (mundo != null) {
                ForestFarming.locs.add(new Location(world, x, y, z));
                cancel();
                return;
              } 
            }
          }).runTaskTimer((Plugin)this, 0L, 40L);
        return;
      } 
      locs.add(new Location(world, x, y, z));
    } 
    ConfigurationSection playerdataCs = getConfig().getConfigurationSection("playerdata");
    for (String player : playerdataCs.getKeys(false))
      playerdata.put(player, playerdataCs.getIntegerList(player)); 
    ConfigurationSection cropsCategory = getConfig().getConfigurationSection("crops");
    for (String crop : cropsCategory.getKeys(false))
      crops.put(crop, Double.valueOf(cropsCategory.getConfigurationSection(crop).getDouble("money"))); 
    Bukkit.getPluginManager().registerEvents((Listener)new BasicListeners(), (Plugin)this);
    Bukkit.getPluginManager().registerEvents((Listener)new CommandListener(), (Plugin)this);
    getCommand("givefarmbooster").setExecutor((CommandExecutor)new CommandListener());
    setupEconomy();
    (new BukkitRunnable() {
        public void run() {
          for (Location loca : ForestFarming.locs) {
            Location loc = new Location(loca.getWorld(), loca.getX() + 0.5D, loca.getY(), loca.getZ() + 0.5D);
            loc.getWorld().spigot().playEffect(loc, Effect.HAPPY_VILLAGER, 0, 0, 0.5F, 0.5F, 0.5F, 0.01F, 5, 20);
          } 
        }
      }).runTaskTimer((Plugin)this, 20L, 10L);
  }
  
  public void onDisable() {
    reloadConfig();
    getConfig().set("heads", null);
    getConfig().set("playerdata", null);
    int y = 0;
    ConfigurationSection headsCCs = getConfig().getConfigurationSection("heads");
    for (Location loc : locs) {
      if (headsCCs == null)
        headsCCs = getConfig().createSection("heads"); 
      ConfigurationSection cs = headsCCs.createSection((new StringBuilder(String.valueOf(y))).toString());
      cs.set("x", Double.valueOf(loc.getX()));
      cs.set("y", Double.valueOf(loc.getY()));
      cs.set("z", Double.valueOf(loc.getZ()));
      cs.set("mundo", loc.getWorld().getName());
      y++;
    } 
    ConfigurationSection playerdataCs = getConfig().getConfigurationSection("playerdata");
    if (playerdataCs == null)
      playerdataCs = getConfig().createSection("playerdata"); 
    for (String player : playerdata.keySet())
      playerdataCs.set(player, playerdata.get(player)); 
    saveConfig();
  }
  
  public static ForestFarming getPlugin() {
    return plugin;
  }
  
  private boolean setupEconomy() {
    if (getServer().getPluginManager().getPlugin("Vault") == null)
      return false; 
    RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
    if (rsp == null)
      return false; 
    econ = (Economy)rsp.getProvider();
    return (econ != null);
  }
  
  public static Economy getEconomy() {
    return econ;
  }
}
