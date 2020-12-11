package com.redeforest.gdata.forestfarming.listeners;

import com.redeforest.gdata.forestfarming.ForestFarming;
import com.redeforest.gdata.forestfarming.utils.ActionBar;
import com.redeforest.gdata.forestfarming.utils.Formatter;
import com.redeforest.gdata.forestfarming.utils.ItemBuilder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

public class BasicListeners implements Listener {
  @EventHandler(ignoreCancelled = false)
  public void onBreak(BlockBreakEvent e) {
    Player p = e.getPlayer();
    if (p == null)
      return; 
    if (!p.getWorld().getName().equals("Spawn"))
      return; 
    Block block = e.getBlock();
    if (ForestFarming.crops.containsKey((new StringBuilder(String.valueOf(block.getType().getId()))).toString())) {
      double booster = 1.0D;
      if (ForestFarming.boosters.containsKey(p))
        booster = ((Double)ForestFarming.boosters.get(p)).doubleValue(); 
      if (block.getType().equals(Material.POTATO) || block.getType().equals(Material.CARROT) || block.getType().equals(Material.WHEAT) || block.getType().equals(Material.CROPS)) {
        int growthStatus = block.getData();
        if (growthStatus != 7) {
          e.setCancelled(true);
          e.getBlock().setType(e.getBlock().getType());
          return;
        } 
      } 
      if (ForestFarming.playerdata.containsKey(p.getName())) {
        int sortudo = (int)(((Integer)((List<Integer>)ForestFarming.playerdata.get(p.getName())).get(1)).intValue() * booster);
        Random rand = new Random();
        double randomNum = rand.nextDouble();
        double chance = PolarFarmPlugin.getPlugin().getConfig().getDouble("sortudo-chance");
        if (randomNum <= sortudo * chance) {
          List<String> commands = PolarFarmPlugin.getPlugin().getConfig().getStringList("commands");
          int cmdNum = rand.nextInt(commands.size() - 1 - 0 + 1) + 0;
          Bukkit.dispatchCommand((CommandSender)Bukkit.getConsoleSender(), ((String)commands.get(cmdNum)).replace("%p", p.getName()));
          p.sendMessage(ChatColor.GREEN + "§aVocê conseguiu um item raro.");
        } 
      } 
      Collection<ItemStack> drops = e.getBlock().getDrops();
      if (ForestFarming.playerdata.containsKey(p.getName())) {
        int colheitor = (int)(((Integer)((List<Integer>)ForestFarming.playerdata.get(p.getName())).get(2)).intValue() * booster);
        for (int y = 0; y < colheitor; y++)
          drops.add((new ItemBuilder(Material.WHEAT)).toItemStack()); 
      } 
      drops.add((new ItemBuilder(Material.WHEAT)).toItemStack());
      double money = drops.size() * ((Double)ForestFarming.crops.get((new StringBuilder(String.valueOf(block.getType().getId()))).toString())).doubleValue();
      if (ForestFarming.playerdata.containsKey(p.getName())) {
        int fertilizador = (int)(((Integer)((List<Integer>)ForestFarming.playerdata.get(p.getName())).get(0)).intValue() * booster);
        money *= fertilizador;
      } 
      ForestFarming.getEconomy().depositPlayer((OfflinePlayer)p, money);
      String message = ChatColor.translateAlternateColorCodes('&', "&6&lFARM &a> &eVocê ganhou " + Formatter.format(money) + " de money.");
      ActionBar.sendActionbar(p, message);
      e.setCancelled(true);
      e.getBlock().setType(e.getBlock().getType());
    } 
  }
  
  @EventHandler
  public void onPhysic(PlayerInteractEvent e) {
    if (e.getAction() == Action.PHYSICAL && 
      e.getClickedBlock().getType() == Material.SOIL)
      e.setCancelled(true); 
  }
  
