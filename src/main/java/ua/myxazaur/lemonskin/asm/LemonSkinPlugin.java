package ua.myxazaur.lemonskin.asm;

import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * Useless for now
 */
@IFMLLoadingPlugin.Name("LemonSkinPlugin")
@IFMLLoadingPlugin.MCVersion(ForgeVersion.mcVersion)
public class LemonSkinPlugin implements IFMLLoadingPlugin {
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

//    @Override
//    public List<String> getMixinConfigs() {
//        return Collections.singletonList("mixins.lemonskin.json");
//    }
}
