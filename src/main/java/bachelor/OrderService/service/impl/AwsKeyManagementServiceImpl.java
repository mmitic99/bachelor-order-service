package bachelor.OrderService.service.impl;

import bachelor.OrderService.dto.DataKeyDto;
import bachelor.OrderService.exception.BadRequestException;
import bachelor.OrderService.service.AwsKeyManagementService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.kms.KmsClient;
import software.amazon.awssdk.services.kms.model.*;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
@AllArgsConstructor
public class AwsKeyManagementServiceImpl implements AwsKeyManagementService {

    private final KmsClient kmsClient;

    @Override
    public String GetKeyByAlias(String alias) {
        var aliasResponse = kmsClient.listAliases(ListAliasesRequest.builder().limit(100).build());
        if (aliasResponse == null || aliasResponse.aliases() == null) {
            throw new BadRequestException("Aliases are empty.");
        }
        var foundAlias = aliasResponse.aliases().stream().filter(aliasR -> aliasR.aliasName().equals("alias/" + alias)).findFirst().orElse(null);
        if (foundAlias != null) {
            return foundAlias.targetKeyId();
        }
        throw new BadRequestException("Alias not found");
    }

    @Override
    public byte[] EncryptText(String textToEncrypt, String keyId) {
        if (textToEncrypt == null || textToEncrypt.isBlank()) {
            return null;
        }

        SdkBytes input = SdkBytes.fromByteArray(textToEncrypt.getBytes(StandardCharsets.UTF_8));
        EncryptRequest encryptRequest = EncryptRequest.builder().keyId(keyId).plaintext(input).build();

        EncryptResponse encryptResponse = kmsClient.encrypt(encryptRequest);
        return encryptResponse.ciphertextBlob().asByteArray();
    }

    @Override
    public String DecryptText(byte[] encryptedText) {
        if (encryptedText == null) {
            return "";
        }
        SdkBytes input = SdkBytes.fromByteArray(encryptedText);
        DecryptRequest decryptRequest = DecryptRequest.builder().ciphertextBlob(input).build();

        DecryptResponse decryptResponse = kmsClient.decrypt(decryptRequest);


        if (decryptResponse != null) {
            return new String(decryptResponse.plaintext().asByteArray());
        }

        throw new BadRequestException("Decrypt error");
    }

    @Override
    public String DecryptKey(byte[] encryptedText) {
        if (encryptedText == null) {
            return "";
        }
        SdkBytes input = SdkBytes.fromByteArray(encryptedText);
        DecryptRequest decryptRequest = DecryptRequest.builder().ciphertextBlob(input).build();

        DecryptResponse decryptResponse = kmsClient.decrypt(decryptRequest);


        if (decryptResponse != null) {
            return Base64.getEncoder().encodeToString(decryptResponse.plaintext().asByteArray());
        }

        throw new BadRequestException("Decrypt error");
    }

    @Override
    public DataKeyDto GenerateDataKey() {

        String keyId = GetKeyByAlias("bachelor-order");

        var response = kmsClient.generateDataKey(GenerateDataKeyRequest.builder().keyId(keyId).keySpec(DataKeySpec.AES_128).build());

        var plain = Base64.getEncoder().encodeToString(response.plaintext().asByteArray());
        var cipher = Base64.getEncoder().encodeToString(response.ciphertextBlob().asByteArray());

        return DataKeyDto.builder().plaintext(plain).ciphertext(cipher).build();
    }
}
