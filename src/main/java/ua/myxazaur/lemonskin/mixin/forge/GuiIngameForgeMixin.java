package ua.myxazaur.lemonskin.mixin.forge;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.FoodStats;
import net.minecraftforge.client.GuiIngameForge;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ua.myxazaur.lemonskin.LemonSkin;
import ua.myxazaur.lemonskin.ModConfig;
import ua.myxazaur.lemonskin.client.AnimationHandler;
import ua.myxazaur.lemonskin.client.HUDOverlayRenderer;
import ua.myxazaur.lemonskin.helpers.AppleCoreHelper;
import ua.myxazaur.lemonskin.helpers.FoodHelper;
import ua.myxazaur.lemonskin.helpers.HealthHelper;
import ua.myxazaur.lemonskin.helpers.HungerHelper;
import ua.myxazaur.lemonskin.mixin.vanilla.GuiIngameAccessor;

import static net.minecraftforge.client.GuiIngameForge.left_height;
import static net.minecraftforge.client.GuiIngameForge.right_height;

@Mixin(value = GuiIngameForge.class, remap = false)
public abstract class GuiIngameForgeMixin
{
    @Unique
    private static int ls$foodRightHeight = 0;

    @Unique
    private static int ls$healthLeftHeight = 0;

    // Exhaustion underlay rendering
    @SuppressWarnings("MixinAnnotationTarget")
    @Inject(method = "renderFood", at = @At(value = "INVOKE", target = "Lnet/minecraft/profiler/Profiler;func_76320_a(Ljava/lang/String;)V"))
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

    @Inject(method = "renderFood", at = @At(value = "FIELD", target = "Lnet/minecraftforge/client/GuiIngameForge;right_height:I", shift = At.Shift.AFTER, opcode = Opcodes.PUTSTATIC))
    public void cacheRightHeight(int width, int height, CallbackInfo ci) {
        ls$foodRightHeight = right_height;
    }

    // Saturation / Hunger overlay rendering
    @Inject(method = "renderFood", at = @At("TAIL"))
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
        int top  = height - ls$foodRightHeight + 10;

        // Saturation overlay
        if (ModConfig.CLIENT.SHOW_SATURATION_OVERLAY)
            HUDOverlayRenderer.drawSaturationOverlay(0, stats.getSaturationLevel(), mc, left, top, 1f, updateCounter);

        if (!ModConfig.CLIENT.SHOW_FOOD_VALUES_OVERLAY || !FoodHelper.isFood(held))
            return;

        FoodHelper.BasicFoodValues  values = FoodHelper.getModifiedFoodValues(held, player);
        if (LemonSkin.hasAppleCore) values = AppleCoreHelper.getFoodValuesForDisplay(values, player);

        if (!player.canEat(false))
            return;

        // Restored hunger overlay
        HUDOverlayRenderer.drawHungerOverlay(values.hunger, stats.getFoodLevel(),
                mc, left, top, AnimationHandler.getFlashAlpha(), FoodHelper.isRotten(held), updateCounter);

        // Restored saturation overlay
        if (ModConfig.CLIENT.SHOW_SATURATION_OVERLAY)
        {
            int   newFoodValue       = stats.getFoodLevel() + values.hunger;
            float newSaturationValue = stats.getSaturationLevel() + values.getSaturationIncrement();
            HUDOverlayRenderer.drawSaturationOverlay(
                    newSaturationValue > newFoodValue ? newFoodValue - stats.getSaturationLevel() : values.getSaturationIncrement(),
                    stats.getSaturationLevel(), mc, left, top, AnimationHandler.getFlashAlpha(), updateCounter);
        }
    }

    @Inject(method = "renderHealth", at = @At("HEAD"))
    public void cacheLeftHeight(int width, int height, CallbackInfo ci) {
        ls$healthLeftHeight = left_height;
    }

    // Health overlay rendering
    @Inject(method = "renderHealth", at = @At("TAIL"))
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

        PotionEffect effect = FoodHelper.getHealingEffect(held);

        float heal = HealthHelper.getEstimatedHealthIncrement(player, values, effect);

        if (heal <= 0) return;

        float currentHealth = player.getHealth();
        float newHealth     = Math.min(currentHealth + heal, player.getMaxHealth());

        int left = width / 2 - 91;
        int top  = height - ls$healthLeftHeight;

        HUDOverlayRenderer.drawHealthOverlay(currentHealth, newHealth, mc, left, top, AnimationHandler.getFlashAlpha(), updateCounter);
    }
}
