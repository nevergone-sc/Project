import java.security.*;
import java.security.spec.*;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Crypto {
	public static final int LENGTH_SIGN = 128;
	public static final int LENGTH_ASYM_CIPHER = 128;
	public static final int LENGTH_MAC = 32;
	public static final int LENGTH_HASH = 32;
	final int LENGTH_IV = 16;
	
	public byte[] generateSymmKey(int size) {
		try {
			KeyGenerator keygen = KeyGenerator.getInstance("AES");
			keygen.init(size);
			Key key = keygen.generateKey();
			return key.getEncoded();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	// Symmetric encryption, AES + CBC + PKCS5Padding
	// The first few bits of returned bytes is the initial vector, the rest are ciphertext
	public byte[] encryptSymm(byte[] plaintext, byte[] encryptionKey) {
		// Wrap the key bytes into Key object
		SecretKeySpec key = new SecretKeySpec(encryptionKey, "AES");
		
		byte[] IV;
		// Generate a random IV manually
		/*
		Random random = new Random(System.currentTimeMillis());
		IV = new byte[LENGTH_IV];
		random.nextBytes(IV);
		*/
		
		try {
			Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
			c.init(Cipher.ENCRYPT_MODE, key);
			byte[] ciphertext = c.doFinal(plaintext);
			
			IV = c.getIV();
			int cipherLength= ciphertext.length;
			byte[] returnBytes = new byte[LENGTH_IV+cipherLength];
			
			System.arraycopy(IV, 0, returnBytes, 0, LENGTH_IV);
			System.arraycopy(ciphertext, 0, returnBytes, LENGTH_IV, cipherLength);
			return returnBytes;
			
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	

	// Symmetric decryption, AES + CBC + PKCS5Padding
	// The first few bits of ciphertext will be regarded as the initial vector
	public byte[] decryptSymm(byte[] ciphertext, byte[] decryptionKey) {
		// Wrap the key bytes into Key object
		SecretKeySpec key = new SecretKeySpec(decryptionKey, "AES");
		
		// Extract the IV and real cipher from the source ciphertext
		byte[] IV = new byte[LENGTH_IV];
		int cipherLength = ciphertext.length - LENGTH_IV;
		byte[] cipher = new byte[cipherLength];
		System.arraycopy(ciphertext, 0, IV, 0, LENGTH_IV);
		System.arraycopy(ciphertext, LENGTH_IV, cipher, 0, cipherLength);
		
		try {
			Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
			c.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(IV));
			return c.doFinal(cipher);
			
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public KeyPair generateAsymKey(int size) {
		try {
			KeyPairGenerator keygen = KeyPairGenerator.getInstance("RSA");
			keygen.initialize(size);
			return keygen.generateKeyPair();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public byte[] encryptAsym(byte[] plaintext, byte[] encryptionKey) {
		try {
			// Wrap the key bytes into public Key object
			KeyFactory kf = KeyFactory.getInstance("RSA");
			PublicKey publicKey = kf.generatePublic(new X509EncodedKeySpec(encryptionKey));
			
			Cipher c = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			c.init(Cipher.ENCRYPT_MODE, publicKey);
			byte[] ciphertext = c.doFinal(plaintext);
			
			return ciphertext;
			
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public byte[] decryptAsym(byte[] ciphertext, byte[] decryptionKey) {
		try {
			// Wrap the key bytes into Key object	
			KeyFactory kf = KeyFactory.getInstance("RSA");
			PrivateKey privateKey = kf.generatePrivate(new PKCS8EncodedKeySpec(decryptionKey));
			
			Cipher c = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			c.init(Cipher.DECRYPT_MODE, privateKey);
			return c.doFinal(ciphertext);
			
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public byte[] getHashDigest(byte[] input) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(input);
			return md.digest();
			
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public boolean verifyHashDigest(byte[] input, byte[] hash) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(input);
			byte[] digest = md.digest();
			if (digest.length != hash.length) return false;
			for (int i = 0; i < digest.length; i++) {
				if (digest[i] != hash[i]) return false;
			}
			return true;
			
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	public byte[] getMACDigest(byte[] input, byte[] key) {
		SecretKeySpec macKey = new SecretKeySpec(key, "HmacSHA256");
		
		try {
			Mac mac = Mac.getInstance("HmacSHA256");
			mac.init(macKey);
			return mac.doFinal(input);
		
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public boolean verifyMACDigest(byte[] input, byte[] key, byte[] macDigest) {
		SecretKeySpec macKey = new SecretKeySpec(key, "HmacSHA256");
		
		try {
			Mac mac = Mac.getInstance("HmacSHA256");
			mac.init(macKey);
			byte[] digest = mac.doFinal(input);
			
			for (int i = 0; i < digest.length; i++) {
				if (digest[i] != macDigest[i]) return false;
			}
		
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	
	public byte[] getSIGN(byte[] input, byte[] key) {
		try {
			// Wrap the key bytes into Key object	
			KeyFactory kf = KeyFactory.getInstance("RSA");
			PrivateKey privateKey = kf.generatePrivate(new PKCS8EncodedKeySpec(key));
						
			Signature sig = Signature.getInstance("SHA256withRSA");
			sig.initSign(privateKey);
			sig.update(input);
			return sig.sign();
			
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SignatureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public boolean verifySIGN(byte[] input, byte[] key, byte[] signature) {
		try {
			// Wrap the key bytes into Key object
			KeyFactory kf = KeyFactory.getInstance("RSA");
			PublicKey publicKey = kf.generatePublic(new X509EncodedKeySpec(key));
			
			Signature sig = Signature.getInstance("SHA256withRSA");
			sig.initVerify(publicKey);
			sig.update(input);
			return sig.verify(signature);
			
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SignatureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
}
