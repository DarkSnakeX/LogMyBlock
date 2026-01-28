package me.darksnakex;

import dev.architectury.event.events.common.LifecycleEvent;
import me.darksnakex.model.BlockInteraction;
import me.darksnakex.model.MyBlock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.darksnakex.LogMyBlockCommands.registerCommands;
import static me.darksnakex.events.BlockEvents.registerEvents;

public final class LogMyBlock {

    public static String prefix = "§e[§3Log§aMy§6Block§e] §f";

    public static final String MOD_ID = "logmyblock";

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);


    public static Map<MyBlock, List<BlockInteraction>> interactions = new HashMap<>();
    public static final List<String> inspectingModePlayers = new ArrayList<>();


    public static void init() {
        LOGGER.info("Initializing LogMyBlock mod");
        registerEvents();
        registerCommands();

        LifecycleEvent.SERVER_STARTED.register(server -> {
            LogSavedData.load(server);
            LOGGER.info("LogMyBlock data loaded.");
        });

        LifecycleEvent.SERVER_STOPPING.register(server -> {
            LogSavedData.save(server);
            LOGGER.info("LogMyBlock data saved.");
        });

    }







}
