package ua.myxazaur.lemonskin.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.FoodStats;
import net.minecraftforge.client.GuiIngameForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ua.myxazaur.lemonskin.LemonSkin;
import ua.myxazaur.lemonskin.ModConfig;
import ua.myxazaur.lemonskin.client.HUDOverlayRenderer;
import ua.myxazaur.lemonskin.helpers.AppleCoreHelper;
import ua.myxazaur.lemonskin.helpers.FoodHelper;
import ua.myxazaur.lemonskin.helpers.HealthHelper;
import ua.myxazaur.lemonskin.helpers.HungerHelper;

import static net.minecraftforge.client.GuiIngameForge.right_height;
import static ua.myxazaur.lemonskin.LemonSkin.tickHandler;

@Mixin(GuiIngameForge.class)
public abstract class GuiIngameForgeMixin
{
    // Exhaustion underlay rendering
    @Inject(method = "renderFood", at = @At(value = "INVOKE", target = "Lnet/minecraft/profiler/Profiler;func_76320_a(Ljava/lang/String;)V"), remap = false)
    public void preRenderFood(int width, int height, CallbackInfo ci)
    {
        if (!ModConfig.CLIENT.SHOW_FOOD_EXHAUSTION_UNDERLAY)
            return;

        Minecraft    mc     = Minecraft.getMinecraft();
        EntityPlayer player = mc.player;

        int left = width / 2 + 91;
        int top  = height - right_height;

        HUDOverlayRenderer.drawExhaustionOverlay(HungerHelper.getExhaustion(player), mc, left, top, 1f);
    }

    // Saturation / Hunger overlay rendering
    @Inject(method = "renderFood", at = @At("TAIL"), remap = false)
    public void postRenderFood(int width, int height, CallbackInfo ci)
    {
        if (!ModConfig.CLIENT.SHOW_FOOD_VALUES_OVERLAY && !ModConfig.CLIENT.SHOW_SATURATION_OVERLAY)
            return;

        Minecraft    mc     = Minecraft.getMinecraft();
        EntityPlayer player = mc.player;

        int updateCounter = ((GuiIngameAccessor) mc.ingameGUI).getUpdateCounter();

        ItemStack                    held   = player.getHeldItemMainhand();
        if(!FoodHelper.isFood(held)) held   = player.getHeldItemOffhand();

        FoodStats stats  = player.getFoodStats();

        int left = width / 2 + 91;
        int top  = height - right_height + 10;

        // Saturation overlay
        if (ModConfig.CLIENT.SHOW_SATURATION_OVERLAY)
            HUDOverlayRenderer.drawSaturationOverlay(0, stats.getSaturationLevel(), mc, left, top, 1f, updateCounter);

        if (!ModConfig.CLIENT.SHOW_FOOD_VALUES_OVERLAY || !FoodHelper.isFood(held))
        {
            tickHandler.flashAlpha = 0;
            tickHandler.alphaDir   = 1;
            return;
        }

        FoodHelper.BasicFoodValues  values = FoodHelper.getModifiedFoodValues(held, player);
        if (LemonSkin.hasAppleCore) values = AppleCoreHelper.getFoodValuesForDisplay(values, player);

        // Restored hunger overlay
        HUDOverlayRenderer.drawHungerOverlay(values.hunger, stats.getFoodLevel(),
                mc, left, top, tickHandler.flashAlpha, FoodHelper.isRotten(held), updateCounter);

        // Restored saturation overlay
        if (ModConfig.CLIENT.SHOW_SATURATION_OVERLAY)
        {
            int   newFoodValue       = stats.getFoodLevel() + values.hunger;
            float newSaturationValue = stats.getSaturationLevel() + values.getSaturationIncrement();
            HUDOverlayRenderer.drawSaturationOverlay(
                    newSaturationValue > newFoodValue ? newFoodValue - stats.getSaturationLevel() : values.getSaturationIncrement(),
                    stats.getSaturationLevel(), mc, left, top, tickHandler.flashAlpha, updateCounter);
        }
    }

    // Health overlay rendering
    @Inject(method = "renderHealth", at = @At("TAIL"), remap = false)
    public void postRenderHealth(int width, int height, CallbackInfo ci)
    {
        Minecraft    mc     = Minecraft.getMinecraft();
        EntityPlayer player = mc.player;

        if (!HUDOverlayRenderer.shouldShowEstimatedHealth(player))
            return;

        int updateCounter = ((GuiIngameAccessor) mc.ingameGUI).getUpdateCounter();

        ItemStack                    held   = player.getHeldItemMainhand();
        if(!FoodHelper.isFood(held)) held   = player.getHeldItemOffhand();
        if(!FoodHelper.isFood(held)) return;

        FoodHelper.BasicFoodValues  values = FoodHelper.getModifiedFoodValues(held, player);
        if (LemonSkin.hasAppleCore) values = AppleCoreHelper.getFoodValuesForDisplay(values, player);

        PotionEffect effect = FoodHelper.getEffect(held);

        // Hardcode for golden apple
        if (held.getItem() == Items.GOLDEN_APPLE)
        {
            if (held.getMetadata() > 0)
                 effect = new PotionEffect(MobEffects.REGENERATION, 400, 1);
            else effect = new PotionEffect(MobEffects.REGENERATION, 100, 1);

        }

        float heal = HealthHelper.getEstimatedHealthIncrement(player, values, effect);

        if (heal <= 0) return;

        float currentHealth = player.getHealth();
        float newHealth     = Math.min(currentHealth + heal, player.getMaxHealth());

        int left = width / 2 - 91;
        int top  = height - right_height;

        HUDOverlayRenderer.drawHealthOverlay(currentHealth, newHealth, mc, left, top, tickHandler.flashAlpha, updateCounter);
    }
}
