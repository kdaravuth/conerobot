package conerobotprj.dv.lib;

import javax.net.ssl.X509TrustManager;
import javax.security.cert.X509Certificate;

/**
 * Dummy class implementing X509TrustManager to trust all certificates
 */
public abstract class TrustAllCertificates implements X509TrustManager {
	public void checkClientTrusted(X509Certificate[] certs, String authType) {
	}

	public void checkServerTrusted(X509Certificate[] certs, String authType) {
	}

	public java.security.cert.X509Certificate[] getAcceptedIssuers() {
		return null;
	}
}