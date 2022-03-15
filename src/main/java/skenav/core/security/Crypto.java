package skenav.core.security;

import org.bouncycastle.crypto.generators.SCrypt;
import org.bouncycastle.jcajce.provider.digest.SHA3;
import skenav.core.db.Database;

import javax.ws.rs.WebApplicationException;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

public class Crypto {
    private static String cryptoSeed;

    public static void setCryptoSeed() {
        String seed = randomAlphaNum(10);
        String key = "Crypto Seed";
        Database database = new Database();
        database.addToAppData(key, seed);
    }
    public static String getCryptoSeed() {
        if (cryptoSeed == null) {
            Database database = new Database();
            cryptoSeed = database.getAppData("Crypto Seed");
        }
        return cryptoSeed;
    }
    public static String base64Encode(byte[] input) {
        return Base64.getEncoder().encodeToString(input);
    }
    //generate random chars from an array
    private static String randomChars(int length, char[] charsArray) {
        SecureRandom random = new SecureRandom();
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < length; i++) {
            char selected = charsArray[random.nextInt(charsArray.length)];
            output.append(selected);
        }
        return output.toString();
    }
    //create array of alphanumeric characters and use randomchars on it
    public static String randomAlphaNum(int length) {
        // https://stackoverflow.com/a/7111735
        char[] charsArray = {'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w',
                'x','y','z','A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W',
                'X','Y','Z', 1, 2, 3, 4, 5, 6, 7, 8, 9, 0};
        return randomChars(length, charsArray);
    }


    public static String hashPassword(String input) {
        ByteArrayOutputStream saltedBytes = new ByteArrayOutputStream();
        try {
            saltedBytes.write(getSalt().getBytes(StandardCharsets.UTF_8));
            saltedBytes.write(input.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
        }
        byte[] sCryptHash = SCrypt.generate(saltedBytes.toByteArray(), cryptoSeed.getBytes(), 16384, 8, 8, 32);
        return base64Encode(sCryptHash);
    }

    public static String getSalt() {
        String hashedCryptoSeed = sha3(getCryptoSeed());
        return hashedCryptoSeed.substring(0, 5);
    }

    public static boolean checkPassword(String username, String inputpassword) {
        String hashedinputpassword = hashPassword(inputpassword);
        Database database = new Database();
        String referencehashedpassword = database.getPasswordHash(username);
        if (inputpassword.equals(referencehashedpassword)) {
            return true;
        }
        else {
            return false;
        }

    }

    public static String sha3(String input) {
        final SHA3.DigestSHA3 sha3 = new SHA3.Digest512();
        sha3.update(input.getBytes(StandardCharsets.UTF_8));
        StringBuilder buff = new StringBuilder();
        for (byte b : sha3.digest()) {
            buff.append(String.format("%02x", b & 0xFF));
        }
        return buff.toString();
    }
}
