package net.kingdomsofarden.andrew2060.toolhandler.util;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import net.kingdomsofarden.andrew2060.toolhandler.ToolHandlerPlugin;
import net.kingdomsofarden.andrew2060.toolhandler.cache.types.CachedScytheInfo;
import net.kingdomsofarden.andrew2060.toolhandler.mods.EmptyModSlot;
import net.kingdomsofarden.andrew2060.toolhandler.mods.typedefs.ToolMod;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ScytheLoreUtil {
    
    public static void write(CachedScytheInfo data, ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        List<String> lore = new LinkedList<String>();
        double bonusDamage = 0;
        double manaRestore = 0;
        double spellLeech = 0;
        ToolHandlerPlugin plugin = ToolHandlerPlugin.instance;
        for(UUID id : data.getMods()) {
            ToolMod mod = plugin.getModManager().getToolMod(id);
            bonusDamage += mod.getTrueDamage();
        }
        lore.add(0,ToolHandlerPlugin.versionIdentifier + ChatColor.WHITE + "=======Item Statistics=======");
        lore.add(1,ChatColor.GRAY + "Improvement Quality: " + FormattingUtil.getWeaponToolQualityFormat(data.getQuality()));
        lore.add(2,ChatColor.GRAY + "Damage Boost Rating: " + FormattingUtil.getAttribute(bonusDamage) + "%");
        lore.add(3,ChatColor.GRAY + "Mana Restoration: " + FormattingUtil.getAttribute(manaRestore) + "%");
        lore.add(4,ChatColor.GRAY + "Spell Leech: " + FormattingUtil.getAttribute(spellLeech) + "%");
        lore.add(5,ChatColor.WHITE + "========Modifications========");
        int usedSlots = 0;
        int addedBlankSlots = 0;
        int baseBlankSlots = 0;
        for(UUID id : data.getMods()) {
            ToolMod mod = plugin.getModManager().getToolMod(id);
            if(mod == null) {
                if(id.equals(EmptyModSlot.bonusId)) {
                    addedBlankSlots++;
                    continue;
                }
                if(id.equals(EmptyModSlot.baseId)) {
                    baseBlankSlots++;
                }
                continue;
            }
            if(usedSlots > 1 || !mod.isSlotRequired()) {
                lore.add(ChatColor.MAGIC + "" + ChatColor.RESET + "" + ChatColor.GOLD + mod.getName());
            } else {
                lore.add(ChatColor.GOLD + mod.getName());
            }
            if(mod.getTrueDamage() != null && mod.getTrueDamage() > Double.valueOf(0.00)) {
                lore.add(ChatColor.GRAY + "- " 
                        + FormattingUtil.getAttributeColor(mod.getTrueDamage()) 
                        + FormattingUtil.modDescriptorFormat.format(mod.getTrueDamage())
                        + ChatColor.GRAY + " True Damage");
            }
            if(mod.getBashChance() != null && mod.getBashChance() > Double.valueOf(0.00)) {
                lore.add(ChatColor.GRAY + "- " 
                        + FormattingUtil.getAttributeColor(mod.getBashChance()) 
                        + FormattingUtil.modDescriptorFormat.format(mod.getBashChance())
                        + ChatColor.GRAY + "% Bash Attack Chance");
            }
            if(mod.getDecimateChance() != null && mod.getDecimateChance() > Double.valueOf(0.00)) {
                lore.add(ChatColor.GRAY + "- " 
                        + FormattingUtil.getAttributeColor(mod.getDecimateChance()) 
                        + FormattingUtil.modDescriptorFormat.format(mod.getDecimateChance())
                        + ChatColor.GRAY + "% Decimating Attack Chance");
            }
            for(String s : mod.getDescription()) {
                lore.add(ChatColor.GRAY + "- " + s);
            }
            if(mod.isSlotRequired()) {
                usedSlots++; 
                continue;
            } else {
                continue;
            }
        }
        for(int i = 0; i < baseBlankSlots; i++) {
            lore.add(EmptyModSlot.baseDesc);
        }
        for(int i = 0; i < addedBlankSlots; i++) {
            lore.add(EmptyModSlot.bonusDesc);
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
    }
}
