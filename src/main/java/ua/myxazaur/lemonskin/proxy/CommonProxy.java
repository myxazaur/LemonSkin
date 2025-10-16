package ua.myxazaur.lemonskin.proxy;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import ua.myxazaur.lemonskin.LemonSkin;
import ua.myxazaur.lemonskin.ModConfig;
import ua.myxazaur.lemonskin.helpers.BetterWithModsHelper;
import ua.myxazaur.lemonskin.network.SyncHandler;

public class CommonProxy
{
    public void preInit(FMLPreInitializationEvent event)
    {
        LemonSkin.hasAppleCore = Loader.isModLoaded("AppleCore") || Loader.isModLoaded("applecore");
        LemonSkin.hasAppleSkin = Loader.isModLoaded("AppleSkin") || Loader.isModLoaded("appleskin");

        MinecraftForge.EVENT_BUS.register(new ModConfig());
        BetterWithModsHelper.init();

        if (LemonSkin.hasAppleSkin) LemonSkin.Log.warn("AppleSkin detected. Better remove it");
    }

    public void init(FMLInitializationEvent event) {
        SyncHandler.init();
    }
}
