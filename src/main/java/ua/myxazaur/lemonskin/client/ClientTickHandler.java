package ua.myxazaur.lemonskin.client;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ua.myxazaur.lemonskin.ModConfig;

@SideOnly(Side.CLIENT)
public class ClientTickHandler
{
    private Minecraft mc = Minecraft.getMinecraft();
    public float flashAlpha = 0f;
    public byte  alphaDir   = 1;

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event)
    {
        if (mc.isGamePaused() && !ModConfig.CLIENT.UPDATE_OVERLAY_ON_PAUSE) return;

        if (event.phase != TickEvent.Phase.END)
            return;

        flashAlpha += alphaDir * 0.125f;
        if (flashAlpha >= 1.5f)
        {
            flashAlpha = 1f;
            alphaDir = -1;
        }
        else if (flashAlpha <= -0.5f)
        {
            flashAlpha = 0f;
            alphaDir = 1;
        }
    }
}
