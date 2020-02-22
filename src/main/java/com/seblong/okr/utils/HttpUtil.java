package com.seblong.okr.utils;

import java.util.Map;

import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

public class HttpUtil {
	
	
	public static <T> T get(RestTemplate restTemplate, String url, Class<T> clazz,  Object... params) throws RestClientException{
		T response = restTemplate.getForObject(url, clazz, params);
		return response;
	}
	
	public static <T> T get(RestTemplate restTemplate, String url, MultiValueMap<String, String> params, Class<T> clazz) throws RestClientException{
		T response = restTemplate.getForObject(url, clazz, params);
		return response;
	}
	
	public static <T> T post(RestTemplate restTemplate, String url, MultiValueMap<String, String> params, Class<T> clazz) throws RestClientException{
		T response = restTemplate.postForObject(url, params, clazz);
		return response;
	}

	public static <T> T post(RestTemplate restTemplate, String url, Map<String, Object> params, Class<T> clazz) throws RestClientException{
		T response = restTemplate.postForObject(url, params, clazz);
		return response;
	}
}
