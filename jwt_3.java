import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;

public class Main {
    public static void main(String[] args) {
        String publicKeyUrl = "https://example.com/public-key";
        String jwtTokenUrl = "https://example.com/jwt-token";

        try {
            // Step 1: Receive public key from URL
            PublicKey publicKey = getPublicKeyFromURL(publicKeyUrl);

            // Step 2: Receive JWT token from URL
            String jwtToken = getJWTTokenFromURL(jwtTokenUrl);

            // Step 3: Verify public key and JWT token
            if (verifyJWTToken(jwtToken, publicKey)) {
                // If OK, allow URL
                System.out.println("Access allowed.");
            } else {
                // Otherwise, do not allow
                System.out.println("Access denied.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static PublicKey getPublicKeyFromURL(String publicKeyUrl) throws Exception {
        URL url = new URL(publicKeyUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        StringBuilder response = new StringBuilder();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
        }

        // Convert public key string to PublicKey object
        byte[] publicKeyBytes = Base64.getDecoder().decode(response.toString());
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        X509Certificate cert = (X509Certificate) cf.generateCertificate(new java.io.ByteArrayInputStream(publicKeyBytes));
        return cert.getPublicKey();
    }

    private static String getJWTTokenFromURL(String jwtTokenUrl) throws IOException {
        URL url = new URL(jwtTokenUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        StringBuilder response = new StringBuilder();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
        }
        return response.toString();
    }

    private static boolean verifyJWTToken(String jwtToken, PublicKey publicKey) throws Exception {
        // Split JWT token to get signature and payload
        String[] jwtParts = jwtToken.split("\\.");
        String signatureBase64 = jwtParts[2];

        // Decode signature
        byte[] signatureBytes = Base64.getDecoder().decode(signatureBase64);

        // Initialize Signature object with public key
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initVerify(publicKey);

        // Load payload for verification
        String payload = jwtParts[0] + "." + jwtParts[1];
        byte[] payloadBytes = payload.getBytes("UTF-8");

        // Verify the signature
        signature.update(payloadBytes);
        return signature.verify(signatureBytes);
    }
}
