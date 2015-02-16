package auth.test.encryption

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig

/**
 * Jacipt util for password generation
 */
class JasyptUtil {

    public static void main(final String[] args) {
        final StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor(
                config: new SimpleStringPBEConfig(
                        algorithm: 'PBEWithMD5AndDES',
                        password: 'secret',
                        providerClassName: 'org.bouncycastle.jce.provider.BouncyCastleProvider'
                )
        );

        System.out.println(encryptor.decrypt('xxxx'));
        System.out.println(encryptor.encrypt('xxxx'));
    }
}
