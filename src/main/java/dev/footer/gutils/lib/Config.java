package dev.footer.gutils.lib;

import dev.footer.gutils.GeneralUtilities;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class Config {

    public final ModConfigSpec.BooleanValue json;

    Config(ModConfigSpec.Builder builder) {
        json = builder
                .translation(GeneralUtilities.ID + "export_json")
                .comment("Allows GUtils to export JSON from commands.")
                .define("export_json", false)
                ;
    }

    public static final ModConfigSpec config;
    public static final Config clientConfig;

    static {
        final Pair<Config, ModConfigSpec> pair = new ModConfigSpec.Builder()
                .configure(Config::new);
        config = pair.getRight();
        clientConfig = pair.getLeft();
    }
}
