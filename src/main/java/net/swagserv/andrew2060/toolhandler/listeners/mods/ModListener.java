package net.swagserv.andrew2060.toolhandler.listeners.mods;

import java.util.List;

import net.swagserv.andrew2060.toolhandler.ToolHandlerPlugin;
import net.swagserv.andrew2060.toolhandler.mods.ModManager;
import net.swagserv.andrew2060.toolhandler.mods.typedefs.ArmorMod;
import net.swagserv.andrew2060.toolhandler.mods.typedefs.ScytheMod;
import net.swagserv.andrew2060.toolhandler.mods.typedefs.ToolMod;
import net.swagserv.andrew2060.toolhandler.mods.typedefs.WeaponMod;
import net.swagserv.andrew2060.toolhandler.util.ModUtil;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.herocraftonline.heroes.api.events.SkillDamageEvent;
import com.herocraftonline.heroes.api.events.SkillUseEvent;
import com.herocraftonline.heroes.api.events.WeaponDamageEvent;
import com.herocraftonline.heroes.characters.Hero;

public class ModListener implements Listener {
	private ToolHandlerPlugin plugin;

	public ModListener(ToolHandlerPlugin plugin) {
		this.plugin = plugin;
	}
	
	//Event Handler for Outgoing Weapon Damage (Weapon/Tool Mods)
	@EventHandler(ignoreCancelled = true,priority = EventPriority.MONITOR)
	public void onWeaponDamageOutgoing(WeaponDamageEvent event) {
		if(!(event.getDamager() instanceof Hero)) {
			return;
		}
		Player p = ((Hero) event.getDamager()).getPlayer();
		ItemStack i = p.getItemInHand();
		switch(i.getType()) {
			case DIAMOND_SWORD:	
			case IRON_SWORD: 
			case GOLD_SWORD: 
			case STONE_SWORD: 
			case WOOD_SWORD:
			case BOW: {
				List<String> mods = ModUtil.getWeaponMods(i);
				ModManager modManager = plugin.getModManager();
				for(int x = 0; x < mods.size(); x++) {
					WeaponMod mod = modManager.getWeaponMod(mods.get(x));
					if(mod != null) {
						try {
							mod.tickMod(event);
						} catch (Exception e) {
							System.out.println("Error in Weapon Mod " + mod.getName());
							e.printStackTrace();
						}
					}
				}
				return;
			}
			case DIAMOND_PICKAXE:
			case DIAMOND_AXE:
			case DIAMOND_SPADE:
			case IRON_PICKAXE:
			case IRON_AXE:
			case IRON_SPADE:
			case GOLD_PICKAXE:
			case GOLD_AXE:
			case GOLD_SPADE:
			case STONE_PICKAXE:				
			case STONE_AXE:
			case STONE_SPADE:
			case WOOD_PICKAXE:
			case WOOD_AXE:
			case WOOD_SPADE: {
				List<String> mods = ModUtil.getToolMods(i);
				ModManager modManager = plugin.getModManager();
				for(int x = 0; x < mods.size(); x++) {
					ToolMod mod = modManager.getToolMod(mods.get(x));
					if(mod != null) {
						mod.executeOnWeaponDamage(event);
					}
				}
				return;
			}
			default: {
				return;
			}
		}
		
	}
	//Event Handler for Incoming Damage (Armor Mods)
	@EventHandler(ignoreCancelled = true,priority = EventPriority.MONITOR)
	public void onWeaponDamageIncoming(WeaponDamageEvent event) {
		if(!(event.getEntity() instanceof Player)) {
			return;
		}
		Player p = (Player) event.getEntity();
		boolean helmetcheck = true;
		boolean chestcheck = true;
		boolean legscheck = true;
		boolean bootscheck = true;
		PlayerInventory inv = p.getInventory();
		ItemStack helmet = inv.getHelmet();
		if(helmet == null) {
			helmetcheck = false;
		}
		ItemStack chest = inv.getChestplate();
		if(chest == null) {
			chestcheck = false;
		}
		ItemStack legs = inv.getLeggings();
		if(legs == null) {
			legscheck = false;
		} 
		ItemStack boots = inv.getBoots();
		if(boots == null) {
			bootscheck = false;
		}
		if(helmetcheck) {
			List<String> mods = ModUtil.getArmorMods(helmet);
			ModManager modManager = plugin.getModManager();
			for(int x = 0; x < mods.size(); x++) {
				ArmorMod mod = modManager.getArmorMod(mods.get(x));
				if(mod != null) {
					mod.executeOnArmorDamage(event);
				}
			}		
		}
		if(chestcheck) {
			List<String> mods = ModUtil.getArmorMods(chest);
			ModManager modManager = plugin.getModManager();
			for(int x = 0; x < mods.size(); x++) {
				ArmorMod mod = modManager.getArmorMod(mods.get(x));
				if(mod != null) {
					mod.executeOnArmorDamage(event);
				}
			}	
		}
		if(legscheck) {
			List<String> mods = ModUtil.getArmorMods(legs);
			ModManager modManager = plugin.getModManager();
			for(int x = 0; x < mods.size(); x++) {
				ArmorMod mod = modManager.getArmorMod(mods.get(x));
				if(mod != null) {
					mod.executeOnArmorDamage(event);
				}
			}	
		}
		if(bootscheck) {
			List<String> mods = ModUtil.getArmorMods(boots);
			ModManager modManager = plugin.getModManager();
			for(int x = 0; x < mods.size(); x++) {
				ArmorMod mod = modManager.getArmorMod(mods.get(x));
				if(mod != null) {
					mod.executeOnArmorDamage(event);
				}
			}	
		}
		
	}
	//Event Handler for Block Breaking (Tool Mods)
	@EventHandler(ignoreCancelled = true,priority = EventPriority.MONITOR)
	public void onBlockBreak(BlockBreakEvent event) {
		Player p = event.getPlayer();
		ItemStack i = p.getItemInHand();
		switch(i.getType()) {
			case DIAMOND_PICKAXE:
			case DIAMOND_AXE:
			case DIAMOND_SPADE:
			case IRON_PICKAXE:
			case IRON_AXE:
			case IRON_SPADE:
			case GOLD_PICKAXE:
			case GOLD_AXE:
			case GOLD_SPADE:
			case STONE_PICKAXE:				
			case STONE_AXE:
			case STONE_SPADE:
			case WOOD_PICKAXE:
			case WOOD_AXE:
			case WOOD_SPADE: {
				List<String> mods = ModUtil.getToolMods(i);
				ModManager modManager = plugin.getModManager();
				for(int x = 0; x < mods.size(); x++) {
					ToolMod mod = modManager.getToolMod(mods.get(x));
					if(mod != null) {
						mod.executeOnBlockBreak(event);
					}
				}
				return;
			}
			default: {
				return;
			}
		}
		
	}
	//Event Handlers for Skill Use and Skill Damage
	@EventHandler(ignoreCancelled = true,priority = EventPriority.MONITOR)
	public void onSkillUse(SkillUseEvent event) {
		ItemStack i = event.getHero().getPlayer().getItemInHand();
		switch(i.getType()) {
			case DIAMOND_HOE:
			case IRON_HOE:
			case GOLD_HOE:
			case STONE_HOE:
			case WOOD_HOE: {
				List<String> mods = ModUtil.getToolMods(i);
				ModManager modManager = plugin.getModManager();
				for(int x = 0; x < mods.size(); x++) {
					ScytheMod mod = modManager.getScytheMod(mods.get(x));
					if(mod != null) {
						mod.executeOnSkillUse(event);
					}
				}
				return;
			}
			default: {
				return;
			}
		}
	}
	@EventHandler(ignoreCancelled = true,priority = EventPriority.MONITOR)
	public void onSkillDamage(SkillDamageEvent event) {
		if(!(event.getDamager() instanceof Hero)) {
			return;
		}
		ItemStack i = ((Hero)(event.getDamager())).getPlayer().getItemInHand();
		switch(i.getType()) {
			case DIAMOND_HOE:
			case IRON_HOE:
			case GOLD_HOE:
			case STONE_HOE:
			case WOOD_HOE: {
				List<String> mods = ModUtil.getToolMods(i);
				ModManager modManager = plugin.getModManager();
				for(int x = 0; x < mods.size(); x++) {
					ScytheMod mod = modManager.getScytheMod(mods.get(x));
					if(mod != null) {
						mod.executeOnSkillDamage(event);
					}
				}
				return;
			}
			default: {
				return;
			}
		}
	}
}