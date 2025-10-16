package ua.myxazaur.lemonskin.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import ua.myxazaur.lemonskin.LemonSkin;
import ua.myxazaur.lemonskin.client.ClientTickHandler;
import ua.myxazaur.lemonskin.client.DebugInfoHandler;
import ua.myxazaur.lemonskin.client.GrayIconsReloader;
import ua.myxazaur.lemonskin.client.TooltipOverlayHandler;

public class ClientProxy extends CommonProxy
{
    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);

        DebugInfoHandler.init();
        TooltipOverlayHandler.init();
        LemonSkin.tickHandler = new ClientTickHandler();
        MinecraftForge.EVENT_BUS.register(LemonSkin.tickHandler);
        ((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager())
                .registerReloadListener(new GrayIconsReloader());
    }
}
