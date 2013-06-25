package net.swagserv.andrew2060.toolhandler.util;

import java.util.LinkedList;
import java.util.List;

import net.swagserv.andrew2060.toolhandler.ToolHandlerPlugin;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ToolLoreUtil {

    public static int getBonusTrueDamage(ItemStack tool) {
        ItemMeta meta = tool.getItemMeta();
        List<String> lore = meta.getLore();
        if(!lore.get(0).contains(ToolHandlerPlugin.versionIdentifier)) {
            GeneralLoreUtil.updateLore(tool);
            meta = tool.getItemMeta();
            lore = meta.getLore();
        }
        String intParse = lore.get(2).replaceAll("[^.0-9]", "");
        int bonus = Integer.parseInt(intParse);
        return bonus;
    }
    
    public static void updateToolLore(ItemStack tool) {
        ItemMeta meta = tool.getItemMeta();
        if(!meta.hasLore()) {
            GeneralLoreUtil.populateLoreDefaults(tool);
            return;
        }
        List<String> lore = meta.getLore();
        List<String> modifications = new LinkedList<String>();
        double improvementQuality = 0.00D;
        int bonusDamage = 0;
        double bashChance = 0.00;
        double decimateChance = 0.00;
        //Once this is true, assumes all lore past is modification lore
        boolean reachedmodifications = false;
        for(String line : lore) {
            if(reachedmodifications) {
                modifications.add(line);
            }
            if(line.contains("Quality")) {
                try {
                    improvementQuality = Double.parseDouble(line.replaceAll("[^.0-9]", ""));
                } catch (NumberFormatException e) {
                }
            }
            if(line.contains("True")) {
                try {
                    bonusDamage = Integer.parseInt(line.replaceAll("[^.0-9]", ""));
                } catch (NumberFormatException e) {
                }
            }
            if(line.contains("Bash")) {
                try {
                    bashChance = Double.parseDouble(line.replaceAll("[^.0-9]", ""));
                } catch (NumberFormatException e) {
                }
            }
            if(line.contains("Decimating")) {
                try {
                    decimateChance = Double.parseDouble(line.replaceAll("[^.0-9]", ""));
                } catch (NumberFormatException e) {
                }
            }
            if(line.contains("Modifications")) {
                reachedmodifications = true;
            }
        }
        String improvementText = FormattingUtil.getQualityColor(improvementQuality) + FormattingUtil.dF.format(improvementQuality) + ChatColor.GRAY;
        improvementText = ChatColor.GRAY + "Improvement Quality: " + improvementQuality + "%";
        String truedmgText = FormattingUtil.getAttributeColor(bonusDamage) + bonusDamage + ChatColor.GRAY;
        truedmgText = ChatColor.GRAY + "True Damage: " + truedmgText;
        String bashText = FormattingUtil.getAttributeColor(bashChance) + FormattingUtil.dF.format(bashChance) + ChatColor.GRAY;
        bashText = ChatColor.GRAY + "Bash Attack Chance: " + bashText + "%";
        String decimationText = FormattingUtil.getAttributeColor(decimateChance) + FormattingUtil.dF.format(decimateChance) + ChatColor.GRAY;
        decimationText = ChatColor.GRAY + "Bash Attack Chance: " + decimationText + "%";
        List<String> loreUpdated = new LinkedList<String>();
        loreUpdated.add(ToolHandlerPlugin.versionIdentifier + ChatColor.WHITE + "=======Item Statistics=======");
        loreUpdated.add(improvementText);
        loreUpdated.add(truedmgText);
        loreUpdated.add(bashText);
        loreUpdated.add(decimationText);
        loreUpdated.add(ChatColor.WHITE + "========Modifications========");
        for(String toAdd : modifications ) {
            loreUpdated.add(toAdd);
        }
        meta.setLore(loreUpdated);
        tool.setItemMeta(meta);
        return;
        
    }

}