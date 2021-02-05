package com.wanglonghai.attendance.common;

import java.security.MessageDigest;
import java.util.Date;
import java.util.Random;

public class EncryptUtil {

	private static final char[] numbersAndLetters = ("0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ").toCharArray();

	public static final String randomSalt(int length) {

		Random randGen = new Random();
		StringBuilder sb = new StringBuilder(length);
		for (int i = 0; i < length; i++) {
			sb.append(numbersAndLetters[randGen.nextInt(62)]);
		}
		return sb.toString();
	}

	public static final String shaEncode(String source, String salt) {

		return shaEncode(source + salt);
	}

	public static final String cardNoEncode(String fullName, String mobile, Date createTime) {

		return shaEncode(fullName + "-" + mobile + "-" + createTime.getTime());
	}

	public static final String encode(String source, String algorithm) {

		try {
			MessageDigest alga = MessageDigest.getInstance(algorithm);
			alga.update(source.getBytes("UTF-8"));
			byte[] digesta = alga.digest();
			return toHex(digesta);
		} catch (Exception e) {
			return null;
		}
	}

	public static final byte[] toByteArr(String algorithm, String source) {

		try {
			MessageDigest alga = MessageDigest.getInstance(algorithm);
			alga.update(source.getBytes());
			return alga.digest();
		} catch (Exception e) {
			return null;
		}
	}

	public static final String shaEncode(String source) {

		return encode(source, "SHA");
	}

	public static final String sha1Encode(String source) {

		return encode(source, "SHA-1");
	}

	public static final String md5Encode(String source) {

		return encode(source, "MD5");
	}

	private static final String toHex(byte[] bytes) {

		StringBuffer result = new StringBuffer();
		for (int i = 0; i < bytes.length; i++) {
			if (Integer.toHexString(0xFF & bytes[i]).length() == 1)
				result.append("0").append(Integer.toHexString(0xFF & bytes[i]));
			else
				result.append(Integer.toHexString(0xFF & bytes[i]));
		}
		return result.toString();
	}

	public static String generateRandomNumbers(int length) {

		Random random = new Random();
		StringBuilder sb = new StringBuilder(length);
		for (int i = 0; i < length; i++) {
			sb.append(random.nextInt(10));
		}
		return sb.toString();
	}

	public static String generateRandomToken() {

		return generateRandom(32);
	}

	public static String generateRandom(Integer lenth) {

		Random random = new Random();
		StringBuilder sb = new StringBuilder(lenth);
		for (int i = 0; i < lenth; i++) {
			boolean b = random.nextBoolean();
			if (b) { // 字符串
				sb.append((char) (65 + random.nextInt(26)));
			} else { // 数字
				sb.append(String.valueOf(random.nextInt(10)));
			}
		}
		return sb.toString();
	}

	public static String[] generateApiKey(String platform) {

		String[] s = new String[2];
		s[0] = EncryptUtil.md5Encode(System.currentTimeMillis() + platform);
		s[1] = EncryptUtil.shaEncode(platform + System.currentTimeMillis());
		return s;
	}

}
