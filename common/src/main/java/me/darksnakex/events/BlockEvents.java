package me.darksnakex.events;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.BlockEvent;
import dev.architectury.event.events.common.InteractionEvent;
import dev.architectury.utils.value.IntValue;
import me.darksnakex.LogMyBlockCommands;
import me.darksnakex.model.BlockInteraction;
import me.darksnakex.model.InteractionType;
import me.darksnakex.model.MyBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

import static me.darksnakex.LogMyBlock.inspectingModePlayers;
import static me.darksnakex.LogMyBlock.interactions;

public class BlockEvents {


    public static void registerEvents() {

        BlockEvent.BREAK.register(BlockEvents::onBlockBreak);
        BlockEvent.PLACE.register(BlockEvents::onBlockPlace);
        InteractionEvent.LEFT_CLICK_BLOCK.register(BlockEvents::onLeftClickBlock);
        InteractionEvent.RIGHT_CLICK_BLOCK.register(BlockEvents::onRightClickBlock);

    }

    private static InteractionResult onRightClickBlock(Player player, InteractionHand interactionHand, BlockPos blockPos, Direction direction) {


        BlockState state = player.level().getBlockState(blockPos);

        if (interactionHand != InteractionHand.MAIN_HAND) {
            return InteractionResult.PASS;
        }

        if (inspectingModePlayers.contains(player.getPlainTextName())) {
            LogMyBlockCommands.showBlockHistory(
                    player,
                    blockPos.getX(),
                    blockPos.getY(),
                    blockPos.getZ(),
                    1
            );
            return InteractionResult.FAIL;
        }


        String blockId = BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString();

        MyBlock block = new MyBlock(blockId, blockPos);


        interactions.putIfAbsent(block, new ArrayList<>());

        // Corrección: Añadido blockId como 4to argumento
        BlockInteraction interaction = new BlockInteraction(player.getPlainTextName(), System.currentTimeMillis(), InteractionType.RIGHT_CLICK, blockId);

        interactions.get(block).add(interaction);


        return InteractionResult.PASS;
    }

    private static InteractionResult onLeftClickBlock(Player player, InteractionHand interactionHand, BlockPos blockPos, Direction direction) {


        BlockState state = player.level().getBlockState(blockPos);
        String blockId = BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString();

        MyBlock block = new MyBlock(blockId, blockPos);

        interactions.putIfAbsent(block, new ArrayList<>());

        // Corrección: Añadido blockId como 4to argumento
        BlockInteraction interaction = new BlockInteraction(player.getPlainTextName(), System.currentTimeMillis(), InteractionType.LEFT_CLICK, blockId);

        interactions.get(block).add(interaction);


        return InteractionResult.PASS;
    }

    private static EventResult onBlockPlace(Level level, BlockPos blockPos, BlockState blockState, @Nullable Entity entity) {

        if (level.isClientSide()) {
            return EventResult.pass();
        }

        assert entity != null;

        String blockId = BuiltInRegistries.BLOCK.getKey(blockState.getBlock()).toString();

        MyBlock block = new MyBlock(blockId, blockPos);

        interactions.putIfAbsent(block, new ArrayList<>());

        // Corrección: Añadido blockId como 4to argumento
        BlockInteraction interaction = new BlockInteraction(entity.getPlainTextName(), System.currentTimeMillis(), InteractionType.PLACE, blockId);

        interactions.get(block).add(interaction);

        return EventResult.pass();
    }


    private static EventResult onBlockBreak(Level level, BlockPos pos, BlockState state, Player player, IntValue xp) {
        if (level.isClientSide()) {
            return EventResult.pass();
        }

        String blockId = BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString();

        MyBlock block = new MyBlock(blockId, pos);

        interactions.putIfAbsent(block, new ArrayList<>());

        // Corrección: Añadido blockId como 4to argumento
        BlockInteraction interaction = new BlockInteraction(player.getPlainTextName(), System.currentTimeMillis(), InteractionType.BREAK, blockId);

        interactions.get(block).add(interaction);

        return EventResult.pass();
    }

}
