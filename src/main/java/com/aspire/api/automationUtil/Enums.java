package com.aspire.api.automationUtil;


public class Enums {

	public enum AuthenticationType {
		BASIC, DIGEST
	}

	public enum MediaType {

		WILDCARD("*/*"), 
		APPLICATION_XML("application/xml"), 
		APPLICATION_ATOM_XML("application/atom+xml"), 
		APPLICATION_XHTML_XML("application/xhtml+xml"), 
		APPLICATION_SVG_XML("application/svg+xml"), 
		APPLICATION_JSON("application/json"), 
		APPLICATION_FORM_URLENCODED("application/x-www-form-urlencoded"), 
		MULTIPART_FORM_DATA("multipart/form-data"), 
		APPLICATION_OCTET_STREAM("application/octet-stream"), 
		TEXT_PLAIN("text/plain"), 
		TEXT_XML("text/xml"), 
		TEXT_HTML("text/html");

		private final String mimeType;

		private MediaType(String mimeType) {
			this.mimeType = mimeType;
		}

		public String getMimeType() {
			return mimeType;
		}

		public static MediaType fromString(String type) {
			MediaType result = null;
			MediaType[] values = values();
			for (MediaType value : values) {
				if (value.getMimeType().equals(type)) {
					result = value;
				}
			}
			return result;
		}

		@Override
		public String toString() {
			return mimeType;
		}
	}

	public enum Status {

		// Informational 1xx
		CONTINUE(100, "Continue"), SWITCHING_PROTOCOLS(101, "Switching Protocols"),
		// Successful 2xx
		OK(200, "OK"), 
		CREATED(201, "Created"), 
		ACCEPTED(202, "Accepted"), 
		NON_AUTHORITATIVE_INFORMATION(203,"Non-Authoritative Information"), 
		NO_CONTENT(204, "No Content"), 
		RESET_CONTENT(205,"Reset Content"), 
		PARTIAL_CONTENT(206, "Partial Content"),
		// Redirection 3xx
		MULTIPLE_CHOICES(300, "Multiple Choices"), 
		MOVED_PERMANENTLY(301, "Moved Permanently"), 
		FOUND(302, "Found"), 
		SEE_OTHER(303, "See Other"), 
		NOT_MODIFIED(304, "Not Modified"), 
		USE_PROXY(305, "Use Proxy"), 
		TEMPORARY_REDIRECT(307, "Temporary Redirect"),
		// Client Error 4xx
		BAD_REQUEST(400, "Bad Request"), 
		UNAUTHORIZED(401, "Unauthorized"), 
		PAYMENT_REQUIRED(402, "Payment Required"), 
		FORBIDDEN(403, "Forbidden"), 
		NOT_FOUND(404, "Not Found"), 
		METHOD_NOT_ALLOWED(405, "Method Not Allowed"), 
		NOT_ACCEPTABLE(406,"Not Acceptable"), 
		PROXY_AUTHENTICATION_REQUIRED(407,"Proxy Authentication Required"), 
		REQUEST_TIMEOUT(408,"Request Timeout"), 
		CONFLICT(409, "Conflict"), 
		GONE(410, "Gone"), 
		LENGTH_REQUIRED(411,"Length Required"), 
		PRECONDITION_FAILED(412, "Precondition Failed"), 
		REQUEST_ENTITY_TOO_LARGE(413,"Request Entity Too Large"), 
		REQUEST_URI_TOO_LONG(414,"Request-URI Too Long"), 
		UNSUPPORTED_MEDIA_TYPE(415, "Unsupported Media Type"), 
		REQUEST_RANGE_NOT_SATISFIABLE(416, "Requested Range Not Satisfiable"), 
		EXPECTATION_FAILED(417, "Expectation Failed"),
		// Server
		// Error
		// 5xx
		INTERNAL_SERVER_ERROR(500, "Internal Server Error"), 
		NOT_IMPLEMENTED(501, "Not Implemented"), 
		BAD_GATEWAY(502, "Bad Gateway"), 
		SERVICE_UNAVAILABLE(503, "Service Unavailable"), 
		GATEWAY_TIMEOUT(504, "Gateway Timeout"), 
		HTTP_VERSION_NOT_SUPPORTED(505, "HTTP Version Not Supported");

		public enum Family {
			INFORMATIONAL, SUCCESSFUL, REDIRECTION, CLIENT_ERROR, SERVER_ERROR, UNKNOWN
		}

		private final int code;
		private final String reason;
		private Family family;

		Status(int statusCode, String reasonPhrase) {
			this.code = statusCode;
			this.reason = reasonPhrase;
			assignFamily();
		}

		private void assignFamily() {
			switch (code / 100) {
			case 1:
				this.family = Family.INFORMATIONAL;
				break;
			case 2:
				this.family = Family.SUCCESSFUL;
				break;
			case 3:
				this.family = Family.REDIRECTION;
				break;
			case 4:
				this.family = Family.CLIENT_ERROR;
				break;
			case 5:
				this.family = Family.SERVER_ERROR;
				break;
			default:
				this.family = Family.UNKNOWN;
				break;
			}
		}

		public int getStatusCode() {
			return code;
		}

		public Family getFamily() {
			return family;
		}

		public static Status forStatusCode(int code) {
			Status[] values = Status.values();
			for (Status status : values) {
				if (status.getStatusCode() == code) {
					return status;
				}
			}
			return null;
		}

		@Override
		public String toString() {
			return reason;
		}
	}

	public enum Method {
		GET,
		POST, 
		PUT, 
		DELETE
	}

//	public enum OAuthSignature {
//		HEADER, QUERY_STRING
//	}

}
