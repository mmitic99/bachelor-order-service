package bachelor.OrderService.service;

import software.amazon.awssdk.core.SdkBytes;

public interface AwsKeyManagementService {
    String GetKeyByAlias(String alias);
    byte[] EncryptText(String textToEncrypt, String keyId);
    String DecryptText(byte[] encryptedText);
}
