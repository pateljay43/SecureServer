/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.example.websocket;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import javax.crypto.interfaces.DHPrivateKey;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;
import javax.swing.KeyStroke;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x509.X509Name;
import org.bouncycastle.cert.X509v1CertificateBuilder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.crypto.tls.SignatureAlgorithm;
import org.bouncycastle.x509.X509V3CertificateGenerator;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author JAY
 */
public class test {

    public static void main(String[] args) throws IOException {
        JSONObject json = new JSONObject();
        String a = "asfd\\^sdfa\\asd";
        System.out.println("" + a.replace("\\", "/"));
        json.put("a", a);
        json.put("b", "a\\sfdsdfa");
        String toString = json.toString();
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("DH");
            kpg.initialize(512);
            KeyPair dkp = kpg.generateKeyPair();
            DHParameterSpec params = ((DHPublicKey) dkp.getPublic()).getParams();
            BigInteger p = params.getP();
            BigInteger g = params.getG();
            BigInteger X = ((DHPrivateKey) dkp.getPrivate()).getX();
            BigInteger Y = ((DHPublicKey) dkp.getPublic()).getY();
            int i = 0;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
//        JSONArray arr = new JSONArray();
//        JSONObject obj1 = new JSONObject();
//        obj1.put("id", "@");
//        
//        FileOutputStream out = null;
//        FileInputStream in = null;
//        DatabaseHandler db = DatabaseHandler.getInstance("root", "123456");
//        if (db == null) {
//            return;
//        }
//        String[] myKeys = db.getRSAKeys();
//        if (myKeys != null) {
//            System.out.println("public key: " + myKeys[0]);
//            System.out.println("private key: " + myKeys[1]);
//        }
//        db.close();
//            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA", "BC");
//            kpg.initialize(2048, new SecureRandom());
//            KeyPair kp = kpg.generateKeyPair();
    }
}
