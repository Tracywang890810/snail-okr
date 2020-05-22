package com.seblong.okr.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

	private static Random strGen = new Random();
	 private static Random numGen = new Random();
	 private static char[] numbersAndLetters = ("0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ").toCharArray();
	 private static char[] numbers = ("0123456789").toCharArray();
	 /** * 产生随机字符串 * */
	 public static final String randomString(int length) {
	  if (length < 1) {
	   return null;
	  }
	  char[] randBuffer = new char[length];
	  for (int i = 0; i < randBuffer.length; i++) {
	   randBuffer[i] = numbersAndLetters[strGen.nextInt(61)];
	  }
	  return new String(randBuffer);
	 }

	 /** * 产生随机数值字符串 * */
	 public static final String randomNumStr(int length) {
	  if (length < 1) {
	   return null;
	  }
	  char[] randBuffer = new char[length];
	  for (int i = 0; i < randBuffer.length; i++) {
	   randBuffer[i] = numbers[numGen.nextInt(9)];
	  }
	  return new String(randBuffer);
	 }
	
	
	/**
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(String str) {
		return null == str || "".equals(str.trim());
	}

	/**
	 * @param str
	 * @return
	 */
	public static boolean isNotEmpty(String str) {
		return null != str && !"".equals(str.trim());
	}

	/**
	 * @param collection
	 * @param symbol
	 * @return
	 */
	public static String join(Set<String> collection, String symbol) {
		int index = 0;
		StringBuilder sb = new StringBuilder();
		for (String item : collection) {
			sb.append(item);
			index++;
			if (index < collection.size()) {
				sb.append(symbol);
			}
		}
		return sb.toString();
	}

	public static String join(String[] row, String symbol) {
		int index = 0;
		StringBuilder sb = new StringBuilder();
		for (String item : row) {
			sb.append(item);
			index++;
			if (index < row.length) {
				sb.append(symbol);
			}
		}
		return sb.toString();
	}

	/**
	 * @param collection
	 * @param symbol
	 * @return
	 */
	public static String join(List<String> collection, String symbol) {
		int index = 0;
		StringBuilder sb = new StringBuilder();
		for (String item : collection) {
			sb.append(item);
			index++;
			if (index < collection.size()) {
				sb.append(symbol);
			}
		}
		return sb.toString();
	}

	/**
	 * @param str
	 * @return
	 */
	public static Boolean isMobileNumber(String str, String countryCode) {
		if (!StringUtil.isEmpty(str) && (StringUtil.isEmpty(countryCode) || "86".equals(countryCode))) {
			//Pattern p = Pattern.compile("^(((13[0-9])|(17[0-9])|(14[5,7])|(15[^4,\\D])|(18[0-9]))\\d{8})$");
			Pattern p = Pattern.compile("^(0?(13|14|15|16|17|18|19)[0-9]{9})$");
			
			Matcher m = p.matcher(str);
			return m.matches();
		} else if(!StringUtil.isEmpty(str)){ //^[1-9]\d*|0$
			Pattern p = Pattern.compile("^[0-9]+$");
			Matcher m = p.matcher(str);
			return m.matches();
		} else {
			return false;
		}
	}

	public static boolean isIDCardNumber(String str) {
		if (!StringUtil.isEmpty(str)) {
			Pattern p = Pattern.compile("(^\\d{15}$)|(^\\d{18}$)|(^\\d{17}(\\d|X|x)$)");
			Matcher m = p.matcher(str);
			return m.matches();
		} else {
			return false;
		}
	}

	public static Boolean isTelNumber(String str) {
		if (!StringUtil.isEmpty(str)) {
			Pattern p = Pattern.compile("^((\\d{3,4})|\\d{3,4}\\-)?\\d{7,8}$");
			Matcher m = p.matcher(str);
			return m.matches();
		} else {
			return false;
		}
	}

	public static Boolean isNickname(String str) {
		if (!StringUtil.isEmpty(str)) {
			{
				Pattern p = Pattern.compile("^[0-9a-zA-Z\\-\\._]{5,40}$");
				Matcher m = p.matcher(str);
				if (m.matches()) {
					return true;
				}
			}
			{
				Pattern p = Pattern.compile("^[0-9a-zA-Z\\-\\._\\u4e00-\\u9fa5]{2,40}$");
				Matcher m = p.matcher(str);
				if (m.matches()) {
					return true;
				}
			}

			return false;
		} else {
			return false;
		}
	}

	/**
	 * @param str
	 * @return
	 */
	public static Boolean isEmailAddress(String str) {
		if (!StringUtil.isEmpty(str)) {
			Pattern p = Pattern.compile("^([a-zA-Z0-9\\._%\\-]+)@([a-zA-Z0-9\\.\\-]+\\.[a-zA-Z]{2,6})$");
			Matcher m = p.matcher(str);
			return m.matches();
		} else {
			return false;
		}
	}

	/**
	 * @param str
	 * @return
	 */
	public static Boolean isValidPassword(String str) {
		if (!StringUtil.isEmpty(str)) {
			Pattern p = Pattern.compile("^[\\w]{6,20}$");
			Matcher m = p.matcher(str);
			return m.matches();
		} else {
			return false;
		}
	}

	public static String capitalize(String str) {
		List<String> words = new ArrayList<String>();
		for (String word : str.split("\\s")) {
			String firstLetter = word.substring(0, 1);
			String others = word.substring(1);
			words.add(firstLetter.toUpperCase() + others);
		}
		return StringUtil.join(words, " ");
	}

	public static String trim(String str, String trimStr) {
		if (str.indexOf(trimStr) == 0) {
			str = str.substring(trimStr.length());
		}

		if (str.lastIndexOf(trimStr) == str.length() - trimStr.length()) {
			str = str.substring(0, str.length() - trimStr.length());
		}

		return str;
	}

	public static String getFileExtension(String filename) {
		String extension = "";

		int i = filename.lastIndexOf('.');
		int p = Math.max(filename.lastIndexOf('/'), filename.lastIndexOf('\\'));

		if (i > p) {
			extension = filename.substring(i + 1);
		}

		return extension;
	}

	public static Date getBirthFromIdCard(String idCard) {
		Date birth = null;
		String regex = "^\\d{6}(\\d{8})\\d{3}(\\d|X|x)$";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(idCard);
		boolean isIdCard = matcher.find();
		if (isIdCard) {
			String sbirth = matcher.group(1);
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
			try {
				birth = simpleDateFormat.parse(sbirth);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return birth;

	}

	public static boolean isValidName(String name) {
		if (Pattern.matches("^[A-Za-z0-9\\s\\.]{3,60}$", name)) {
			return true;
		} else if (Pattern.matches("^[A-Za-z0-9\\u4e00-\\u9fa5\\s\\.]{2,30}$", name)) {
			return true;
		} else {
			return false;
		}
	}

	public static String getUnique(long timestamp) {
		int[] table = new int[] { 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74,
				75, 76, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113,
				114, 115, 116, 117, 118, 119, 120, 121, 122 };
		String alias = "";

		char[] seq = new char[] { 48, 48, 48, 48, 48, 48, 48, 48, 48, 48 };
		seq[9] = (char) table[(int) (timestamp % 47)];
		if ((float) timestamp / 47 > 0) {
			int k = (int) ((float) timestamp / 47);
			seq[8] = (char) table[k % 47];
		}

		if ((float) timestamp / (47 * 47) > 0) {
			int k = (int) ((float) timestamp / (47 * 47));
			seq[7] = (char) table[k % 47];
		}

		if ((float) timestamp / (47 * 47 * 47) > 0) {
			int k = (int) ((float) timestamp / (47 * 47 * 47));
			seq[6] = (char) table[k % 47];
		}
		if ((float) timestamp / (47 * 47 * 47 * 47) > 0) {
			int k = (int) ((float) timestamp / (47 * 47 * 47 * 47));
			seq[5] = (char) table[k % 47];
		}
		if ((float) timestamp / (47 * 47 * 47 * 47 * 47) > 0) {
			int k = (int) ((float) timestamp / (47 * 47 * 47 * 47 * 47));
			seq[4] = (char) table[k % 47];
		}
		if ((float) timestamp / (47 * 47 * 47 * 47 * 47 * 47) > 0) {
			int k = (int) ((float) timestamp / (47 * 47 * 47 * 47 * 47 * 47));
			seq[3] = (char) table[k % 47];
		}
		if ((float) timestamp / (47 * 47 * 47 * 47 * 47 * 47 * 47) > 0) {
			int k = (int) ((float) timestamp / (47 * 47 * 47 * 47 * 47 * 47 * 47));
			seq[2] = (char) table[k % 47];
		}
		if ((float) timestamp / (47 * 47 * 47 * 47 * 47 * 47 * 47 * 47) > 0) {
			int k = (int) ((float) timestamp / (47 * 47 * 47 * 47 * 47 * 47 * 47 * 47));
			seq[1] = (char) table[k % 47];
		}
		if ((float) timestamp / (47 * 47 * 47 * 47 * 47 * 47 * 47 * 47 * 47) > 0) {
			int k = (int) ((float) timestamp / (47 * 47 * 47 * 47 * 47 * 47 * 47 * 47 * 47));
			seq[0] = (char) table[k % 47];
		}
		alias = new String(seq);
		return alias;

	}

	public static String getCaptcha() {
		char[] chars = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f',
				'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x',
				'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',
				'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };
		Random random = new Random();
		StringBuilder sb = new StringBuilder();
		int index = 0;
		for (int i = 0; i < 6; i++) {
			index = random.nextInt(62);
			sb.append(chars[index]);
		}
		return sb.toString();
	}

	public static boolean isValidCaptcha(String captcha) {
		if (!StringUtil.isEmpty(captcha)) {
			Pattern p = Pattern.compile("^[0-9a-z]{6}$");
			Matcher m = p.matcher(captcha);
			if (m.matches()) {
				return true;
			}
			return false;
		} else {
			return false;
		}

	}

	public static boolean isUniqueName(String name) {
		if (!StringUtil.isEmpty(name)) {
			Pattern p = Pattern.compile("^[0-9a-z\\-\\._]{5,40}$");
			Matcher m = p.matcher(name);
			if (m.matches()) {
				return true;
			}

			return false;
		} else {
			return false;
		}
	}

	public static String getMMDDFromBirthday(long birthday) {
		String mmdd = null;
		SimpleDateFormat format = new SimpleDateFormat("MMdd");
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(birthday);
		mmdd = format.format(cal.getTime());
		return mmdd;
	}

	public static boolean isCorrectVersion(String version) {

		boolean correct = false;
		String[] vs = version.split("\\.");
		if (vs.length == 3) {
			correct = true;
		}
		return correct;
	}
	
	public static boolean isValidateHttpURL(String url){
		if(StringUtil.isEmpty(url)){
			return false;
		}
		String regex = "^(?:https|http)?://"
				  + "(?:(?:[0-9]{1,3}\\.){3}[0-9]{1,3}" // IP形式的URL- 199.194.52.184 
				  + "|" // 允许IP和DOMAIN（域名）
				  + "(?:[0-9a-zA-Z_!~*'()-]+\\.)+" // 域名- www. 
				  + "[a-z]{2,10})" // first level domain- .com or .museum 
				  + "(?::[0-9]{1,4})?" // 端口- :80 
				  + "(?:(?:/?)|" // a slash isn't required if there is no file name 
				  + "(?:/[0-9a-zA-Z_!~*'().;?:@&=+$,%#-]+)+/?)$";
		return Pattern.matches(regex, url);
	}
	
	/**
	 * 获取当前时间戳加/减指定天的毫秒所得的值
	 * @param day 可正可负
	 * @return
	 */
	public static long getGivenTimeStamp(int day){
		long span = day * 24 * 60 * 60 * 1000;
		long given = System.currentTimeMillis() + span;
		return given;
	}
}
