package me.darksnakex;


import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import me.darksnakex.model.BlockInteraction;
import me.darksnakex.model.MyBlock;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static me.darksnakex.LogMyBlock.*;

public class LogMyBlockCommands {



    public static void registerCommands() {
        CommandRegistrationEvent.EVENT.register((dispatcher, registry, selection) -> {
            registerCommands(dispatcher);
        });
    }

    private static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("logmyblock")
                        .then(Commands.literal("lookup")
                                .then(Commands.argument("x", IntegerArgumentType.integer())
                                        .then(Commands.argument("y", IntegerArgumentType.integer())
                                                .then(Commands.argument("z", IntegerArgumentType.integer())
                                                        .executes(LogMyBlockCommands::lookup)
                                                        .then(Commands.argument("page", IntegerArgumentType.integer(1))
                                                                .executes(LogMyBlockCommands::lookup)
                                                        )
                                                )
                                        )
                                )
                        )
                        .then(Commands.literal("inspect")
                                .executes(LogMyBlockCommands::inspect)
                        )
        );
    }

    private static final int PER_PAGE = 5;

    private static int lookup(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        int x = IntegerArgumentType.getInteger(ctx, "x");
        int y = IntegerArgumentType.getInteger(ctx, "y");
        int z = IntegerArgumentType.getInteger(ctx, "z");

        int page = 1;
        if (ctx.getNodes().size() == 6) {
            page = IntegerArgumentType.getInteger(ctx, "page");
        }


        ServerPlayer player = ctx.getSource().getPlayerOrException();
        showBlockHistory(player, x, y, z, page);

        return 1;
    }




    private static int inspect(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {

        ServerPlayer player = ctx.getSource().getPlayerOrException();

        if(inspectingModePlayers.contains(player.getPlainTextName())){
            inspectingModePlayers.remove(player.getPlainTextName());
            player.displayClientMessage(
                    Component.literal(prefix +"Inspect mode disabled."),
                    false
            );
        }else{
            inspectingModePlayers.add(player.getPlainTextName());
            player.displayClientMessage(
                    Component.literal(prefix +"Inspect mode enabled."),
                    false
            );
        }


        return 0;
    }

    public static Map.Entry<MyBlock, List<BlockInteraction>> getBlockAndInteractionsAt(int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        List<BlockInteraction> result = new ArrayList<>();
        MyBlock foundBlock = null;

        for (Map.Entry<MyBlock, List<BlockInteraction>> entry : interactions.entrySet()) {
            MyBlock block = entry.getKey();

            if (block.getPos().equals(pos)) {
                result.addAll(entry.getValue());
                if (foundBlock == null) {
                    foundBlock = block;
                }
            }
        }

        result.sort((a, b) -> Long.compare(b.getTimestamp(), a.getTimestamp()));

        return foundBlock != null ? Map.entry(foundBlock, result) : null;
    }



    public static void showBlockHistory(Player player, int x, int y, int z, int page) {
        Map.Entry<MyBlock, List<BlockInteraction>> entry = getBlockAndInteractionsAt(x, y, z);

        player.displayClientMessage(
                Component.literal("----- "+prefix+ " ----- §7(" + x + " " + y + " " + z + ")"),
                false
        );

        if (entry == null || entry.getValue().isEmpty()) {
            player.displayClientMessage(Component.literal(prefix+ "There is no event history for this block."), false);
            return;
        }

        List<BlockInteraction> list = entry.getValue();

        int total = list.size();
        int totalPages = (int) Math.ceil(total / (double) PER_PAGE);
        if (page > totalPages) page = totalPages;
        if (page < 1) page = 1;

        int start = (page - 1) * PER_PAGE;
        int end = Math.min(start + PER_PAGE, total);

        for (int i = start; i < end; i++) {
            BlockInteraction in = list.get(i);
            long diff = System.currentTimeMillis() - in.getTimestamp();
            long seconds = diff / 1000;
            long minutes = seconds / 60;

            player.displayClientMessage(
                    Component.literal(
                            "§a" + in.getPlayerId() + " §f" + in.getType().name().toLowerCase() + " §3"+ in.getBlockName() +" §7(" + minutes + "m " + (seconds % 60) + "s)"
                    ),
                    false
            );
        }

        player.displayClientMessage(
                Component.literal("§7Page " + page + " / " + totalPages),
                false
        );
    }
}
