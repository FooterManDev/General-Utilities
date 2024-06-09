package dev.footer.gutils.lib;

import dev.footer.gutils.GeneralUtilities;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class Config {


    private static final ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
    public static final ServerConfig config = new ServerConfig(builder);
    public static final ModConfigSpec spec = builder.build();

    public static class ServerConfig {

        public final ModConfigSpec.IntValue inspectItemPerm;
        public final ModConfigSpec.IntValue inspectBlockPerm;
        public final ModConfigSpec.IntValue inspectBiomePerm;

        ServerConfig(ModConfigSpec.Builder builder) {
            inspectItemPerm = builder
                    .comment("Permission level required to use /inspectItem.")
                    .defineInRange("PermissionLevel", 0, 0, 4)
            ;
            inspectBlockPerm = builder
                    .comment("Permission level required to use /inspectBlock.")
                    .defineInRange("PermissionLevel", 0, 0, 4)
            ;
            inspectBiomePerm = builder
                    .comment("Permission level required to use /inspectBiome.")
                    .defineInRange("PermissionLevel", 0, 0, 4)
            ;
        }
    }

    @SubscribeEvent
    public static void load(final ModConfigEvent.Loading e) {
        GeneralUtilities.LOGGER.debug("Loaded GUtils' ServerConfig {}", e.getConfig().getFileName());
    }

    @SubscribeEvent
    public static void fileChange(final ModConfigEvent.Reloading e) {
        GeneralUtilities.LOGGER.debug("GUtils' ServerConfig changed");
    }
}
