package com.bms.bms.util;

import com.google.gson.Gson;
import com.xpresspayments.phedMiddlewareAdminPortal.exception.GeneralException;
import com.xpresspayments.phedMiddlewareAdminPortal.general.enums.ResponseCodeAndMessage;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.validator.routines.EmailValidator;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Formatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GeneralUtil {

    public static void validateNameAndPhoneNumber(String firstName, String lastName, String phoneNumber) {
        // check that first name is not null or empty
        if (GeneralUtil.stringIsNullOrEmpty(firstName)) {
            throw new GeneralException(ResponseCodeAndMessage.INCOMPLETE_PARAMETERS_91.responseCode, "First name cannot be null or empty!");
        }

        // check that last name is not null or empty
        if (GeneralUtil.stringIsNullOrEmpty(lastName)) {
            throw new GeneralException(ResponseCodeAndMessage.INCOMPLETE_PARAMETERS_91.responseCode, "Last name cannot be null or empty!");
        }

        // check that phone number is not null or empty
        if (GeneralUtil.stringIsNullOrEmpty(phoneNumber)) {
            throw new GeneralException(ResponseCodeAndMessage.INCOMPLETE_PARAMETERS_91.responseCode, "Phone number cannot be null or empty!");
        }
    }

    public static void verifyStringFieldIsPresent(String value, String fieldName, int rowNo) {
        boolean notValid = stringIsNullOrEmpty(value);
        if (notValid) {
            throw new GeneralException(ResponseCodeAndMessage.INCOMPLETE_PARAMETERS_91.responseCode,
                    fieldName + " on row " + rowNo + " cannot be empty");
        }
    }

    public static boolean invalidEmail(String email) {
        if (stringIsNullOrEmpty(email)) return true;

        return !EmailValidator.getInstance().isValid(email);
    }

    public static boolean stringIsNullOrEmpty(String arg) {
        if ((arg == null)) return true;
        else
            return ("".equals(arg)) || (arg.trim().length() == 0);
    }

    public static boolean containSpecialCharacter(String matchingString) {
        Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(matchingString);
        return m.find();
    }


    public static String toBase64(String val) {
        String encodedString = String.valueOf(Base64.encodeBase64String(val.getBytes()));
        System.out.println("converted " + val + " to Base 64 => " + encodedString);
        return encodedString;
    }

    public static String toSHA256(String text) {
        MessageDigest digest = null;
        String hashText = "";

        try {
            digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(text.getBytes(StandardCharsets.UTF_8));

            BigInteger no = new BigInteger(1, hash);

            hashText = no.toString(16);

            while (hashText.length() < 32) {
                hashText = "0" + hashText;
            }

            System.out.println("Converted " + text + " to SHA-256 => " + hashText);
            return hashText;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String encryptAES(String value, String ivKey, String apiKey) {
        try {
            IvParameterSpec iv = new IvParameterSpec(ivKey.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(apiKey.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            byte[] encrypted = cipher.doFinal(value.getBytes());
            System.out.println("encrypted string is :: " + Hex.encodeHexString(encrypted));
            return Hex.encodeHexString(encrypted);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static String decryptAES(String encrypted, String ivKey, String apiKey) {
        try {
            IvParameterSpec iv = new IvParameterSpec(ivKey.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(apiKey.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            byte[] original = cipher.doFinal(Hex.decodeHex(encrypted.toCharArray()));
            System.out.println("decrypted string is :: " + new String(original));

            return new String(original);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public static String calculateHMAC512(String data, String key)
            throws NoSuchAlgorithmException, InvalidKeyException {
        String HMAC_SHA512 = "HmacSHA512";

        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), HMAC_SHA512);
        Mac mac = Mac.getInstance(HMAC_SHA512);
        mac.init(secretKeySpec);

        return Hex.encodeHexString(mac.doFinal(data.getBytes()));
    }

    public static String[] split(String toSplit, String splitValue) {
        return toSplit.split(splitValue);
    }

    public static String sha512(String args) {

        try {
            // Create MessageDigest instance for MD5
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            //Add password bytes to digest
            md.update(args.getBytes(StandardCharsets.UTF_8));
            //Get the hash's bytes
            byte[] bytes = md.digest();
            //This bytes[] has bytes in decimal format;
            //Convert it to hexadecimal format

            return java.util.Base64.getEncoder().encodeToString(bytes);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean checkIntraBankStatus(String bankCode) {
        if (bankCode.length() == 3) {
            return compareBankCode(bankCode);
        } else {
            String code = bankCode.substring(1);
            return compareBankCode(code);
        }
    }


    public static boolean compareBankCode(String beneficiaryBankCode) {
        return beneficiaryBankCode.equals("214");
    }

    public static String toJson(Object o) {
        Gson gson = new Gson();
        String payload = gson.toJson(o);
        System.out.println(payload);
        return payload;
    }

    public static String getTimeString(int length) {
        long systemtime = System.currentTimeMillis();

        return Long.toString(systemtime).substring(0, length);
    }

    private static String toHexString(byte[] bytes) {
        Formatter formatter = new Formatter();
        for (byte b : bytes) {
            formatter.format("%02x", b);
        }
        return formatter.toString();
    }


    public static void main(String[] args) {
        String reference = "test_20191123132233";
        String appId = "FREE444942";
        String appKey = "77ecdc14a4b4f2f78323a996d9e5a0d8";

        Long systemtime = System.currentTimeMillis();

        String time = systemtime.toString().substring(0, 13);

//        httpHeaders.set("Authorization", "magtipon " + username + ":" + encodedString);

        //for backend test
//        String toHash = reference + "1617953042";
//        String hashed = sha512(toHash);


        String s = DateUtil.dateToJoinedString(new Date());
        String clientId = "3LINE CARD MANAGEMENT LIMITED100621";
        s = s + clientId;

        String usernameHash2 = toSHA256(s);
        String usernameHash = DigestUtils.sha256Hex(s);
        String passwordHash = DigestUtils.sha256Hex(clientId);

        System.out.println(usernameHash);
        System.out.println(passwordHash);
//
//        System.out.println(hashed);

    }

}
