package net.swagserv.andrew2060.toolhandler.listeners.mods;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.herocraftonline.heroes.util.Util;

import net.swagserv.andrew2060.toolhandler.ToolHandlerPlugin;

public class ModCombinerListener implements Listener {
    //Define slots for individual features
    private int[] inputSlots;
    private ArrayList<Integer> inputSlotList;  
    private int[] soulGemSlots;
    private int[] greenWoolSlots;
    private int[] redWoolSlots;
    private int[] whiteWoolSlots;
    private int[] expBottleSlots;
    private int[] toolSlots;
    private int scrollSlot;
    private int bookSlot;
    private int modSignSlot;
    private int modSlotSignSlot;
    private int scrollSignSlot;
    private int gemCombinerSignSlot;
	private ToolHandlerPlugin plugin;
	private HashMap<Block, Inventory> activeModChests;
	public ModCombinerListener(ToolHandlerPlugin toolHandlerPlugin) {
		this.plugin = toolHandlerPlugin;
		this.activeModChests = new HashMap<Block,Inventory>();
		this.inputSlots = new int[] {11,12,15,16,17,38,39,42,43,44}; 
		this.inputSlotList = new ArrayList<Integer>();
		for(int i : inputSlots) {
		    inputSlotList.add(i);
		}
		this.soulGemSlots = new int[] {2,6,33,34};
		this.greenWoolSlots = new int[] {20,24,47,51};
		this.redWoolSlots = new int[] {21,26,48,53};
		this.whiteWoolSlots = new int[] {25,52};
		this.expBottleSlots = new int[] {8,35};
		this.toolSlots = new int[] {3,7};
		this.scrollSlot = 29;
		this.bookSlot = 30;
		this.modSignSlot = 10;
		this.modSlotSignSlot = 14;
		this.scrollSignSlot = 37;
		this.gemCombinerSignSlot = 41;
	}
	
