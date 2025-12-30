package ua.myxazaur.lemonskin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ua.myxazaur.lemonskin.LemonSkin;
import ua.myxazaur.lemonskin.Tags;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Gray icons are used for customizable saturation color
 * @see ua.myxazaur.lemonskin.ModConfig
 */
@SideOnly(Side.CLIENT)
public class GrayIconsReloader implements IResourceManagerReloadListener
{
    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        generateGrayIcons(resourceManager);
    }

    public static void init()
    {
        ((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager())
                .registerReloadListener(new GrayIconsReloader());
    }

    private void generateGrayIcons(IResourceManager resourceManager)
    {
        try {
            BufferedImage image = ImageIO.read(
                    resourceManager.getResource(
                            new ResourceLocation(Tags.MOD_ID, "textures/icons.png")
                    ).getInputStream()
            );

            DynamicTexture grayTex = new DynamicTexture(toGrayscale(image));
            ResourceLocation rl = new ResourceLocation(Tags.MOD_ID, "icons_gray");

            Minecraft.getMinecraft().getTextureManager().loadTexture(rl, grayTex);
            LemonSkin.grayIcons = rl;

        } catch (IOException e) {
            throw new RuntimeException("Failed to reload gray icons", e);
        }
    }

    private static BufferedImage toGrayscale(BufferedImage img)
    {
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

