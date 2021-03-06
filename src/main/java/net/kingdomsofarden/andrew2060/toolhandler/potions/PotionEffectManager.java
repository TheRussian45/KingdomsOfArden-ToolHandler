package net.kingdomsofarden.andrew2060.toolhandler.potions;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

import net.kingdomsofarden.andrew2060.toolhandler.ToolHandlerPlugin;
import net.kingdomsofarden.andrew2060.toolhandler.listeners.potions.PotionEffectManagerListener;
import net.kingdomsofarden.andrew2060.toolhandler.tasks.PotionUpdateRunnable;
import net.minecraft.server.v1_7_R1.EntityPlayer;
import net.minecraft.server.v1_7_R1.MobEffect;
import net.minecraft.server.v1_7_R1.PacketPlayOutEntityEffect;

import org.bukkit.craftbukkit.v1_7_R1.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

/**
 * This class offers utilities to better be able to apply stacking potion effects
 * as well as utilities to apply hidden potion effects without any visual indicators.
 * 
 * Note that hidden potion effects can ONLY be applied to be players and are untracked, meaning that
 * new potion effects will override them.
 * @author Andrew
 *
 */
public class PotionEffectManager {
    
    private ToolHandlerPlugin plugin;
    private ConcurrentHashMap<LivingEntity, PotionTaskWrapper> activeTasks;
    
    public PotionEffectManager(ToolHandlerPlugin plugin) {
        this.plugin = plugin;
        this.activeTasks = new ConcurrentHashMap<LivingEntity,PotionTaskWrapper>();
        //Remove invalid entities/despawned entities on a schedule 
        new InvalidEntityRemovalTask().runTaskTimer(plugin, 6000, 100);
        //Schedule Listener to remove Entities on Death
        plugin.getServer().getPluginManager().registerEvents(new PotionEffectManagerListener(this), plugin);
        //Override CraftLivingEntity's existing methods
        //new PotionMethodInjector(this).createProxy();
    }
    /**
     * Processes all active potion effects for a LivingEntity in queue and removes scheduled future applications
     * of the potioneffecttype for said LivingEntity
     * @param type The PotionEffectType to Remove
     * @param lE The Living Entity to remove the Potion Effect from
     */
    @SuppressWarnings("deprecation")
    public void removePotionEffect(PotionEffectType type, LivingEntity lE) {
        if(activeTasks.containsKey(lE)) {
            activeTasks.get(lE).removePotionEffect(type);
        }
        ((CraftLivingEntity)lE).getHandle().m(type.getId());
    }