	@SuppressWarnings("deprecation")   //Not much we can do, Bukkit requires this
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onInventoryInteract(InventoryClickEvent event) {
        Inventory inv = event.getInventory();
        if(inv.getHolder() instanceof ArtificierInventoryHolder) {
            if(!inputSlotList.contains(event.getSlot())) {
                if(event.getRawSlot() < 54) {
                    event.setCancelled(true);
                    Player p = (Player) event.getWhoClicked();
                    ((Player)event.getWhoClicked()).updateInventory();
                    switch(event.getSlot()) {
                    
                        case 20: {  //Mod Installer Slot
                            ItemStack soulGem = inv.getItem(11);
                            ItemStack item = inv.getItem(12);
                            //Validate
                            if(!soulGem.getType().equals(Material.EMERALD)) {
                                p.sendMessage(ChatColor.GRAY + "The item in the soul gem slot is not a soul gem!");
                                break;
                            }
                            if(!soulGem.getItemMeta().hasDisplayName()) {
                                p.sendMessage(ChatColor.GRAY + "The soul gem in the soul gem slot is uncharged!");
                                break;
                            }
                            if(!(Util.isArmor(item.getType()) || Util.isWeapon(item.getType()))) {
                                p.sendMessage(ChatColor.GRAY + "This is not a valid item type to attempt to install a mod to!");
                                break;
                            }
                            //Combine
                            int code = addMod(item,soulGem);
                            if(code == 1) {
                            if(soulGem.getAmount() > 1) {
                                soulGem.setAmount(soulGem.getAmount()-1);
                            } else {
                                inv.setItem(11, new ItemStack(Material.AIR));
                            }
                            p.sendMessage(ChatColor.GRAY + "Item Modification Successful");
                            //Error Handling
                            } else if(code == 0) {
                                p.sendMessage(ChatColor.GRAY + "There are no open mod slots on this tool");
                                break;
                            } else if(code == -1) { //Should Never be Called
                                p.sendMessage(ChatColor.GRAY + "This is not a valid item type to attempt to install a mod to!");
                                break;
                            } else if(code == -2) { //Should Never be Called
                                p.sendMessage(ChatColor.GRAY + "There can only be one tool in the item mod creator!");
                                break;
                            }
                            break;
                        }
                        case 24: {  //Mod Slot Creator
                            ItemStack soulGem = inv.getItem(15);
                            ItemStack item = inv.getItem(16);
                            ItemStack essenceOfEnchanting = inv.getItem(17);
                            
                            //Validate
                            if(!soulGem.getType().equals(Material.EMERALD)) {
                                p.sendMessage(ChatColor.GRAY + "The item in the soul gem slot is not a soul gem!");
                                break;
                            }
                            if(!soulGem.getItemMeta().hasDisplayName()) {
                                p.sendMessage(ChatColor.GRAY + "The soul gem in the soul gem slot is uncharged!");
                                break;
                            }
                            if(!(Util.isArmor(item.getType()) || Util.isWeapon(item.getType()))) {
                                p.sendMessage(ChatColor.GRAY + "This is not a valid item type to attempt to install a mod to!");
                                break;
                            }
                            if(!essenceOfEnchanting.getType().equals(Material.EXP_BOTTLE)) {
                                p.sendMessage(ChatColor.GRAY + "The item in the essence of enchanting slot is not an essence of enchanting!");
                                break;
                            }
                            if(!(essenceOfEnchanting.getAmount() == 64)) {
                                p.sendMessage(ChatColor.GRAY + "Creating new mod slots require 64 essence of enchanting bottles!");
                                break;
                            }
                            break;
                        }
                        case 51: {  //Soul Gem Combiner
                            break;
                        }
                        
                    }
                }
                return;
            }
        }
    }
	@EventHandler(priority=EventPriority.LOWEST, ignoreCancelled = true)
	public void onPlayerInteractModCombiner(PlayerInteractEvent event) {
		if(!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			return;
		}
		Block b = event.getClickedBlock();
		if(!(b.getType().equals(Material.ENDER_CHEST))) {
			return;
		}
		event.setCancelled(true);
		Player p = event.getPlayer();
		if(activeModChests.containsKey(b)) {
			p.openInventory(activeModChests.get(b));
		} else {
			Inventory inv = constructModCombinerInventory();
			activeModChests.put(b, inv);
			p.openInventory(inv);
		}
		
	}
	public HashMap<Block, Inventory> getActiveModChests() {
		return this.activeModChests;
	}
	public int addMod(ItemStack tool, ItemStack soulGem) {
		String name = ChatColor.stripColor(soulGem.getItemMeta().getDisplayName());
		name = name.toLowerCase();
		int weight = 1;
		if(name.contains("weak")) {
			weight = 2;
		} else if(name.contains("common")) {
			weight = 3;
		} else if(name.contains("strong")) {
			weight = 4;
		} else if(name.contains("major")) {
			weight = 5;
		} else if(name.contains("master")) {
			weight = 6;
		} else if(name.contains("legendary")) {
			weight = 7;
		}
		return plugin.getModManager().addMod(tool, weight);
	}
	private Inventory constructModCombinerInventory() {
        Inventory inv = Bukkit.createInventory(new ArtificierInventoryHolder(), 54, "Artificier Table");
        
        //Fill Inventory with sticks w/ no name
        ItemStack stick = new ItemStack(Material.STICK);
        ItemMeta stickMeta = stick.getItemMeta();
        stickMeta.setDisplayName("");
        stick.setItemMeta(stickMeta);
        for(int i = 1; i<=inv.getSize() ; i++) {
            inv.setItem(i, stick);
        }
        //Insert Empty Spaces/Input Slots
        for(int i : inputSlots) {
            inv.setItem(i, null);
        }
        //Populate Soul Gem Item Slots
        ItemStack soulGem = new ItemStack(Material.EMERALD);
        ItemMeta soulGemMeta = soulGem.getItemMeta();
        soulGemMeta.setDisplayName("Soul Gem");
        LinkedList<String> soulGemLore = new LinkedList<String>();
        soulGemLore.add(ChatColor.GRAY + "Insert A Soul Gem in the Item Slot Below");
        soulGemMeta.setLore(soulGemLore);
        soulGem.setItemMeta(soulGemMeta);
        for(int i : soulGemSlots) {
            inv.setItem(i, soulGem);
        }
        //Populate Wool Item Slots
        ItemStack greenWool = new ItemStack(Material.WOOL,1,(short) 5);
        ItemStack redWool = new ItemStack(Material.WOOL,1,(short) 14);
        ItemStack whiteWool = new ItemStack(Material.WOOL,1,(short) 0);
        ItemMeta greenMeta = greenWool.getItemMeta();
        ItemMeta redMeta = redWool.getItemMeta();
        ItemMeta whiteMeta = whiteWool.getItemMeta();
        greenMeta.setDisplayName("Combine");
        redMeta.setDisplayName("Cancel");
        whiteMeta.setDisplayName("");
        greenWool.setItemMeta(greenMeta);
        redWool.setItemMeta(redMeta);
        whiteWool.setItemMeta(whiteMeta);
        for(int i : greenWoolSlots) {
            inv.setItem(i,greenWool);
        }
        for(int i : redWoolSlots) {
            inv.setItem(i,redWool);
        }
        for(int i : whiteWoolSlots) {
            inv.setItem(i, whiteWool);
        }
        //Populate Essence of Enchanting Slots
        ItemStack expBottle = new ItemStack(Material.EXP_BOTTLE,64);
        ItemMeta expBottleMeta = expBottle.getItemMeta();
        expBottleMeta.setDisplayName("Essence of Enchantment");
        LinkedList<String> expBottleLore = new LinkedList<String>();
        expBottleLore.add(ChatColor.GRAY + "Insert a stack of Essence of Enchantment Bottles below");
        expBottleMeta.setLore(expBottleLore);
        for(int i : expBottleSlots) {
            inv.setItem(i, expBottle);
        }
        //Tools
        ItemStack tool = new ItemStack(Material.DIAMOND_PICKAXE);
        ItemMeta toolMeta = tool.getItemMeta();
        toolMeta.setDisplayName("Tool/Armor Piece");
        LinkedList<String> toolLore = new LinkedList<String>();
        toolLore.add(ChatColor.GRAY + "Insert a tool/armor piece below below");
        toolMeta.setLore(toolLore);
        for(int i : toolSlots) {
            inv.setItem(i, tool);
        }
        //Mod Installer Sign
        ItemStack modSign = new ItemStack(Material.SIGN);
        ItemMeta modSignMeta = modSign.getItemMeta();
        modSignMeta.setDisplayName(ChatColor.RESET + "" + ChatColor.WHITE + "Mod Installer");
        LinkedList<String> modSignLore = new LinkedList<String>();
        modSignLore.add(ChatColor.GRAY + "Install new mods onto an item with empty mod slots here");
        modSignLore.add(ChatColor.GRAY + "by combining a soul gem.");
        modSignLore.add(ChatColor.GRAY + "The greater the power of the soul gem, ");
        modSignLore.add(ChatColor.GRAY + "The greater the chances of getting a rare mod.");
        modSignMeta.setLore(modSignLore);
        modSign.setItemMeta(modSignMeta);
        inv.setItem(modSignSlot,modSign);
        //Mod Slot Sign
        ItemStack modSlotCreatorSign = new ItemStack(Material.SIGN);
        ItemMeta modSlotCreatorMeta = modSlotCreatorSign.getItemMeta();
        modSlotCreatorMeta.setDisplayName(ChatColor.RESET + "" + ChatColor.WHITE + "Mod Slot Installer");
        LinkedList<String> modSlotCreatorLore = new LinkedList<String>();
        modSlotCreatorLore.add(ChatColor.GRAY + "Add new mod slots to an item here!");
        modSlotCreatorLore.add(ChatColor.GRAY + "Note that the more mod slots a tool/armor piece has,");
        modSlotCreatorLore.add(ChatColor.GRAY + "the greater the chances of it breaking on upgrade!");
        modSlotCreatorMeta.setLore(modSlotCreatorLore);
        modSlotCreatorSign.setItemMeta(modSlotCreatorMeta);
        inv.setItem(modSlotSignSlot, modSlotCreatorSign);
        //Soul Gem Combiner Sign
        ItemStack soulGemCombinerSign = new ItemStack(Material.SIGN);
        ItemMeta soulGemCombinerMeta = soulGemCombinerSign.getItemMeta();
        soulGemCombinerMeta.setDisplayName(ChatColor.RESET + "" + ChatColor.WHITE + "Soul Gem Combiner");
        LinkedList<String> soulGemCombinerLore = new LinkedList<String>();
        soulGemCombinerLore.add(ChatColor.GRAY + "Combine two soul gems of the same power level here");
        soulGemCombinerLore.add(ChatColor.GRAY + "to upgrade to the next tier.");
        soulGemCombinerLore.add(ChatColor.GRAY + "The higher your enchanter level the more likely you are to succeed!");
        soulGemCombinerMeta.setLore(soulGemCombinerLore);
        soulGemCombinerSign.setItemMeta(soulGemCombinerMeta);
        inv.setItem(gemCombinerSignSlot, soulGemCombinerSign);
        //Temporary Fillers
        inv.setItem(bookSlot,new ItemStack(Material.BOOK));
        inv.setItem(scrollSlot, new ItemStack(Material.MAP));
        inv.setItem(scrollSignSlot, new ItemStack(Material.SIGN));
	    return inv;
	}
	private class ArtificierInventoryHolder implements InventoryHolder {

	    //Should not be called
        @Override
        public Inventory getInventory() {
            return null;
        }
	    
	}
}
