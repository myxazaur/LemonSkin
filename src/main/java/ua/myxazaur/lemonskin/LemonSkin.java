package ua.myxazaur.lemonskin;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ua.myxazaur.lemonskin.client.DebugInfoHandler;
import ua.myxazaur.lemonskin.client.HUDOverlayHandler;
import ua.myxazaur.lemonskin.client.TooltipOverlayHandler;
import ua.myxazaur.lemonskin.helpers.BetterWithModsHelper;
import ua.myxazaur.lemonskin.network.SyncHandler;

@Mod(modid = Tags.MOD_ID, name = Tags.MOD_NAME, version = Tags.VERSION)
public class LemonSkin {

    public static Logger Log = LogManager.getLogger(ModInfo.MODID);
    public static boolean hasAppleCore = false;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        hasAppleCore = Loader.isModLoaded("AppleCore") || Loader.isModLoaded("applecore");
        ModConfig.init(event.getSuggestedConfigurationFile());
        BetterWithModsHelper.init();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        SyncHandler.init();

        if (event.getSide() == Side.CLIENT)
        {
            DebugInfoHandler.init();
            HUDOverlayHandler.init();
            TooltipOverlayHandler.init();
        }
    }

}
