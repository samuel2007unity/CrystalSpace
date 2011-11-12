// Package Declaration
package me.iffa.bananaspace.api;

// Java Imports
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

// BananaSpace Imports
import me.iffa.bananaspace.api.schematic.Schematic;

// Bukkit Imports
import org.bukkit.Location;

// JNBT Imports
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;
import org.jnbt.ByteArrayTag;
import org.jnbt.CompoundTag;
import org.jnbt.IntTag;
import org.jnbt.ListTag;
import org.jnbt.NBTInputStream;
import org.jnbt.ShortTag;
import org.jnbt.Tag;

/**
 * Schematic-file loading class. To be used for coming populators.
 * 
 * @author iffamies
 * @author DrAgonmoray (original NBT loading code)
 */
public class SpaceSchematicHandler {
    // Variables

    public static File schematicFolder = new File("plugins" + File.separator + "BananaSpace" + File.separator + "schematics");
    private static List<Schematic> schematics = new ArrayList<Schematic>();

    /**
     * Gets the list of schematics loaded.
     * 
     * @return List of schematics loaded
     */
    public static List<Schematic> getSchematics() {
        return schematics;
    }

    /**
     * Places a schematic to a location.
     * 
     * @param schematic Schematic
     * @param origin Location the schematic should be placed to
     */
    public static void placeSchematic(Schematic schematic, Location origin) {
        // TODO: Schematic placing code (WILL GET MESSY D:)
        Map<BlockVector, Map<String, Tag>> tileEntitiesMap = new HashMap<BlockVector, Map<String, Tag>>();
        for (Tag tag : schematic.getTileEntities()) {
            if (!(tag instanceof CompoundTag)) {
                continue;
            }
            CompoundTag t = (CompoundTag) tag;

            int x = 0;
            int y = 0;
            int z = 0;

            Map<String, Tag> values = new HashMap<String, Tag>();

            for (Map.Entry<String, Tag> entry : t.getValue().entrySet()) {
                if (entry.getKey().equals("x")) {
                    if (entry.getValue() instanceof IntTag) {
                        x = ((IntTag) entry.getValue()).getValue();
                    }
                } else if (entry.getKey().equals("y")) {
                    if (entry.getValue() instanceof IntTag) {
                        y = ((IntTag) entry.getValue()).getValue();
                    }
                } else if (entry.getKey().equals("z")) {
                    if (entry.getValue() instanceof IntTag) {
                        z = ((IntTag) entry.getValue()).getValue();
                    }
                }

                values.put(entry.getKey(), entry.getValue());
            }

            BlockVector vec = new BlockVector(x, y, z);
            tileEntitiesMap.put(vec, values);
        }
        Vector size = new Vector(schematic.getWidth(), schematic.getHeight(), schematic.getLength());
        for (int x = 0; x < schematic.getWidth(); ++x) {
            for (int y = 0; y < schematic.getHeight(); ++y) {
                for (int z = 0; z < schematic.getLength(); ++z) {
                    int index = y * schematic.getWidth() * schematic.getLength() + z * schematic.getWidth() + x;
                }
            }
        }
    }

    /**
     * Loads a schematic file and adds it to the schematics list for later usage.
     * 
     * @param file Schematic file
     */
    @SuppressWarnings("unchecked")
    public static void loadSchematic(File file) {
        try {
            FileInputStream fis = new FileInputStream(file);
            NBTInputStream nbt = new NBTInputStream(fis);
            CompoundTag backuptag = (CompoundTag) nbt.readTag();
            Map<String, Tag> tagCollection = backuptag.getValue();
            short width = (Short) getChildTag(tagCollection, "Width", ShortTag.class).getValue();
            short height = (Short) getChildTag(tagCollection, "Height", ShortTag.class).getValue();
            short length = (Short) getChildTag(tagCollection, "Length", ShortTag.class).getValue();
            byte[] blocks = (byte[]) getChildTag(tagCollection, "Blocks", ByteArrayTag.class).getValue();
            byte[] data = (byte[]) getChildTag(tagCollection, "Data", ByteArrayTag.class).getValue();
            List<Object> entities = (List) getChildTag(tagCollection, "Entities", ListTag.class).getValue();
            List<Tag> tileentities = (List) getChildTag(tagCollection, "TileEntities", ListTag.class).getValue();
            if (nbt != null) {
                nbt.close();
            }
            if (fis != null) {
                fis.close();
            }
            Schematic schematic = new Schematic(file.getName().replace(".schematic", ""), blocks, data, width, height, length, entities, tileentities);
            schematics.add(schematic);
            SpaceMessageHandler.debugPrint(Level.INFO, "Added Schematic '" + file.getName() + "' to schematics list:\n" + width + "\n" + height + "\n" + length + "\n" + blocks + "\n" + data);
        } catch (Exception e) {
            SpaceMessageHandler.print(Level.WARNING, "There was a problem while loading schematic file '" + file.getName() + "'! Are you sure it's a schematic: " + e.getMessage());
        }
    }

    /**
     * Gets a child tag.
     * 
     * @param items Tag collection
     * @param key Key
     * @param expected Expected
     * 
     * @return Child tag
     */
    private static Tag getChildTag(Map<String, Tag> items, String key, Class<? extends Tag> expected) {
        return items.get(key);
    }

    /**
     * Constructor of SpaceSchematicHandler.
     */
    private SpaceSchematicHandler() {
    }
}
