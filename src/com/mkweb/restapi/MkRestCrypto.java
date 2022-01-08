package com.mkweb.restapi;

import com.mkweb.logger.MkLogger;
import com.mkweb.utils.crypto.MkCrypto;
import com.mkweb.utils.crypto.RSAHelper;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

public class MkRestCrypto {
    private static File keyFile = null;
    private MkRestCrypto(){};
    private static String sec = null;
    private static String pub = null;
    private static boolean set = false;
    private static MkLogger mklogger = new MkLogger("[MkRestCrypto");
    public static boolean setKeyFile(String path){
        mklogger.info("=*=*=*=*=*=*=* MkRestCrypto Config Start*=*=*=*=*=*=*=*=");
        if(keyFile != null){
            return false;
        }

        keyFile = new File(path);
        if(keyFile.exists())
            return (set = readFile());
        else {
            if (createKeyFile()) {
                return (set = saveFile());
            }
            return (set = false);
        }
    }

    public static boolean isSet(){  return set; }

    public static String encrypt(String message){
        try{
            return MkCrypto.encryptRSA(message, RSAHelper.getRSAPublicKeyFromBase64(pub));
        } catch (NoSuchPaddingException | InvalidKeyException | InvalidKeySpecException
                | BadPaddingException | NoSuchAlgorithmException | IllegalBlockSizeException e) {
            e.printStackTrace();
            mklogger.error("(func) crypto: " + e.getMessage());
        } catch (IOException e){
            e.printStackTrace();
            mklogger.error("(func) crypto: Key format is not supported. Only PKCS8, PKCS1v15 is supported now. : "
                    + e.getMessage());
        }
        return null;
    }

    public static String decrypt(String message){
        try{
            return MkCrypto.decryptRSA(message, RSAHelper.getRSAPrivateKeyFromBase64Encrypted(sec));
        } catch (NoSuchPaddingException | IllegalBlockSizeException | UnsupportedEncodingException | NoSuchAlgorithmException | BadPaddingException | InvalidKeySpecException | InvalidKeyException e) {
            e.printStackTrace();
            mklogger.error("(func) decrypt: " + e.getMessage());
        }
        return null;
    }

    private static boolean readFile(){
        try(FileReader reader = new FileReader(keyFile)) {
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);
            JSONObject keyPair = (JSONObject) jsonObject.get("key-pair");

            String seco = keyPair.get("sec").toString();
            String pubo = keyPair.get("pub").toString();
            sec = seco;
            pub = pubo;
        } catch (Exception e){
            mklogger.error(
                    "Fail to read key file. You need to restart the server to use RESTful API.: " +
                    e.getMessage());
            return false;
        }
        return true;
    }

    private static boolean createKeyFile() {
        try{
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            KeyPair keyPair = keyGen.genKeyPair();

            mklogger.info(keyPair.getPublic().toString());

            byte[] publicKey = keyPair.getPublic().getEncoded();
            pub = new String(Base64.getUrlEncoder().withoutPadding().encode(publicKey));

            byte[] secretKey = keyPair.getPrivate().getEncoded();
            mklogger.debug("secret: \n" + new String(Base64.getEncoder().encode(secretKey)));
            sec = new String(Base64.getUrlEncoder().withoutPadding().encode(secretKey));
            return true;
        } catch (NoSuchAlgorithmException e){
            mklogger.error(
                    "Fail to create key file. You need to restart the server to use RESTful API.: " +
                            e.getMessage());
            return false;
        }
    }

    private static boolean saveFile(){
        try{
            JSONObject jsonObject = new JSONObject();
            JSONObject jsonParent = new JSONObject();

            jsonObject.put("pub", pub);
            jsonObject.put("sec", sec);
            jsonParent.put("key-pair", jsonObject);

            new MkLogger("[MkRestCrypto]").info(
                    "Saving key pair... : " + jsonParent + "\n" +
                            "Target directory: " + keyFile.getAbsolutePath()
            );

            FileWriter file = new FileWriter(keyFile.getAbsolutePath());
            file.write(jsonParent.toJSONString());
            file.flush();
            file.close();
        } catch (IOException e) {
            mklogger.error(
                    "Fail to save key file. You need to restart the server to use ERSTful API. : " +
                            e.getMessage());
            return false;
        }

        keyFile = new File(keyFile.getAbsolutePath());
        return keyFile.exists();
    }
}
