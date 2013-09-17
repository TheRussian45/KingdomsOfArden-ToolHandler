package net.kingdomsofarden.andrew2060.toolhandler.mods.typedefs;
import java.util.Collection;
import java.util.LinkedList;

import net.kingdomsofarden.andrew2060.toolhandler.ToolHandlerPlugin;
import net.kingdomsofarden.andrew2060.toolhandler.potions.PotionEffectManager;
import net.kingdomsofarden.andrew2060.toolhandler.util.ArmorLoreUtil;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import com.herocraftonline.heroes.api.events.WeaponDamageEvent;

public abstract class ArmorMod {
    protected PotionEffectManager pEMan;
    private String[] desc;
    private String name;
    private int weight;
    private boolean requiresSlot;
    private LinkedList<PotionEffect> effectsOnTick;
    private Double magicResist;
    private Double healingBonus;
    private Double protBonus;
    public ArmorMod(String name, int weight, boolean requiresSlot, String... desc) {
        this.pEMan = ToolHandlerPlugin.instance.getPotionEffectHandler();
        this.name = name;
        this.desc = desc;
        this.weight = weight;
        this.requiresSlot = requiresSlot;
        this.effectsOnTick = new LinkedList<PotionEffect>();
        this.magicResist = null;
        this.healingBonus = null;
        this.protBonus = null;
    }
    public void applyToArmor(ItemStack armor) {
        if(magicResist != null && magicResist > Double.valueOf(0.00)) {
            ArmorLoreUtil.addMagicResistRating(magicResist,armor);
        }
        if(healingBonus != null && healingBonus > Double.valueOf(0.00)) {
            ArmorLoreUtil.addHealingBonus(healingBonus,armor);
        }
        if(protBonus != null && protBonus > Double.valueOf(0.00)) {
            ArmorLoreUtil.addProtBonus(protBonus,armor);
        }
    }
    public String getName() {
        return name;
    }
    public String[] getDescription() {
        return desc;
    }
    public int getWeight() {
        return weight;
    }
    public boolean isSlotRequired() {
        return requiresSlot;
    }
    public abstract void executeOnArmorDamage(WeaponDamageEvent event);

    public abstract void executeOnTick(Player p);

    public void addTickEffect(PotionEffect effect) {
        effectsOnTick.add(effect);
    }
    public void addTickEffect(Collection<PotionEffect> effects) {
        for(PotionEffect effect: effects) {
            effectsOnTick.add(effect);
        }
    }
    //To prevent lag, only tick through players as opposed to all possible living entities
    public void onTick(Player p) {
        pEMan.addPotionEffectStacking(effectsOnTick, p, true);
        executeOnTick(p);
    }
    public Double getMagicResist() {
        return magicResist;
    }
    public void setMagicResist(Double magicResist) {
        this.magicResist = magicResist;
    }
    public Double getHealingBonus() {
        return healingBonus;
    }
    public void setHealingBonus(Double healingBonus) {
        this.healingBonus = healingBonus;
    }
    public Double getProtBonus() {
        return protBonus;
    }
    public void setProtBonus(Double protBonus) {
        this.protBonus = protBonus;
    }
}
