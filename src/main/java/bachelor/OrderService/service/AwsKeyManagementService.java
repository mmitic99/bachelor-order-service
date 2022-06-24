package bachelor.OrderService.service;

public interface AwsKeyManagementService {
    String GetKeyByAlias(String alias);

    byte[] EncryptText(String textToEncrypt, String keyId);

    String DecryptText(byte[] encryptedText);
}
