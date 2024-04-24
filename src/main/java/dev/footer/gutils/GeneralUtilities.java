package dev.footer.gutils;

import dev.footer.gutils.cmd.CmdRegistry;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static dev.footer.gutils.GeneralUtilities.ID;

@Mod(ID)
@SuppressWarnings("unused")
public class GeneralUtilities {
    public static final String ID = "gutils";
    public static final Logger LOGGER = LogManager.getLogger();


    public GeneralUtilities(IEventBus bus) {
        NeoForge.EVENT_BUS.register(new CmdRegistry());
    }
}
