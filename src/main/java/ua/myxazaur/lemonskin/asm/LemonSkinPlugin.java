package ua.myxazaur.lemonskin.asm;

import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import ua.myxazaur.lemonskin.ModConfig;
import zone.rong.mixinbooter.IEarlyMixinLoader;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@IFMLLoadingPlugin.Name("LemonSkinPlugin")
@IFMLLoadingPlugin.MCVersion(ForgeVersion.mcVersion)
public class LemonSkinPlugin implements IFMLLoadingPlugin, IEarlyMixinLoader {
    @Override
    public String[] getASMTransformerClass() {
        return new String[0];
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Nullable
    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {

    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }

    @Override
    public List<String> getMixinConfigs() {
        return Arrays.asList("mixins.lemonskin.json", "mixins.recipebook_fix.json");
    }

    @Override
    public boolean shouldMixinConfigQueue(String mixinConfig) {
        switch (mixinConfig)
        {
            case "mixins.recipebook_fix.json":
                return ModConfig.CLIENT.RECIPE_BOOK_TOOLTIP_FIX;

            default:
                return true;
        }
    }
}
