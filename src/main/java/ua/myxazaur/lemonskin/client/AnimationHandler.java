package ua.myxazaur.lemonskin.client;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ua.myxazaur.lemonskin.ModConfig;
import ua.myxazaur.lemonskin.Tags;

@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber(modid = Tags.MOD_ID, value = Side.CLIENT)
public class AnimationHandler
{
    private static final Minecraft mc = Minecraft.getMinecraft();

    private static float flashAlpha = 0f;
    private static int ticks = 0;

    public static float getFlashAlpha() {
        return flashAlpha;
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event)
    {
        if (event.phase != TickEvent.Phase.END) return;
        if (mc.isGamePaused() && !ModConfig.CLIENT.UPDATE_OVERLAY_ON_PAUSE) return;

        ticks++;
    }

    @SubscribeEvent
    public static void onRenderTick(TickEvent.RenderTickEvent event)
    {
        if (event.phase != TickEvent.Phase.START) return;

        float exactTick = ticks + (mc.isGamePaused() ? 0 : event.renderTickTime);

        float cycle = (exactTick % 32.0f) / 16.0f;
        float unclampedAlpha;

        if (cycle < 1.0f) {
            unclampedAlpha = -0.5f + (cycle * 2.0f);
        } else {
            unclampedAlpha = 1.5f - ((cycle - 1.0f) * 2.0f);
        }

        float clampedValue = Math.max(0F, Math.min(1F, unclampedAlpha));

        flashAlpha = clampedValue * Math.max(0F, Math.min(1F, ModConfig.CLIENT.MAX_HUD_OVERLAY_FLASH_ALPHA));
    }
}