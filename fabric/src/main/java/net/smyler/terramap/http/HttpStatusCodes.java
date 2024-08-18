package net.smyler.terramap.http;


/**
 * HTTP response codes.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Status">MDN documentation</a>
 */
public final class HttpStatusCodes {

    // Information responses
    public static final int HTTP_CONTINUE = 100;
    public static final int HTTP_SWITCHING_PROTOCOLS = 101;
    public static final int HTTP_PROCESSING = 102;
    public static final int HTTP_EARLY_HINTS = 103;

    // Successful responses
    public static final int HTTP_OK = 200;
    public static final int HTTP_CREATED = 201;
    public static final int HTTP_ACCEPTED = 202;
    public static final int HTTP_NON_AUTHORITATIVE_INFORMATION = 203;
    public static final int HTTP_NO_CONTENT = 204;
    public static final int HTTP_RESET_CONTENT = 205;
    public static final int HTTP_PARTIAL_CONTENT = 206;
    public static final int HTTP_MULTI_STATUS = 207;
    public static final int HTTP_ALREADY_REPORTED = 208;
    public static final int HTTP_IM_USED = 209;

    // Redirection message
    public static final int HTTP_MULTIPLE_CHOICES = 300;
    public static final int HTTP_MOVED_PERMANENTLY = 301;
    public static final int HTTP_FOUND = 302;
    public static final int HTTP_SEE_OTHER = 303;
    public static final int HTTP_NOT_MODIFIED = 304;
    public static final int HTTP_USE_PROXY = 305;
    public static final int HTTP_UNUSED = 306;
    public static final int HTTP_TEMPORARY_REDIRECT = 307;
    public static final int HTTP_PERMANENT_REDIRECT = 308;

    // Client error response
    public static final int HTTP_BAD_REQUEST = 400;
    public static final int HTTP_UNAUTHORIZED = 401;
    public static final int HTTP_PAYMENT_REQUIRED = 402;
    public static final int HTTP_FORBIDDEN = 403;
    public static final int HTTP_NOT_FOUND = 404;
    public static final int HTTP_METHOD_NOT_ALLOWED = 405;
    public static final int HTTP_NOT_ACCEPTABLE = 406;
    public static final int HTTP_PROXY_AUTHENTICATION_REQUIRED = 407;
    public static final int HTTP_REQUEST_TIMEOUT = 408;
    public static final int HTTP_CONFLICT = 409;
    public static final int HTTP_GONE = 410;
    public static final int HTTP_LENGTH_REQUIRED = 411;
    public static final int HTTP_PRECONDITION_FAILED = 412;
    public static final int HTTP_REQUEST_ENTITY_TOO_LARGE = 413;
    public static final int HTTP_REQUEST_URI_TOO_LONG = 414;
    public static final int HTTP_UNSUPPORTED_MEDIA_TYPE = 415;
    public static final int HTTP_REQUESTED_RANGE_NOT_SATISFIABLE = 416;
    public static final int HTTP_EXPECTATION_FAILED = 417;
    public static final int HTTP_I_M_A_TEAPOT = 418;
    public static final int HTTP_UNPROCESSABLE_ENTITY = 422;
    public static final int HTTP_LOCKED = 423;
    public static final int HTTP_FAILED_DEPENDENCY = 424;
    public static final int HTTP_TOO_EARLY = 425;
    public static final int HTTP_UPGRADE_REQUIRED = 426;
    public static final int HTTP_PRECONDITION_REQUIRED = 428;
    public static final int HTTP_TOO_MANY_REQUESTS = 429;
    public static final int HTTP_REQUEST_HEADER_FIELDS_TOO_LARGE = 431;
    public static final int HTTP_UNAVAILABLE_FOR_LEGAL_REASONS = 432;

    // Server error response
    public static final int HTTP_INTERNAL_SERVER_ERROR = 500;
    public static final int HTTP_NOT_IMPLEMENTED = 501;
    public static final int HTTP_BAD_GATEWAY = 502;
    public static final int HTTP_SERVICE_UNAVAILABLE = 503;
    public static final int HTTP_GATEWAY_TIMEOUT = 504;
    public static final int HTTP_HTTP_VERSION_NOT_SUPPORTED = 505;
    public static final int HTTP_VARIANT_ALSO_NEGOTIATES = 506;
    public static final int HTTP_INSUFFICIENT_STORAGE = 507;
    public static final int HTTP_LOOP_DETECTED = 508;
    public static final int HTTP_NOT_EXTENDED = 510;
    public static final int HTTP_NETWORK_AUTHENTICATION_REQUIRED = 511;

    private HttpStatusCodes() {}

}
