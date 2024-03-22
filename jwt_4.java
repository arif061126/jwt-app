import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class JWTVerifierExample {

    public static void main(String[] args) {
        // Step 1: Receive public key and JWT token from the scaler server
        String publicKey = fetchPublicKeyFromScalerServer();
        String jwtToken = fetchJWTTokenFromScalerServer();

        // Step 2: Verify public key and JWT token
        if (verifyPublicKeyAndJWT(publicKey, jwtToken)) {
            // Step 3: If OK, then allow the URL
            System.out.println("Access allowed.");
        } else {
            // Otherwise, do not allow
            System.out.println("Access denied.");
        }
    }

    public static boolean verifyPublicKeyAndJWT(String publicKey, String jwtToken) {
        try {
            // Create Algorithm instance with the provided public key
            Algorithm algorithm = Algorithm.RSA256(null, publicKey);

            // Create JWTVerifier instance
            JWTVerifier verifier = JWT.require(algorithm).build();

            // Verify the JWT token
            verifier.verify(jwtToken);

            // Verification successful
            return true;
        } catch (JWTVerificationException e) {
            // Verification failed
            return false;
        }
    }

    public static String fetchPublicKeyFromScalerServer() {
        // This method should implement the logic to fetch the public key from the scaler server
        // For demonstration, we'll assume a mock implementation returning a hardcoded public key
        return "-----BEGIN PUBLIC KEY-----\n" +
                "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA6PStBO2eTNXiuNcPtQXz\n" +
                "3NU/3NBZ5K1hFegz9myvlCGBd+PzLfX6O9UJ2m2Sg5VG5qj6jAl7C0rsZnZZkNyA\n" +
                "MzWjGXJJjI8wxPYzLDB3LJNxG3JihueeTmInzyvYv/X7QLt8H6kLlFPpZzGhJxFG\n" +
                "zWNdQZX2AQfrjGO4p7Mg3cVgQK27U8B3G/A1ZjzMx5n5cSlFKtMn71rQnmr1x4BU\n" +
                "SS2q8tSKYX4e0/ZzAl3cMRLjBYGwF8bwq4eTwvcmqBc1jMumgHf4tMNdYL6H8a3F\n" +
                "jqoeDKEl5yqXaH0k8+K8z9ZcYI3xR0RSdzUcHvwnafJ7yn29i4N3M0Xfdq/+3sO0\n" +
                "lwIDAQAB\n" +
                "-----END PUBLIC KEY-----";
    }

    public static String fetchJWTTokenFromScalerServer() {
        // This method should implement the logic to fetch the JWT token from the scaler server
        // For demonstration, we'll assume a mock implementation returning a hardcoded JWT token
        return "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.YFGAAdUwxWyTnrL0P1uInFbJq9fD-EukDxGvvH2_8_xmO9oZB-IRGk6r5NhZKWNSNG_cFW3qLQit7RINBp4ejP7PXavChBDyFpBN8yYkRrTJYCGQcgfqV_nH0cS9oV8OMy2i_lIbH50pZZ6GQFS4g2WGrJFyY_vxU3lOosSsnJ9I";
    }
}
