package io.gridgo.utils;

import java.nio.ByteBuffer;
import java.util.UUID;

import com.fasterxml.uuid.EthernetAddress;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.NoArgGenerator;

import lombok.NonNull;

public class UuidUtils {

    private static final long NUM_100NS_INTERVALS_SINCE_UUID_EPOCH = 0x01b21dd213814000L;

    private static final EthernetAddress ADDRESS = EthernetAddress.fromInterface();

    private static final NoArgGenerator TIME_BASED_UUID_GENERATOR = ADDRESS == null //
            ? Generators.timeBasedGenerator() //
            : Generators.timeBasedGenerator(ADDRESS);

    /**************************************************************
     * RANDOM UUID
     **************************************************************/

    public static UUID randomUUID() {
        return UUID.randomUUID();
    }

    public static byte[] randomUUIDAsBytes() {
        return uuidToBytes(randomUUID());
    }

    public static String randomUUIDAsString() {
        return randomUUID().toString();
    }

    /**************************************************************
     * TIME BASED UUID
     **************************************************************/

    public static UUID timebasedUUID() {
        return TIME_BASED_UUID_GENERATOR.generate();
    }

    public static byte[] timebasedUUIDAsBytes() {
        return uuidToBytes(timebasedUUID());
    }

    public static String timebasedUUIDAsString() {
        return timebasedUUID().toString();
    }

    public static long getTimeFromUUID(@NonNull UUID uuid) {
        return (uuid.timestamp() - NUM_100NS_INTERVALS_SINCE_UUID_EPOCH) / 10000;
    }

    public static boolean isTimeBaseUUID(@NonNull UUID uuid) {
        return uuid.version() == 1;
    }

    public static byte[] uuidToBytes(@NonNull UUID uuid) {
        return ByteBuffer //
                .allocate(16) //
                .putLong(uuid.getMostSignificantBits()) //
                .putLong(uuid.getLeastSignificantBits()) //
                .array();
    }

    public static byte[] uuidToBytes(@NonNull String uuidString) {
        return uuidToBytes(UUID.fromString(uuidString));
    }

    public static UUID bytesToUUID(@NonNull byte[] bytes) {
        if (bytes.length != 16)
            throw new IllegalArgumentException("only byte array with length 16 is allowed, got: " + bytes.length);

        int i = 0;

        long msl = 0;
        for (; i < 8; i++)
            msl = (msl << 8) | (bytes[i] & 0xFF);

        long lsl = 0;
        for (; i < 16; i++)
            lsl = (lsl << 8) | (bytes[i] & 0xFF);

        return new UUID(msl, lsl);
    }

    public static String bytesToUUIDString(@NonNull byte[] bytes) {
        return bytesToUUID(bytes).toString();
    }
}
