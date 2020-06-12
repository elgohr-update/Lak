package com.uddernetworks.lak;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

public class Utility {

    /**
     * Returns an object array from varargs
     *
     * @param args The args
     * @return The args
     */
    public static <T> T[] args(T... args) {
        return args;
    }

    /**
     * Clamps a value between two other inclusive values.
     *
     * @param value The value to clamp and return
     * @param min The minimum value allowed
     * @param max The maximum value allowed
     * @return The clamped value
     */
    public static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    /**
     * Creates an ARGB hex string from a given {@link Color}, in the format of AARRGGBB. This is decodable by
     * {@link #colorFromHex(String)}.
     *
     * @param color The {@link Color} input
     * @return The created hex string
     */
    public static String hexFromColor(Color color) {
        return String.format("%02X%02X%02X%02X", color.getAlpha(), color.getRed(), color.getGreen(), color.getBlue());
    }

    /**
     * Creates a {@link Color} from a given ARGB hex value.
     *
     * @param hex The 8-digit hex value
     * @return The created {@link Color}
     */
    public static Color colorFromHex(String hex) {
        if (hex.length() != 8) {
            return null;
        }

        return new Color(parseColorHex(hex, 1), parseColorHex(hex, 2), parseColorHex(hex, 3), parseColorHex(hex, 0));
    }

    /**
     * Gets an integer of a pair of hex values at a given offset. Used internally for {@link #colorFromHex(String)}
     *
     * @param hex The full hex value
     * @param index The index of the hex value pair. For example, 0 would be the first two characters of hex, 1 would be
     *              the third and fourth characters, and so on.
     * @return The parsed integer
     */
    public static int parseColorHex(String hex, int index) {
        return Integer.parseInt(hex.substring((index * 2), (index * 2) + 2), 16);
    }

    /**
     * Creates an array of bytes representing the given {@link UUID}.
     *
     * @param uuid The {@link UUID} to get bytes from
     * @return The byte array
     */
    public static byte[] getBytesFromUUID(UUID uuid) {
        var bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        return bb.array();
    }

    /**
     * Gets a {@link UUID} from a given byte array, generated by {@link #getBytesFromUUID(UUID)}.
     *
     * @param bytes The byte input
     * @return The created {@link UUID}
     */
    public static UUID getUUIDFromBytes(byte[] bytes) {
        var byteBuffer = ByteBuffer.wrap(bytes);
        return new UUID(byteBuffer.getLong(), byteBuffer.getLong());
    }

    /**
     * Copies a {@link ByteBuffer} with an additional set of bytes at the end to be used. Works the same as using
     * {@link #copyBuffer(byte[], int)} with {@link ByteBuffer#array()}.
     *
     * @param initial       The initial {@link ByteBuffer}
     * @param trailingExtra The extra bytes to be used at the end
     * @return The new {@link ByteBuffer}
     */
    public static ByteBuffer copyBuffer(ByteBuffer initial, int trailingExtra) {
        return copyBuffer(initial.array(), trailingExtra);
    }

    /**
     * Copies a byte array with an additional set of bytes at the end to be used, resulting in a {@link ByteBuffer}.
     *
     * @param initial       The initial byte array
     * @param trailingExtra The extra bytes to be used at the end
     * @return The resultant {@link ByteBuffer}
     */
    public static ByteBuffer copyBuffer(byte[] initial, int trailingExtra) {
        var resulting = ByteBuffer.allocate(initial.length + trailingExtra);
        resulting.put(initial);
        return resulting;
    }

    /**
     * Reads a resource from the internal resources directory. Use / for path separator.
     *
     * @param path The relative path to the file
     * @return The bytes of the file content
     * @throws IOException If an error occurs
     */
    public static byte[] readResource(String path) throws IOException {
        var resource = Objects.requireNonNull(Utility.class.getClassLoader().getResource(path));
        return resource.openStream().readAllBytes();
    }

    /**
     * Reads a resource from the internal resources directory. Use / for path separator.
     *
     * @param path The relative path to the file
     * @return The file content as a String
     * @throws IOException If an error occurs
     */
    public static String readResourceString(String path) throws IOException {
        return new String(readResource(path));
    }
}
