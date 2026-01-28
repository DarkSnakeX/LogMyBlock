package me.darksnakex.model;


public class BlockInteraction {

    private final String playerName;
    private final long timestamp;
    private final InteractionType type;
    // Nuevo campo
    private final String blockName;

    public BlockInteraction(String playerId, long timestamp, InteractionType type, String blockName) {
        this.playerName = playerId;
        this.timestamp = timestamp;
        this.type = type;
        this.blockName = blockName;
    }

    public String getPlayerId() {
        return playerName;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public InteractionType getType() {
        return type;
    }

    public String getBlockName() {
        return blockName;
    }

}
