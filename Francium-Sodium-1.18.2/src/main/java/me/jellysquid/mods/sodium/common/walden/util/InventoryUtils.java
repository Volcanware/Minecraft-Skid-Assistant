package me.jellysquid.mods.sodium.common.walden.util;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SplashPotionItem;
import net.minecraft.potion.PotionUtil;

import java.util.function.Predicate;

import static me.jellysquid.mods.sodium.common.walden.ConfigManager.MC;

public enum InventoryUtils
{
    ;

    /**
     * Returns integer of a slot with splash potion of your specific potion effect
     *
     * @param  rawId  		You can get id of your specific effect from https://minecraft.fandom.com/el/wiki/Status_effect
     * @param  duration		Duration of potion effect
     * @param  amplifier	Multiplier of potion effect
     *
     * @return integer of slot with splash potion of your specific potion effect
     *
     * @author pycat
     */

    public static int findSplash(int rawId, int duration, int amplifier) {
        PlayerInventory inv = MC.player.getInventory();
        StatusEffectInstance potion = new StatusEffectInstance(StatusEffect.byRawId(rawId), duration, amplifier);

        for (int i = 0; i < 9; i++) {
            ItemStack itemStack = inv.getStack(i);

            if (itemStack.getItem() instanceof SplashPotionItem
                    && PotionUtil.getPotion(itemStack).getEffects().toString()
                    .contains(potion.toString())) {
                return i;
            }
        }

        return -1;

    }

    /**
     * Returns true if mainhand has the specific splash potion
     *
     * @param  rawId  			You can get id of your specific effect from https://minecraft.fandom.com/el/wiki/Status_effect
     * @param  duration			Duration of potion effect
     * @param  amplifier		Multiplier of potion effect
     * @param  mainHandStack	ItemStack of main hand
     *
     * @return boolean
     *
     * @author pycat
     */

    public static boolean isThatSplash(int rawId, int duration, int amplifier, ItemStack mainHandStack) {
        StatusEffectInstance potion = new StatusEffectInstance(StatusEffect.byRawId(rawId), duration, amplifier);

        if (mainHandStack.getItem() instanceof SplashPotionItem
                && PotionUtil.getPotion(mainHandStack).getEffects().toString()
                .contains(potion.toString())) {
            return true;
        }

        return false;

    }

    public static boolean selectItemFromHotbar(Predicate<Item> item)
    {
        PlayerInventory inv = MC.player.getInventory();

        for (int i = 0; i < 9; i++)
        {
            ItemStack itemStack = inv.getStack(i);
            if (!item.test(itemStack.getItem()))
                continue;
            inv.selectedSlot = i;
            return true;
        }

        return false;
    }

    public static boolean selectItemFromHotbar(Item item)
    {
        return selectItemFromHotbar(i -> i == item);
    }

    public static boolean hasItemInHotbar(Item item)
    {
        PlayerInventory inv = MC.player.getInventory();

        for (int i = 0; i < 9; i++)
        {
            ItemStack itemStack = inv.getStack(i);
            if (item.equals(itemStack.getItem()))
                return true;
        }
        return false;
    }

    public static int countItem(Predicate<Item> item)
    {
        PlayerInventory inv = MC.player.getInventory();

        int count = 0;

        for (int i = 0; i < 36; i++)
        {
            ItemStack itemStack = inv.getStack(i);
            if (item.test(itemStack.getItem()))
                count += itemStack.getCount();
        }

        return count;
    }

    public static int countItem(Item item)
    {
        return countItem(i -> i == item);
    }
}