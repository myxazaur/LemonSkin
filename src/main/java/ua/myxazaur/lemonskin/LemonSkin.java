package ua.myxazaur.lemonskin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ua.myxazaur.lemonskin.client.DebugInfoHandler;
import ua.myxazaur.lemonskin.client.GrayIconsReloader;
import ua.myxazaur.lemonskin.client.HUDOverlayHandler;
import ua.myxazaur.lemonskin.client.TooltipOverlayHandler;
import ua.myxazaur.lemonskin.helpers.BetterWithModsHelper;
import ua.myxazaur.lemonskin.network.SyncHandler;

@Mod(modid = Tags.MOD_ID, name = Tags.MOD_NAME, version = Tags.VERSION)
public class LemonSkin {

    public static Logger Log = LogManager.getLogger(Tags.MOD_ID);
    public static ResourceLocation grayIcons;
    public static boolean hasAppleCore = false;
    public static boolean hasAppleSkin = false;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        hasAppleCore = Loader.isModLoaded("AppleCore") || Loader.isModLoaded("applecore");
        hasAppleSkin = Loader.isModLoaded("AppleSkin") || Loader.isModLoaded("appleskin");

        MinecraftForge.EVENT_BUS.register(new ModConfig());
        BetterWithModsHelper.init();

        if (hasAppleSkin) Log.warn("AppleSkin detected. Better remove it");
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

            ((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(new GrayIconsReloader());
        }
    }
}
