package bachelor.OrderService.service.impl;

import bachelor.OrderService.service.EncryptionService;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.Random;

@Service
public class EncryptionServiceImpl implements EncryptionService {
    private Cipher cipher;
    private String algorithm;

    private static Random rand;
    private static int saltLength;

    @SneakyThrows
    public EncryptionServiceImpl(){
        cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        algorithm = "AES";
        rand = new Random((new Date()).getTime());
        saltLength = 8;
    }

    @SneakyThrows
    @Override
    public byte[] encrypt(String message, String key) {
        SecretKey secretKey = new SecretKeySpec(key.getBytes(), algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        byte[] salt = new byte[saltLength];
        rand.nextBytes(salt);

        String saltString = new String(salt);

        var cipherText = cipher.doFinal((saltString+message).getBytes());

        ByteArrayOutputStream retVal = new ByteArrayOutputStream( );
        retVal.write( salt );
        retVal.write( cipherText );

        return retVal.toByteArray();
    }

    @SneakyThrows
    @Override
    public String decrypt(byte[] message, String key) {
        SecretKey secretKey = new SecretKeySpec(key.getBytes(), algorithm);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        byte[] salt = new byte[saltLength];
        byte[] messageWithoutSalt = new byte[message.length-saltLength];

        System.arraycopy(message, 0, salt, 0, saltLength);
        System.arraycopy(message, saltLength, messageWithoutSalt, 0, message.length-saltLength);

        String retVal = new String(cipher.doFinal(messageWithoutSalt));

        return retVal.substring(new String(salt).length());
    }
}
