package com.lenovo.springmvc.demo.utils;

import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;

public class Bash64Utils {
	
	public static String enBase64(String source) {
		Encoder base = Base64.getEncoder();
		return new String(base.encode(source.getBytes()));
	}

	public static String deBase64(String source) {
		Decoder base = Base64.getDecoder();
		return new String(base.decode(source.getBytes()));
	}
}
