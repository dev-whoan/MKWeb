package com.mkweb.restapi;

import java.util.HashMap;

import com.mkweb.utils.MkJsonData;

public class MkRestApiData extends MkJsonData{
	private static HashMap<Integer, String> status = new HashMap<>(){
		{
			put(100, "CONTINUE");
			put(101, "SWITCHING PROTOCOL");
			put(102, "PROCESSING(WebDAV)");
			put(103, "EARLY HINTS");

			put(200, "OK");
			put(201, "CREATED");
			put(202, "ACCEPTED");
			put(203, "NON-AUTHORITATIVE INFORMATION");
			put(204, "NO-CONTENT");

			put(300, "MULTIPLE CHOICES");
			put(301, "MOVED PERMANENTLY");
			put(302, "FOUND");
			put(303, "SEE OTHER");
			put(304, "NOT MODIFIED");
			put(307, "TEMPORARY REDIRECT");

			put(400, "BAD REQUEST");
			put(401, "UNAUTHORIZED");
			put(403, "FORBIDDEN");
			put(404, "NOT FOUND");
			put(405, "METHOD NOT ALLOWED");
			put(406, "NOT ACCEPTABLE");
			put(412, "PRECONDITION FAILED");
			put(415, "UNSUPPORTED MEDIA TYPE");

			put(500, "INTERNAL SERVER ERROR");
			put(501, "NOT IMPLEMENTED");
		}
	};
	
	MkRestApiData(String str){
		super(str);
	}
}
