package com.seblong.okr.utils;

import org.json.JSONObject;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: tracywang
 * @date: 2015-12-1
 * @version:
 */
public class ResponseUtil {

	public static void outputJson(int status, String msg, HttpServletResponse response) {
		enableCORS(response);
		Map<String, Object> rMap = new HashMap<String, Object>();
		rMap.put("status", status);
		rMap.put("message", msg);
		JSONObject object = new JSONObject(rMap);
		try {
			response.setContentType("application/json;charset=UTF-8");
			response.getWriter().write(object.toString());
			response.getWriter().close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return;
	}

	public static void outputJson(Map<String, Object> rMap, HttpServletResponse response) {

		enableCORS(response);
		JSONObject object = new JSONObject(rMap);
		try {
			response.setContentType("application/json;charset=UTF-8");
			response.getWriter().write(object.toString());
			response.getWriter().close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return;
	}

	private static void enableCORS(HttpServletResponse response) {

		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Methods",
				"PROPFIND, PROPPATCH, COPY, MOVE, DELETE, MKCOL, LOCK, UNLOCK, PUT, GETLIB, VERSION-CONTROL, CHECKIN, CHECKOUT, "
						+ "UNCHECKOUT, REPORT, UPDATE, CANCELUPLOAD, HEAD, OPTIONS, GET, POST");
		response.setHeader("Access-Control-Max-Age", "3600");
		response.setHeader("Access-Control-Allow-Credentials", "true");
		response.setHeader("Access-Control-Allow-Headers",
				"Origin, Authorization, Overwrite, Destination, Content-Type, Depth, User-Agent, X-File-Size, X-Requested-With, "
						+ "If-Modified-Since, X-File-Name, Cache-Control, User-Session, Session-Token, Session-Site");

	}
}
