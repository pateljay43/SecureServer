/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.example.websocket;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.interfaces.DHPrivateKey;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;
import org.apache.tomcat.util.codec.binary.Base64;

/**
 *
 * @author JAY
 */
public class DatabaseHandler {

//    private static DatabaseHandler db;
    private String username;
    private String password;
    private Connection secureServerDB;
    private HashMap<String, BigInteger[]> secretKeyList;        // sessionId, [p,b]
    private SessionHandler sessionHandler;

//    public final static DatabaseHandler getInstance(String _username, String _password) {
//        try {
//            if (db == null
//                    || !(db.getUsername().equals(_username) && db.getUsername().equals(_password))) {
//                db = new DatabaseHandler(_username, _password);
//            }
//            return db;
//        } catch (ClassNotFoundException | SQLException ex) {
//            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return null;
//    }
    DatabaseHandler(String _username, String _password) {
        try {
            username = _username;
            password = _password;
            Class.forName("com.mysql.jdbc.Driver");
            secureServerDB = DriverManager
                    .getConnection("jdbc:mysql://127.0.0.1:3306/SecureServerDB?"
                            + "user=" + username
                            + "&password=" + password);
            secretKeyList = new HashMap<>();
            sessionHandler = new SessionHandler();
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void insertClientPublicKey(String _username, String _pubkey) {
        try {
            secureServerDB.createStatement().executeUpdate("INSERT INTO username_publickey (USERNAME,PUBLICKEY)"
                    + "VALUES('"
                    + _username
                    + "','"
                    + _pubkey
                    + "')");
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * tries to find last stored RSA keys in DB or generate new ones
     *
     * @return array containing [publicKey, privateKey] or null if DB does not
     * contain any keys
     */
    public String[] getRSAKeys() {
        try {
            ResultSet resultSet = secureServerDB.createStatement()
                    .executeQuery("select * from " + Constants.serverdata);
            if (resultSet.last()) {
                return new String[]{resultSet.getString(Constants.publickey),
                    resultSet.getString(Constants.privatekey)};
            } else {
                return generateRSAKeys();
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * generate new RSA key pair and store them in the DB, then returns them in
     * an array of string
     *
     * @return array containing [publicKey, privateKey] or null if DB does not
     * contain any keys
     */
    private String[] generateRSAKeys() {
        KeyPairGenerator kpg;
        try {
            kpg = KeyPairGenerator.getInstance("RSA", "BC");
            kpg.initialize(2048, new SecureRandom());
            KeyPair kp = kpg.generateKeyPair();
            String[] keys = new String[]{
                convertPublicKey(kp.getPublic()),
                convertPrivateKey(kp.getPrivate())};
            System.out.println("" + keys[0].toCharArray().length);
            System.out.println("" + keys[1].toCharArray().length);
            secureServerDB.createStatement()
                    .executeUpdate("INSERT INTO " + Constants.serverdata + ""
                            + "(" + Constants.publickey + "," + Constants.privatekey + ")"
                            + "VALUES('"
                            + keys[0]
                            + "','"
                            + keys[1]
                            + "')");
            return keys;
        } catch (NoSuchAlgorithmException | NoSuchProviderException | SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private PrivateKey convertPrivateKey(String key64) {
        try {
            return (KeyFactory.getInstance("RSA"))
                    .generatePrivate(new PKCS8EncodedKeySpec(Base64.decodeBase64(key64)));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private String convertPrivateKey(PrivateKey priv) {
        try {
            return Base64.encodeBase64String(((KeyFactory.getInstance("RSA"))
                    .getKeySpec(priv, PKCS8EncodedKeySpec.class))
                    .getEncoded());
        } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public final PublicKey convertPublicKey(String stored) {
        try {
            return (KeyFactory.getInstance("RSA"))
                    .generatePublic(new X509EncodedKeySpec(Base64.decodeBase64(stored)));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public final String convertPublicKey(PublicKey publ) {
        try {
            return Base64.encodeBase64String(((KeyFactory.getInstance("RSA"))
                    .getKeySpec(publ, X509EncodedKeySpec.class))
                    .getEncoded());
        } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public final String getUsername() {
        return username;
    }

    public final String getPassword() {
        return password;
    }

    public void close() {
        try {
            secureServerDB.close();
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

//    public String decryptRSA(String data) {
//// decrypt using rsa private key
//        String ret = "";
//        try {
//            ResultSet resultSet = secureServerDB.createStatement()
//                    .executeQuery("select " + Constants.privatekey + " from " + Constants.serverdata);
//            if (resultSet.last()) {
//                PrivateKey prik = convertPrivateKey(resultSet.getString(Constants.privatekey));
//                // decrypt
//                return "";
//            }
//        } catch (SQLException ex) {
//            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return ret;
//    }
    public boolean verify(String _id, String _hp) {
        try {
            _id = _id.replaceAll("[*]", "");
            String query = "select " + Constants.password + " from " + Constants.username_password
                    + " where " + Constants.username + "='" + _id + "'";
            ResultSet pass = secureServerDB.createStatement().executeQuery(query);
            if (pass.first()) {
                String hp = pass.getString(Constants.password);
                if (hp.equals(_hp)) {
                    return true;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return false;
    }

    public byte[] decryptRSA(byte[] data) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            ResultSet resultSet = secureServerDB.createStatement()
                    .executeQuery("select " + Constants.privatekey + " from " + Constants.serverdata);
            if (resultSet.last()) {
                PrivateKey prik = convertPrivateKey(resultSet.getString(Constants.privatekey));
                cipher.init(Cipher.DECRYPT_MODE, prik);
                return cipher.doFinal(data);
            }
        } catch (NoSuchAlgorithmException |
                NoSuchPaddingException |
                IllegalStateException |
                IllegalBlockSizeException |
                BadPaddingException | SQLException | InvalidKeyException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public BigInteger[] generateDHSeckey(String sessionId) {
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("DH");
            kpg.initialize(1024);
            KeyPair dkp = kpg.generateKeyPair();
            DHParameterSpec params = ((DHPublicKey) dkp.getPublic()).getParams();
            BigInteger p = params.getP();
            BigInteger g = params.getG();
            BigInteger b = ((DHPrivateKey) dkp.getPrivate()).getX();
            secretKeyList.putIfAbsent(sessionId, new BigInteger[]{p, b});
            BigInteger dhB = ((DHPublicKey) dkp.getPublic()).getY();
            return new BigInteger[]{g, p, dhB};
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public void generateDHkey(String sessionId, BigInteger dhA) {
        sessionHandler.addSession(sessionId,
                dhA.modPow(secretKeyList.get(sessionId)[1],
                        secretKeyList.get(sessionId)[0]));  // dhA^b mod p
    }

    public boolean addNewUser(String id, String hp) {
        try {
            if (hp == null || hp.equalsIgnoreCase("") || hp.length() != 31) {
                return false;
            }
//            System.out.println("" + hp.replace("\\\\", "\\"));
//            String query = "INSERT INTO " + Constants.username_password
//                    + " (" + Constants.username + "," + Constants.password + ")"
//                    + " VALUES "
//                    + " ('" + id + "','" + hp + "')";
            String query = "INSERT INTO " + Constants.username_password
                    + " (" + Constants.username + "," + Constants.password + ")"
                    + " VALUES "
                    + " ('" + id + "',?)";
            PreparedStatement statment = secureServerDB.prepareStatement(query);
            statment.setString(1, hp);
            int retCode = statment.executeUpdate();
//            int retCode = secureServerDB.createStatement().executeUpdate(query);
            if (retCode == 1) {
                return true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
}
