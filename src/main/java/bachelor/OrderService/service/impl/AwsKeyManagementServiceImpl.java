package bachelor.OrderService.service.impl;

import bachelor.OrderService.exception.BadRequestException;
import bachelor.OrderService.service.AwsKeyManagementService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.kms.KmsClient;
import software.amazon.awssdk.services.kms.model.*;

import java.nio.charset.StandardCharsets;

@Service
@AllArgsConstructor
public class AwsKeyManagementServiceImpl implements AwsKeyManagementService {

    private final KmsClient kmsClient;

    @Override
    public String GetKeyByAlias(String alias) {
        var aliasResponse = kmsClient.listAliases(ListAliasesRequest.builder().limit(100).build());
        if(aliasResponse == null || aliasResponse.aliases() == null){
            throw new BadRequestException("Aliases are empty.");
        }
        var foundAlias = aliasResponse.aliases().stream().filter(aliasR -> aliasR.aliasName().equals("alias/" + alias)).findFirst().orElse(null);
        if(foundAlias != null){
            return foundAlias.targetKeyId();
        }
        throw new BadRequestException("Alias not found");
    }

    @Override
    public byte[] EncryptText(String textToEncrypt, String keyId) {
        if(textToEncrypt == null || textToEncrypt.isBlank()){
            return null;
        }

        SdkBytes input = SdkBytes.fromByteArray(textToEncrypt.getBytes(StandardCharsets.UTF_8));
        EncryptRequest encryptRequest = EncryptRequest.builder().keyId(keyId).plaintext(input).build();

        EncryptResponse encryptResponse = kmsClient.encrypt(encryptRequest);
        return encryptResponse.ciphertextBlob().asByteArray();
    }

    @Override
    public String DecryptText(byte[] encryptedText) {
        if(encryptedText == null){
            return "";
        }
        SdkBytes input = SdkBytes.fromByteArray(encryptedText);
        DecryptRequest decryptRequest = DecryptRequest.builder().ciphertextBlob(input).build();

        DecryptResponse decryptResponse = kmsClient.decrypt(decryptRequest);


        if(decryptResponse != null){
            return new String(decryptResponse.plaintext().asByteArray());
        }

        throw new BadRequestException("Decrypt error");
    }
}
