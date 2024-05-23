package dev.footer.gutils;

import dev.footer.gutils.cmd.CmdRegistry;
import dev.footer.gutils.lib.Config;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

import static dev.footer.gutils.GeneralUtilities.ID;

@Mod(ID)
@SuppressWarnings("unused")
public class GeneralUtilities {
    public static final String ID = "gutils";
    public static final Logger LOGGER = LogManager.getLogger();


    public GeneralUtilities(ModContainer container, IEventBus bus) {
        NeoForge.EVENT_BUS.register(new CmdRegistry());

        File jsonDir = new File("gutils-output");
        if(!jsonDir.exists()) {
            jsonDir.mkdir();
        }

        container.registerConfig(ModConfig.Type.CLIENT, Config.config, ID + "-client.toml");
    }
}
