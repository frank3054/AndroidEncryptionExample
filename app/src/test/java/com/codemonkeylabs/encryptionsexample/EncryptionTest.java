package com.codemonkeylabs.encryptionsexample;

import com.codemonkeylabs.encryptionexample.app.AESEncryptDecrypt;
import com.codemonkeylabs.encryptionexample.app.RSAEncryptDecrypt;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * Created by brianplummer on 9/2/14.
 */
@RunWith(JUnit4.class)
public class EncryptionTest
{

    private String testText = null;

    @Before
    public void setUp() throws IOException
    {
        File mobyDickFile = new File("src/test/resources/moby_dick.txt");
        FileInputStream fis = new FileInputStream(mobyDickFile);
        testText = IOUtils.toString(fis);
    }

    @After
    public void tearDown() throws Exception
    {
        testText = null;
    }

    @Test
    public void testAESEncryptionCTR()
    {
        AESEncryptDecrypt aesEncryptDecrypt = new AESEncryptDecrypt();
        String encryptedString = aesEncryptDecrypt.encrypt(testText,
                AESEncryptDecrypt.NOT_SECRET_ENCRYPTION_KEY.getBytes(),
                AESEncryptDecrypt.IVS.getBytes(),
                AESEncryptDecrypt.AESCipherType.AES_CIPHER_CTR_NOPADDING);

        String unencryptedString = aesEncryptDecrypt.decrypt(encryptedString,
                AESEncryptDecrypt.NOT_SECRET_ENCRYPTION_KEY.getBytes(),
                AESEncryptDecrypt.IVS.getBytes(),
                AESEncryptDecrypt.AESCipherType.AES_CIPHER_CTR_NOPADDING
        );
        assertTrue(unencryptedString.startsWith("All this while Tashtego, Daggoo, and Queequeg"));
    }

    @Test
    public void testAESEncryptionECB()
    {
        AESEncryptDecrypt aesEncryptDecrypt = new AESEncryptDecrypt();
        String encryptedString = aesEncryptDecrypt.encrypt(testText,
                AESEncryptDecrypt.NOT_SECRET_ENCRYPTION_KEY.getBytes(),
                null,
                AESEncryptDecrypt.AESCipherType.AES_CIPHER_ECB_PKCS5PADDING);

        String unencryptedString = aesEncryptDecrypt.decrypt(encryptedString,
                AESEncryptDecrypt.NOT_SECRET_ENCRYPTION_KEY.getBytes(),
                null,
                AESEncryptDecrypt.AESCipherType.AES_CIPHER_ECB_PKCS5PADDING);
        assertTrue(unencryptedString.startsWith("All this while Tashtego, Daggoo, and Queequeg"));
    }

    @Test
    public void testRSAandAESEncryption()
    {
        AESEncryptDecrypt aesEncryptDecrypt = new AESEncryptDecrypt();
        RSAEncryptDecrypt rsaEncryptDecrypt = new RSAEncryptDecrypt();
        String encryptedString = aesEncryptDecrypt.encrypt(testText,
                AESEncryptDecrypt.NOT_SECRET_ENCRYPTION_KEY.getBytes(),
                AESEncryptDecrypt.IVS.getBytes(),
                AESEncryptDecrypt.AESCipherType.AES_CIPHER_CTR_NOPADDING);

        byte[] combined = concat(AESEncryptDecrypt.NOT_SECRET_ENCRYPTION_KEY.getBytes(), AESEncryptDecrypt.IVS.getBytes());

        byte[] encryptedAESKey = rsaEncryptDecrypt.encrypt(combined);

        byte[] unencryptedAESKey = rsaEncryptDecrypt.decrypt(encryptedAESKey);

        byte[] aesKey = Arrays.copyOfRange(unencryptedAESKey, 0, 16);
        byte[] ivs = Arrays.copyOfRange(unencryptedAESKey, 16, 32);

        String unencryptedString = aesEncryptDecrypt.decrypt(encryptedString,
                aesKey,
                ivs,
                AESEncryptDecrypt.AESCipherType.AES_CIPHER_CTR_NOPADDING);
        assertTrue(unencryptedString.startsWith("All this while Tashtego, Daggoo, and Queequeg"));
    }

    //helper function that concats two byte arrays
    public byte[] concat(byte[] first, byte[] second){
        byte[] combined = new byte[first.length + second.length];
        System.arraycopy(first, 0, combined, 0, first.length);
        System.arraycopy(second, 0, combined, first.length, second.length);
        return combined;
    }
}