    /**
     * Untracks and cancels all pending potion application tasks (if any) for a given LivingEntity
     * @param lE The Living Entity to untrack.
     */
    public void untrackEntity(LivingEntity lE) {
        if(activeTasks.containsKey(lE)) {
            activeTasks.get(lE).removeAllTasks();
            activeTasks.remove(lE);
        }
    }
    /**
     * Adds a potion effect to a player, or scheduling it appropriately in the event of a type conflict
     * @param effects A collection of potion effects to add
     * @param lE The Living Entity to remove the Potion Effect from
     * @param hidden Whether the potion effects should be hidden, i.e. effects still present, bubbles are not.
     */
    public void addPotionEffectStacking(Collection<PotionEffect> effects, LivingEntity lE, boolean hidden) {
        for(PotionEffect effect : effects) {
            addPotionEffectStacking(effect, lE, hidden);
        }
    }
    @Deprecated
    public void addPotionEffectStacking(PotionEffect effect, LivingEntity lE) {
        addPotionEffectStacking(effect,lE,false);
    }
    /**
     * Adds a potion effect to a player, or scheduling it appropriately in the event of a type conflict
     * @param effect The PotionEffect to add
     * @param lE The Living Entity to remove the Potion Effect from
     */
    @SuppressWarnings("deprecation")
    public void addPotionEffectStacking(PotionEffect effect, LivingEntity lE, boolean hidden) {
        PotionEffectType searchType = effect.getType();
        Collection<PotionEffect> activeEffects = lE.getActivePotionEffects();
        PotionEffect search = null;
        //Find the matching PotionEffect
        if(lE.hasPotionEffect(searchType)) {
            for(PotionEffect pE : activeEffects) {
                if(pE.getType().equals(searchType)) {
                    search = pE;
                    break;
                } else {
                    continue;
                }
            }
        }
        //Ignore hidden flag for mobs
        if(!(lE instanceof Player)) {
            hidden = false;
        }
        //If the matching potion effect is not found/expired, then just apply the effect directly
        //If a similar effect already preexists we must determine how the effects will be stacked
        if(search == null) {
            if(hidden) {
                EntityPlayer ePlayer = ((CraftPlayer) lE).getHandle();
                ePlayer.playerConnection.sendPacket(new PacketPlayOutEntityEffect(ePlayer.getId(), new MobEffect(effect.getType().getId(), effect.getDuration(), effect.getAmplifier())));
            } else {
                ((CraftLivingEntity)lE).getHandle().addEffect(new MobEffect(effect.getType().getId(), effect.getDuration(), effect.getAmplifier()));
            }
            return;
        } else {
            //If the newly applied potion effect has a greater amplifier or equal amplifier to the prexisting potion effect
            if(effect.getAmplifier() >= search.getAmplifier()) {
                //If the newly applied effect also has a greater than or equal length remaining duration
                //Then we can just override the effect since the new effect would expire at a later time or at the same time
                //With equal or greater amplifier
                //If the newly applied effect has a shorter length we must schedule reinstatement of the longer potion effect
                if(effect.getDuration() >= search.getDuration()) {
                    if(hidden) {
                        EntityPlayer ePlayer = ((CraftPlayer) lE).getHandle();
                        ePlayer.playerConnection.sendPacket(new PacketPlayOutEntityEffect(ePlayer.getId(), new MobEffect(effect.getType().getId(), effect.getDuration(), effect.getAmplifier())));
                    } else {
                        ((CraftLivingEntity)lE).getHandle().addEffect(new MobEffect(effect.getType().getId(), effect.getDuration(), effect.getAmplifier()));
                    }
                    return;
                } else {
                    int remainingDuration = search.getDuration() - effect.getDuration();
                    //First force apply the new effect immediately
                    if(hidden) {
                        EntityPlayer ePlayer = ((CraftPlayer) lE).getHandle();
                        ePlayer.playerConnection.sendPacket(new PacketPlayOutEntityEffect(ePlayer.getId(), new MobEffect(effect.getType().getId(), effect.getDuration(), effect.getAmplifier())));
                    } else {
                        ((CraftLivingEntity)lE).getHandle().addEffect(new MobEffect(effect.getType().getId(), effect.getDuration(), effect.getAmplifier()));
                    }
                    //Make the remaining effect when the applied one expires
                    final PotionEffect remainder = searchType.createEffect(remainingDuration, search.getAmplifier());
                    //Schedule another attempt to apply the remaining duration when the time of the new effect expires
                    BukkitTask task = new PotionUpdateRunnable(plugin, remainder, lE, hidden).runTaskLater(this.plugin, effect.getDuration());
                    //Add task to list of running potion tasks for a given livingentity
                    if(activeTasks.containsKey(lE)) {
                        PotionTaskWrapper taskWrapper = activeTasks.get(lE);
                        taskWrapper.addTask(remainder.getType(), task);
                    } else {
                        PotionTaskWrapper taskWrapper = new PotionTaskWrapper();
                        taskWrapper.addTask(remainder.getType(), task);
                        activeTasks.put(lE, taskWrapper);
                    }
                    return;
                }
            }
            //If the newly applied potion effect has a smaller amplifier
            if(effect.getAmplifier() < search.getAmplifier()) {
                //If the new effect has a greater duration we must apply new potion effect once current one expires
                //If the new effect has a shorter or equal duration we can just ignore it completely
                if(effect.getDuration() > search.getDuration()) {
                    int remainingDuration = effect.getDuration() - search.getDuration();
                    final PotionEffect remainder = searchType.createEffect(remainingDuration, effect.getAmplifier());
                    //Schedule another attempt to apply the lower amplitude potion effect once current one ends
                    BukkitTask task = new PotionUpdateRunnable(plugin, remainder, lE, hidden).runTaskLater(this.plugin, effect.getDuration());
                    //Add task to list of running potion tasks for a given livingentity
                    if(activeTasks.containsKey(lE)) {
                        PotionTaskWrapper taskWrapper = activeTasks.get(lE);
                        taskWrapper.addTask(remainder.getType(), task);
                    } else {
                        PotionTaskWrapper taskWrapper = new PotionTaskWrapper();
                        taskWrapper.addTask(remainder.getType(), task);
                        activeTasks.put(lE, taskWrapper);
                    }
                    return;
                } else {
                    return;
                }
            }
        }       
    }
    /**
     * Removes invalid entities from the Potion task tracker that have become invalid through other means than death
     */
    private class InvalidEntityRemovalTask extends BukkitRunnable {

        @Override
        public void run() {
            for(LivingEntity lE : activeTasks.keySet()) {
                if(!lE.isValid()) {
                    activeTasks.get(lE).removeAllTasks();
                    activeTasks.remove(lE);
                }
            }
        }
        
    }
    public void removeTask(LivingEntity lE, int taskId) {
        if(activeTasks.containsKey(lE)) {
            activeTasks.get(lE).removeTask(taskId);
        }
    }
}
