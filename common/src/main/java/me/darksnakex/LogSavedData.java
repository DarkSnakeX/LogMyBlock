package me.darksnakex;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import me.darksnakex.model.BlockInteraction;
import me.darksnakex.model.MyBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LogSavedData {

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(BlockPos.class, (JsonSerializer<BlockPos>) (src, typeOfSrc, context) -> {
                JsonObject obj = new JsonObject();
                obj.addProperty("x", src.getX());
                obj.addProperty("y", src.getY());
                obj.addProperty("z", src.getZ());
                return obj;
            })
            .registerTypeAdapter(BlockPos.class, (JsonDeserializer<BlockPos>) (json, typeOfT, context) -> {
                JsonObject obj = json.getAsJsonObject();
                return new BlockPos(
                        obj.get("x").getAsInt(),
                        obj.get("y").getAsInt(),
                        obj.get("z").getAsInt()
                );
            })
            .registerTypeAdapter(MyBlock.class, (JsonSerializer<MyBlock>) (src, typeOfSrc, context) -> {
                JsonObject obj = new JsonObject();
                obj.addProperty("nombre", src.getNombre());
                obj.add("pos", context.serialize(src.getPos()));
                return obj;
            })
            .registerTypeAdapter(MyBlock.class, (JsonDeserializer<MyBlock>) (json, typeOfT, context) -> {
                JsonObject obj = json.getAsJsonObject();
                String nombre = obj.get("nombre").getAsString();
                BlockPos pos = context.deserialize(obj.get("pos"), BlockPos.class);
                return new MyBlock(nombre, pos);
            })
            .enableComplexMapKeySerialization()
            .create();


    private static final String FILENAME = "logmyblock_data.json";

    public static void save(MinecraftServer server) {
        Path path = server.getWorldPath(LevelResource.ROOT).resolve(FILENAME);
        File file = path.toFile();

        try (FileWriter writer = new FileWriter(file)) {
            GSON.toJson(LogMyBlock.interactions, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void load(MinecraftServer server) {
        Path path = server.getWorldPath(LevelResource.ROOT).resolve(FILENAME);
        File file = path.toFile();

        if (!file.exists()) return;

        try (FileReader reader = new FileReader(file)) {
            Type type = new TypeToken<HashMap<MyBlock, List<BlockInteraction>>>(){}.getType();
            Map<MyBlock, List<BlockInteraction>> data = GSON.fromJson(reader, type);

            if (data != null) {
                LogMyBlock.interactions = data;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
