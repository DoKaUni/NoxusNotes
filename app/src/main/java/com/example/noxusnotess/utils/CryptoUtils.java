package com.example.noxusnotess.utils;

import android.content.Context;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Log;

import androidx.security.crypto.EncryptedFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class CryptoUtils {

    private static final String TAG = CryptoUtils.class.getSimpleName();

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

            // Use a try-with-resources block for the FileOutputStream
            try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
                EncryptedFile encryptedFile = new EncryptedFile.Builder(
                        file,
                        context,
                        masterKeyAlias,
                        EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
                ).build();

                byte[] encryptedBytes = encryptStringToBytes(data, masterKeyAlias);
                fileOutputStream.write(encryptedBytes);
            }

            // Return the path to the encrypted file
            return file.getAbsolutePath();
        } catch (Exception e) {
            Log.e(TAG, "Error encrypting data", e);
            return null; // Handle the error appropriately in your app
        }
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
                                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
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

    private static byte[] encryptStringToBytes(String data, String masterKeyAlias) throws IOException {
        try {
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(masterKeyAlias));

            return cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            Log.e(TAG, "Error encrypting data", e);
            throw new IOException("Error encrypting data", e);
        }
    }

    private static SecretKey getSecretKey(String masterKeyAlias) throws Exception {
        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);

        return (SecretKey) keyStore.getKey(masterKeyAlias, null);
    }
}







