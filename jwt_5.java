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
        String publicKeyUrl = "https://your-scaler-server.com/public-key-endpoint";
        try {
            URL url = new URL(publicKeyUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                return response.toString();
            } else {
                // Handle HTTP error response
                System.err.println("Error fetching public key. HTTP Error Code: " + responseCode);
                return null;
            }
        } catch (IOException e) {
            // Handle connection errors
            e.printStackTrace();
            return null;
        }
    }

    public static String fetchJWTTokenFromScalerServer() {
        String jwtTokenUrl = "https://your-scaler-server.com/jwt-token-endpoint";
        try {
            URL url = new URL(jwtTokenUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                return response.toString();
            } else {
                // Handle HTTP error response
                System.err.println("Error fetching JWT token. HTTP Error Code: " + responseCode);
                return null;
            }
        } catch (IOException e) {
            // Handle connection errors
            e.printStackTrace();
            return null;
        }
    }
}
