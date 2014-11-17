package auth.test.encryption

import com.nimbusds.jose.*
import com.nimbusds.jose.crypto.*
import org.junit.Assert
import org.junit.Ignore
import org.junit.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.security.*
import java.security.interfaces.ECPrivateKey
import java.security.interfaces.ECPublicKey
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.ECFieldFp
import java.security.spec.ECParameterSpec
import java.security.spec.ECPoint
import java.security.spec.EllipticCurve
import java.text.ParseException

/**
 * JSON Web Token test
 */
class JWTest {
    static final Logger LOGGER = LoggerFactory.getLogger(JWTest.class)

    static final String TEST_STRING = 'Hello world!'

    @Test
    void testSingCheckMAC() throws JOSEException, ParseException {
        // Create JWS object
        JWSObject jwsObject = new JWSObject(new JWSHeader(JWSAlgorithm.HS256), new Payload(TEST_STRING))

        // Create HMAC signer
        // Generate random 32-bit shared secret
        SecureRandom random = new SecureRandom()
        byte[] sharedSecret = new byte[32]
        random.nextBytes(sharedSecret)

        JWSSigner signer = new MACSigner(sharedSecret)
        jwsObject.sign(signer)

        // Serialise JWS object to compact format
        String s = jwsObject.serialize()
        LOGGER.info('Serialised JWS object: ' + s)

        // Parse back and check signature
        jwsObject = JWSObject.parse(s)

        JWSVerifier verifier = new MACVerifier(sharedSecret)

        LOGGER.info('Recovered payload message: ' + jwsObject.getPayload())
        Assert.assertTrue(jwsObject.verify(verifier))
        Assert.assertEquals(TEST_STRING, jwsObject.getPayload().toString())
    }

    @Test
    void testSingCheckRSA() throws JOSEException, ParseException, InvalidKeyException, NoSuchAlgorithmException {
        // RSA signatures require a and RSA key pair,
        // the key must be made known to the JWS recipient in
        // order to verify the signatures
        KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance('RSA')
        keyGenerator.initialize(1024)

        KeyPair kp = keyGenerator.genKeyPair()
        RSAPublicKey publicKey = (RSAPublicKey) kp.getPublic()
        RSAPrivateKey privateKey = (RSAPrivateKey) kp.getPrivate()

        // Create RSA-signer with the key
        JWSSigner signer = new RSASSASigner(privateKey)

        // Prepare JWS object with simple string as payload
        JWSObject jwsObject = new JWSObject(new JWSHeader(JWSAlgorithm.RS256), new Payload(TEST_STRING))

        // Compute the RSA signature
        jwsObject.sign(signer)

        String s = jwsObject.serialize()
        LOGGER.info('Serialised JWS object: ' + s)

        // To parse the JWS and verify it, e.g. on client-side
        jwsObject = JWSObject.parse(s)

        JWSVerifier verifier = new RSASSAVerifier(publicKey)

        LOGGER.info('Recovered payload message: ' + jwsObject.getPayload())
        Assert.assertTrue(jwsObject.verify(verifier))
        Assert.assertEquals(TEST_STRING, jwsObject.getPayload().toString())
    }

    @Ignore
    @Test
    void testSingCheckEC() throws NoSuchAlgorithmException, JOSEException, InvalidAlgorithmParameterException {
        // Create the and EC keys
        KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance('EC')
        // Valid (see note below) parameters set
        EllipticCurve c =
                new EllipticCurve(new ECFieldFp(BigInteger.valueOf(5L)),
                        BigInteger.ZERO,
                        BigInteger.valueOf(4L))
        ECPoint g = new ECPoint(BigInteger.ZERO, BigInteger.valueOf(2L))
        keyGenerator.initialize(new ECParameterSpec(c, g, BigInteger.valueOf(5L), 10))
        KeyPair keyPair = keyGenerator.generateKeyPair()

        ECPublicKey publicKey = (ECPublicKey) keyPair.getPublic()
        ECPrivateKey privateKey = (ECPrivateKey) keyPair.getPrivate()

        // Create the EC signer
        JWSSigner signer = new ECDSASigner(privateKey.getS())

        // Creates the JWS object with payload
        JWSObject jwsObject = new JWSObject(new JWSHeader(JWSAlgorithm.ES256), new Payload('Elliptic cure'))

        // Compute the EC signature
        jwsObject.sign(signer)

        // Serialize the JWS to compact form
        String s = jwsObject.serialize()

        // The recipient must create a verifier with the 'x' and 'y' EC params
        BigInteger x = publicKey.getW().getAffineX()
        BigInteger y = publicKey.getW().getAffineY()
        JWSVerifier verifier = new ECDSAVerifier(x, y)

        // Verify the EC signature
        Assert.assertTrue('ES256 signature verified', jwsObject.verify(verifier))
        Assert.assertEquals('Elliptic cure', jwsObject.getPayload().toString())
    }
}
