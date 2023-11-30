package com.example.noxusnotess.utils;

import android.content.Context;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class CryptoUtils {

    private static final String TAG = CryptoUtils.class.getSimpleName();

    public CryptoUtils() throws NoSuchPaddingException, NoSuchAlgorithmException {
    }

    public static String encryptData(String data, String fileName, Context context) {
        try {
            // Generate a key for encryption using Android KeyStore
            String masterKeyAlias = createMasterKey();

            File directory = new File(context.getFilesDir(), "encrypted_notes");
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Create an encrypted file with a unique name based on the note title within the "encrypted_notes" directory
            File file = new File(directory, fileName);

            // Generate a random IV
            byte[] iv = generateIV();

            // Use a try-with-resources block for the FileOutputStream
            try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
                // Write the IV to the file
                fileOutputStream.write(iv.length);
                fileOutputStream.write(iv);

                // Encrypt the data and write to the file
                data = "12345678901234567 " + data;
                byte[] encryptedBytes = encryptStringToBytes(data, masterKeyAlias);

                fileOutputStream.write(encryptedBytes.length);
                fileOutputStream.write(encryptedBytes);
            }

            // Return the path to the encrypted file
            return file.getAbsolutePath();
        } catch (Exception e) {
            Log.e(TAG, "Error encrypting data", e);
            return null; // Handle the error appropriately in your app
        }
    }

    private static byte[] encryptStringToBytes(String data, String masterKeyAlias) throws IOException {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(masterKeyAlias));

            return cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            Log.e(TAG, "Error encrypting data", e);
            throw new IOException("Error encrypting data", e);
        }
    }

    public static String decryptData(String filePath, Context context) {
        try {
            // Get the master key alias
            String masterKeyAlias = createMasterKey();
            int ivSize, encryptedBytesSize;

            // Open the encrypted file
            File file = new File(filePath);

            // Use a try-with-resources block for the FileInputStream
            try (FileInputStream fileInputStream = new FileInputStream(file)) {
                // Read the IV from the file
                ivSize = fileInputStream.read();
                byte[] iv = new byte[ivSize]; // Adjust the size based on the IV length
                fileInputStream.read(iv);

                // Read the encrypted bytes from the file
                encryptedBytesSize = fileInputStream.read();
                byte[] encryptedBytes = new byte[encryptedBytesSize];
                fileInputStream.read(encryptedBytes);

                // Decrypt the data and return it as a string
                String decryptedString = decryptBytesToString(encryptedBytes, masterKeyAlias, iv);
                return decryptedString.substring(decryptedString.indexOf(' ') + 1);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error decrypting data", e);
            return null; // Handle the error appropriately in your app
        }
    }

    private static String decryptBytesToString(byte[] encryptedBytes, String masterKeyAlias, byte[] iv) throws IOException {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.DECRYPT_MODE, getSecretKey(masterKeyAlias), ivSpec);

            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            Log.e(TAG, "Error decrypting data", e);
            throw new IOException("Error decrypting data", e);
        }
    }

    private static byte[] generateIV() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] iv = new byte[16]; // Adjust the size based on the IV length
        secureRandom.nextBytes(iv);

        return iv;
    }

    private static String createMasterKey() throws IOException {
        try {
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);

            if (!keyStore.containsAlias("master_key")) {
                KeyGenerator keyGenerator = KeyGenerator.getInstance(
                        KeyProperties.KEY_ALGORITHM_AES,
                        "AndroidKeyStore"
                );

                keyGenerator.init(
                        new KeyGenParameterSpec.Builder(
                                "master_key",
                                KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT
                        )
                                .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                                .setUserAuthenticationRequired(false)
                                .setRandomizedEncryptionRequired(true)
                                .build()
                );

                keyGenerator.generateKey();
            }

            return "master_key";
        } catch (Exception e) {
            Log.e(TAG, "Error creating master key", e);
            throw new IOException("Error creating master key", e);
        }
    }

    private static SecretKey getSecretKey(String masterKeyAlias) throws Exception {
        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);

        return (SecretKey) keyStore.getKey(masterKeyAlias, null);
    }
}