package ua.myxazaur.lemonskin.client;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ua.myxazaur.lemonskin.ModConfig;

@SideOnly(Side.CLIENT)
public class ClientTickHandler
{
    private final Minecraft mc = Minecraft.getMinecraft();

    public float flashAlpha = 0f;
    private float unclampedFlashAlpha = 0f;
    public byte alphaDir = 1;

    public static ClientTickHandler init()
    {
        ClientTickHandler tickHandler = new ClientTickHandler();
        MinecraftForge.EVENT_BUS.register(tickHandler);
        return tickHandler;
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event)
    {
        if (mc.isGamePaused() && !ModConfig.CLIENT.UPDATE_OVERLAY_ON_PAUSE)
            return;

        if (event.phase != TickEvent.Phase.END)
            return;

        unclampedFlashAlpha += alphaDir * 0.125f;

        if (unclampedFlashAlpha >= 1.5f)
        {
            alphaDir = -1;
        }
        else if (unclampedFlashAlpha <= -0.5f)
        {
            alphaDir = 1;
        }

        float clampedValue = Math.max(0F, Math.min(1F, unclampedFlashAlpha));
        flashAlpha = clampedValue * Math.max(0F, Math.min(1F, ModConfig.CLIENT.MAX_HUD_OVERLAY_FLASH_ALPHA));
    }
}