  @EventHandler
  public void onInteract(PlayerInteractEvent e) {
    if (!e.getAction().equals(Action.RIGHT_CLICK_BLOCK) && !e.getAction().equals(Action.LEFT_CLICK_BLOCK))
      return; 
    Player p = e.getPlayer();
    if (ForestFarming.locs.contains(e.getClickedBlock().getLocation())) {
      if (!ForestFarming.playerdata.containsKey(p.getName())) {
        List<Integer> lista = new ArrayList<>();
        lista.add(Integer.valueOf(1));
        lista.add(Integer.valueOf(1));
        lista.add(Integer.valueOf(1));
        ForestFarming.playerdata.put(p.getName(), lista);
      } 
      int fertilizador = ((Integer)((List<Integer>)ForestFarming.playerdata.get(p.getName())).get(0)).intValue();
      int sortudo = ((Integer)((List<Integer>)ForestFarming.playerdata.get(p.getName())).get(1)).intValue();
      int colheitor = ((Integer)((List<Integer>)ForestFarming.playerdata.get(p.getName())).get(2)).intValue();
      double maxFertilizador = ForestFarming.getPlugin().getConfig().getDouble("max-fertilizador");
      double maxSortudo = ForestFarming.getPlugin().getConfig().getDouble("max-sortudo");
      double maxColheitor = ForestFarming.getPlugin().getConfig().getDouble("max-colheitor");
      String fertilizadorCost = Formatter.format(ForestFarming.getPlugin().getConfig().getDouble("fertilizador-cost") * fertilizador);
      String sortudoCost = Formatter.format(ForestFarming.getPlugin().getConfig().getDouble("sortudo-cost") * sortudo);
      String colheitorCost = Formatter.format(ForestFarming.getPlugin().getConfig().getDouble("colheitor-cost") * colheitor);
      Inventory inv = Bukkit.createInventory(null, 36, "Melhore sua enxada");
      inv.setItem(4, (new ItemBuilder(Material.DIAMOND_HOE)).setName(").setLore(new String[] { "§aOlá, fazendeiro!", "§fJá pensou em melhorar sua enxada ?", "", "Fertilizador " + fertilizador, "Sortudo " + sortudo, "Colheitor " + colheitor }).toItemStack());
      inv.setItem(20, (new ItemBuilder(Material.INK_SACK, 1, (byte)15)).setName("+ fertilizador + " > " + (fertilizador + 1)).setLore(new String[] { "este melhoramento da sua", "vocrecebermais dinheiro", "cada plantaquebrada.", "", " m+ maxFertilizador, "", " precisa de + fertilizadorCost + " dinheiro", " evoluir este encantamento!", "", "para Evoluir" }).toItemStack());
      inv.setItem(22, (new ItemBuilder(Material.NETHER_STAR)).setName("+ sortudo + " > " + (sortudo + 1)).setLore(new String[] { "este melhoramento da sua", "você te rmais chance", "conseguir itens raros.", "", " m+ maxSortudo, "", " precisa de + sortudoCost + " dinheiro", " evoluir este encantamento!", "", "para Evoluir" }).toItemStack());
      inv.setItem(24, (new ItemBuilder(Material.SHEARS)).setName("+ colheitor + " > " + (colheitor + 1)).setLore(new String[] { "este melhoramento da sua", "as plantas droparam", "itens.", "", " m+ maxColheitor, "", " precisa de + colheitorCost + " dinheiro", " evoluir este encantamento!", "", "para Evoluir" }).toItemStack());
      p.openInventory(inv);
      e.setCancelled(true);
    } 
  }
  
  @EventHandler
  public void onInventoryClick(InventoryClickEvent e) {
    List<Integer> lista;
    if (!e.getInventory().getName().equals("Melhore sua enxada"))
      return; 
    e.setCancelled(true);
    Player p = (Player)e.getWhoClicked();
    int fertilizador = ((Integer)((List<Integer>)ForestFarming.playerdata.get(p.getName())).get(0)).intValue();
    int sortudo = ((Integer)((List<Integer>)ForestFarming.playerdata.get(p.getName())).get(1)).intValue();
    int colheitor = ((Integer)((List<Integer>)ForestFarming.playerdata.get(p.getName())).get(2)).intValue();
    switch (e.getSlot()) {
      case 20:
        if (fertilizador >= ForestFarming.getPlugin().getConfig().getDouble("max-fertilizador")) {
          p.sendMessage(ChatColor.RED + "Você já chegou ao nível máximo de Fertilizador.");
          p.closeInventory();
          return;
        } 
        if (ForestFarming.getEconomy().getBalance((OfflinePlayer)p) < ForestFarming.getPlugin().getConfig().getDouble("fertilizador-cost") * fertilizador) {
          p.sendMessage(ChatColor.RED + "Você não tem dinheiro suficiente para efetuar essa compra.");
          p.closeInventory();
          return;
        } 
        ForestFarming.getEconomy().withdrawPlayer((OfflinePlayer)p, ForestFarming.getPlugin().getConfig().getDouble("fertilizador-cost") * fertilizador);
        lista = new ArrayList<>();
        lista.add(Integer.valueOf(fertilizador + 1));
        lista.add(Integer.valueOf(sortudo));
        lista.add(Integer.valueOf(colheitor));
        ForestFarming.playerdata.replace(p.getName(), lista);
        p.sendMessage("§aCompra foi efetuada com sucesso.");
        runReload(p);
        return;
      case 22:
        if (sortudo >= ForestFarming.getPlugin().getConfig().getDouble("max-sortudo")) {
          p.sendMessage(ChatColor.RED + "Você já chegou ao nível máximo de Sortudo.");
          p.closeInventory();
          return;
        } 
        if (ForestFarming.getEconomy().getBalance((OfflinePlayer)p) < ForestFarming.getPlugin().getConfig().getDouble("sortudo-cost") * sortudo) {
          p.sendMessage(ChatColor.RED + "VocÊ não tem dinheiro suficiente para efetuar essa compra.");
          p.closeInventory();
          return;
        } 
        ForestFarming.getEconomy().withdrawPlayer((OfflinePlayer)p, ForestFarming.getPlugin().getConfig().getDouble("sortudo-cost") * sortudo);
        lista = new ArrayList<>();
        lista.add(Integer.valueOf(fertilizador));
        lista.add(Integer.valueOf(sortudo + 1));
        lista.add(Integer.valueOf(colheitor));
        ForestFarming.playerdata.replace(p.getName(), lista);
        p.sendMessage("§aCompra foi efetuada com sucesso.");
        runReload(p);
        return;
      case 24:
        if (colheitor >= ForestFarming.getPlugin().getConfig().getDouble("max-colheitor")) {
          p.sendMessage(ChatColor.RED + "Você já chegou ao nível máximo de Colheitor.");
          p.closeInventory();
          return;
        } 
        if (ForestFarming.getEconomy().getBalance((OfflinePlayer)p) < ForestFarming.getPlugin().getConfig().getDouble("colheitor-cost") * colheitor) {
          p.sendMessage(ChatColor.RED + "Você não tem dinheiro suficiente para efetuar essa compra.");
          p.closeInventory();
          return;
        } 
        ForestFarming.getEconomy().withdrawPlayer((OfflinePlayer)p, ForestFarming.getPlugin().getConfig().getDouble("colheitor-cost") * colheitor);
        lista = new ArrayList<>();
        lista.add(Integer.valueOf(fertilizador));
        lista.add(Integer.valueOf(sortudo));
        lista.add(Integer.valueOf(colheitor + 1));
        ForestFarming.playerdata.replace(p.getName(), lista);
        p.sendMessage("§aCompra foi efetuada com sucesso.");
        runReload(p);
        return;
    } 
  }
  
  private void runReload(Player p) {
    int fertilizador = ((Integer)((List<Integer>)ForestFarming.playerdata.get(p.getName())).get(0)).intValue();
    int sortudo = ((Integer)((List<Integer>)ForestFarming.playerdata.get(p.getName())).get(1)).intValue();
    int colheitor = ((Integer)((List<Integer>)ForestFarming.playerdata.get(p.getName())).get(2)).intValue();
    double maxFertilizador = ForestFarming.getPlugin().getConfig().getDouble("max-fertilizador");
    double maxSortudo = ForestFarming.getPlugin().getConfig().getDouble("max-sortudo");
    double maxColheitor = ForestFarming.getPlugin().getConfig().getDouble("max-colheitor");
    String fertilizadorCost = Formatter.format(ForestFarming.getPlugin().getConfig().getDouble("fertilizador-cost") * fertilizador);
    String sortudoCost = Formatter.format(ForestFarming.getPlugin().getConfig().getDouble("sortudo-cost") * sortudo);
    String colheitorCost = Formatter.format(ForestFarming.getPlugin().getConfig().getDouble("colheitor-cost") * colheitor);
    if (!p.getOpenInventory().getTitle().equals("Melhore sua enxada"))
      return; 
    InventoryView inv = p.getOpenInventory();
    inv.setItem(4, (new ItemBuilder(Material.DIAMOND_HOE)).setName(").setLore(new String[] { "afazendeiro", "pensou em melhorar sua enxada ?", "", "Fertilizador " + fertilizador, "Sortudo " + sortudo, "Colheitor " + colheitor }).toItemStack());
    inv.setItem(20, (new ItemBuilder(Material.INK_SACK, 1, (byte)15)).setName("+ fertilizador + " > " + (fertilizador + 1)).setLore(new String[] { "este melhoramento da sua", "vocrecebermais dinheiro", "cada plantaquebrada.", "", " m+ maxFertilizador, "", " precisa de + fertilizadorCost + " dinheiro", " evoluir este encantamento!", "", "para Evoluir" }).toItemStack());
    inv.setItem(22, (new ItemBuilder(Material.NETHER_STAR)).setName("+ sortudo + " > " + (sortudo + 1)).setLore(new String[] { "este melhoramento da sua", "voctermais chance", "conseguir itens raros.", "", " m+ maxSortudo, "", " precisa de + sortudoCost + " dinheiro", " evoluir este encantamento!", "", "para Evoluir" }).toItemStack());
    inv.setItem(24, (new ItemBuilder(Material.SHEARS)).setName("+ colheitor + " > " + (colheitor + 1)).setLore(new String[] { "este melhoramento da sua", "as plantadroparam", "intens.", "", " m+ maxColheitor, "", " precisa de + colheitorCost + " dinheiro", " evoluir este encantamento!", "", "para Evoluir" }).toItemStack());
  }
}
