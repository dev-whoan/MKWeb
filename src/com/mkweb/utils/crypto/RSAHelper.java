package com.mkweb.utils.crypto;

import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Primitive;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class RSAHelper {
    private RSAHelper(){}

    public static PublicKey getRSAPublicKeyFromBase64(String base64Public) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        PublicKey publicKey = null;
        byte[] decodedBase64PubKey = Base64.getDecoder().decode(base64Public);
        KeyFactory kf = KeyFactory.getInstance("RSA");

        try{
            publicKey = kf.generatePublic(new X509EncodedKeySpec(decodedBase64PubKey));
        } catch(Exception e){
            ASN1Primitive asn1Prime = new ASN1InputStream(decodedBase64PubKey).readObject();
            org.bouncycastle.asn1.pkcs.RSAPublicKey rsaPub = org.bouncycastle.asn1.pkcs.RSAPublicKey.getInstance(asn1Prime);
            publicKey = kf.generatePublic(new RSAPublicKeySpec(rsaPub.getModulus(), rsaPub.getPublicExponent()));
        }
        return publicKey;
    }

    public static PrivateKey getRSAPrivateKeyFromBase64Encrypted(String base64PrivateKey)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] decodedBase64PrivateKey = Base64.getDecoder().decode(base64PrivateKey);

        return KeyFactory.getInstance("RSA")
                .generatePrivate(new PKCS8EncodedKeySpec(decodedBase64PrivateKey));
    }
}
