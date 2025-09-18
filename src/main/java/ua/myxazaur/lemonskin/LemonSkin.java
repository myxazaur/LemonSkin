package ua.myxazaur.lemonskin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
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

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

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
            generateGrayIcons();
            DebugInfoHandler.init();
            HUDOverlayHandler.init();
            TooltipOverlayHandler.init();
        }
    }

    private static void generateGrayIcons()
    {
        Minecraft mc = Minecraft.getMinecraft();
        BufferedImage image = null;
        try {
            image = ImageIO.read(mc.getResourceManager().getResource(new ResourceLocation(Tags.MOD_ID, "textures/icons.png")).getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        DynamicTexture grayTex = new DynamicTexture(toGrayscale(image));
        ResourceLocation rl = new ResourceLocation(Tags.MOD_ID, "icons_gray");
        mc.getTextureManager().loadTexture(rl, grayTex);
        grayIcons = rl;
    }

    private static BufferedImage toGrayscale(BufferedImage img) {
        int width = img.getWidth();
        int height = img.getHeight();

        BufferedImage grayImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = img.getRGB(x, y);

                Color c = new Color(rgb, true);
                int gray = (int)(0.299 * c.getRed() + 0.587 * c.getGreen() + 0.114 * c.getBlue());

                Color newColor = new Color(gray, gray, gray, c.getAlpha());
                grayImg.setRGB(x, y, newColor.getRGB());
            }
        }

        return grayImg;
    }
}
