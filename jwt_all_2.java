//import android.util.Base64;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.security.*;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import java.util.*;

import org.json.JSONException;
import org.json.JSONObject;

public class JWTVerificationApp2 {

    //static Algorithm algorithm = null;
    //private Algorithm algorithm;

    public static void main(String[] args) {

        String publicKeyString = null;
        String jwtToken = null;
        String secretKey = null;

        System.out.println(args.length);
        if (args.length==3)
        {
            publicKeyString = args[0];
            jwtToken = args[1];
            secretKey = args[2];
        } else if (args.length==2) {
            publicKeyString = args[0];
            jwtToken = args[1];
        } else {
            System.exit(400);
            return;
        }

/*        Scanner input = new Scanner(System.in);
        System.out.println("input jwt token: ");
        jwtToken = input.nextLine();
        System.out.println("input public key: ");
        publicKeyString = input.nextLine();*/

        // Hardcoded public key and JWT token with RSA256 Algorithm
        /*
        publicKeyString = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAu1SU1LfVLPHCozMxH2Mo4lgOEePzNm0tRgeLezV6ffAt0gunVTLw7onLRnrq0/IzW7yWR7QkrmBL7jTKEn5u+qKhbwKfBstIs+bMY2Zkp18gnTxKLxoS2tFczGkPLPgizskuemMghRniWaoLcyehkd3qqGElvW/VDL5AaWTg0nLVkjRo9z+40RQzuVaE8AkAFmxZzow3x+VJYKdjykkJ0iT9wCS0DRTXu269V264Vf/3jvredZiKRkgwlL9xNAwxXFg0x/XFw005UWVRIkdgcKWTjpBP2dPwVZ4WWC+9aGVd+Gyn1o0CLelf4rEjGoXbAAEgAqeGUxrcIlbjXfbcmwIDAQAB";

        jwtToken = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWUsImlhdCI6MTUxNjIzOTAyMn0.NHVaYe26MbtOYhSKkoKYdFVomg4i8ZJd8_-RU8VNbftc4TSMb4bXP3l3YlNWACwyXPGffz5aXHc6lty1Y2t4SWRqGteragsVdZufDn5BlnJl9pdR_kdVFUsra2rWKEofkZeIC4yWytE58sMIihvo9H1ScmmVwBcQP6XETqYd0aSHp1gOa9RdUPDvoXQ5oqygTqVtxaDr6wUFKrKItgBMzWIdNZ6y7O9E0DhEPTbE9rfBo6KTFsHAZnMg4k68CDp2woYIaXbmYTWcvbzIuHO7_37GT79XdIwkm95QJ7hYC9RiwrV7mesbY4PAahERJawntho0my942XheVLmGwLMBkQ";
        */
        //New publicKey and JWT token for RS256 Algorithm
        /*
        publicKeyString = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAllmXiQx/tlrnQ8jbQB5P" +
                "9FLr9V/fnUqt+J6AJAdz+QuQxbMQ+k2w0H0EKgQEKKW9PkGiJ/Zzk1mGkyBVspi3" +
                "H1h/sR33xTyTUVAeMZLS/S6UhCDfmHcXjB0O4rQgDTm9yYXlvricUZy70s2KWU0R" +
                "iUs+IDHin+TtRHzYlJmXtEL2pS+R7r/cIgB0QQft570+hV4o2CY14E5FC2jRTR7k" +
                "mLSDgGC/nVPbjzQaLwAC4dSUKz/er1dvWxdaE2Zg9WdZHfw80CeE58CEDVBMwDFQ" +
                "hkys9q1OSQwXyJGw83U5Pvc7p8I0UILYqbRFSFtcHnsdVpRwsPs/bpFugb4FbATE" +
                "TwIDAQAB";

        jwtToken = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6InN0c19TSEExd2l0aFJTQSJ9.eyJpc3MiOiJzdHMiLCJzdWIiOiJ1aWQ6VGVzdFVzZXIiLCJhdWQiOlsiYXVkMSIsImF1ZDIiLCJhdWQzIl0sIm5iZiI6MTcxMjEzMTQ5NCwiZXhwIjoxNzEyMTM1MDk0LCJpYXQiOjE3MTIxMzE0OTQsImp0aSI6ImlkMTIzNDU2Iiwic2lkIjoic2Vzc2lvbklkIiwic2VjQ3R4Ijp7ImJvbGFTZWciOnsiY3VzdG9tZXJJZCI6ImNsaWVudCIsImNvbnRyYWN0cyI6WyJjb250cmFjdHMxIiwiY29udHJhY3RzMiIsImNvbnRyYWN0czMiLCJjb250cmFjdHM0Il19LCJhbGciOiJTSEEyNTYiLCJoYXNoIjoiUS8yYlJuY2lnV05qMUpKQWZ4L2tQZHpWZG9qekQ0eXcyTzZweE0wSFBiQT0ifX0.GES-fvsW8y6oa_aeXXQ9bmLFRdS9Lvf0seESwka59MX_6z3n0NnpVmNCuhvCdYh4mj3umv4ZqOnx_6EZL-2jl8E3nm5vKbkAm0535PyfRVlUXbYImZ4BZcojLA1GFM6rNf5Sne6hC60Cxyizk5wAGQ4hiN1bNqB0CS2_QxNPT-dN462KivcdX7IN3cYCWcDYVpc3z8MMsllQTSim4PHrW-8T7JE-y2eubrWHLPVk1zg6DwpG9aIj6izTIv6QXs9G98685NrMwD0sjCd-hA2Ao00XIh70yAqKhru1cYBYJf5B8gp8Y2UfYW3CUquUUngQEOHHy9qB1YF2Wb6uHoHlDQ";
        */

        //Hardcoded jwtToken for HS256 algorithm:
        //jwtToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.XbPfbIHMI6arZ3Y922BhjWgQzWXcXNrz0ogtVhfEd2o";

        //jwtToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IkFQSW1JbnRlcm5ldEdlcm1hbnlfSFMyNTYifQ.eyJpc3MiOiJzdHMiLCJzdWIiOiJ1aWQ6VGVzdFVzZXIiLCJhdWQiOlsiYXVkMSIsImF1ZDIiLCJhdWQzIl0sIm5iZiI6MTcxMjA0ODc3NywiZXhwIjoxNzEyMDUyMzc3LCJpYXQiOjE3MTIwNDg3NzcsImp0aSI6ImlkMTIzNDU2Iiwic2lkIjoic2Vzc2lvbklkIiwic2VjQ3R4Ijp7ImJvbGFTZWciOnsiY3VzdG9tZXJJZCI6ImNsaWVudCIsImNvbnRyYWN0cyI6WyJjb250cmFjdHMxIiwiY29udHJhY3RzMiIsImNvbnRyYWN0czMiLCJjb250cmFjdHM0Il19LCJhbGciOiJTSEEyNTYiLCJoYXNoIjoiUS8yYlJuY2lnV05qMUpKQWZ4L2tQZHpWZG9qekQ0eXcyTzZweE0wSFBiQT0ifX0.B6eIxXoBafhugFfGnHV2Ndj-YyTF9Ia4VLV69varyOY";
        //secretKey = "secret";

        //Hardcoded jwtToken for HS384 algorithm:
        //jwtToken = "eyJhbGciOiJIUzM4NCIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWUsImlhdCI6MTUxNjIzOTAyMn0.np_8QucI7cN6tJo0Fvm3i_eVQEHraSJPRC87yq2UQb77gRWMeeca8zDVIaTlVuZk";

        //jwtToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IkFQSW1JbnRlcm5ldEdlcm1hbnlfSFMyNTYifQ.eyJpc3MiOiJzdHMiLCJzdWIiOiJ1aWQ6VGVzdFVzZXIiLCJhdWQiOlsiYXVkMSIsImF1ZDIiLCJhdWQzIl0sIm5iZiI6MTcxMjA0ODc3NywiZXhwIjoxNzEyMDUyMzc3LCJpYXQiOjE3MTIwNDg3NzcsImp0aSI6ImlkMTIzNDU2Iiwic2lkIjoic2Vzc2lvbklkIiwic2VjQ3R4Ijp7ImJvbGFTZWciOnsiY3VzdG9tZXJJZCI6ImNsaWVudCIsImNvbnRyYWN0cyI6WyJjb250cmFjdHMxIiwiY29udHJhY3RzMiIsImNvbnRyYWN0czMiLCJjb250cmFjdHM0Il19LCJhbGciOiJTSEEyNTYiLCJoYXNoIjoiUS8yYlJuY2lnV05qMUpKQWZ4L2tQZHpWZG9qekQ0eXcyTzZweE0wSFBiQT0ifX0.B6eIxXoBafhugFfGnHV2Ndj-YyTF9Ia4VLV69varyOY";
        //secretKey = "secret";

        //Hardcoded jwtToken for HS512 algorithm:
        //jwtToken = "eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWUsImlhdCI6MTUxNjIzOTAyMn0.nZU_gPcMXkWpkCUpJceSxS7lSickF0tTImHhAR949Z-Nt69LgW8G6lid-mqd9B579tYM8C4FN2jdhR2VRMsjtA";
        //jwtToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IkFQSW1JbnRlcm5ldEdlcm1hbnlfSFMyNTYifQ.eyJpc3MiOiJzdHMiLCJzdWIiOiJ1aWQ6VGVzdFVzZXIiLCJhdWQiOlsiYXVkMSIsImF1ZDIiLCJhdWQzIl0sIm5iZiI6MTcxMjA0ODc3NywiZXhwIjoxNzEyMDUyMzc3LCJpYXQiOjE3MTIwNDg3NzcsImp0aSI6ImlkMTIzNDU2Iiwic2lkIjoic2Vzc2lvbklkIiwic2VjQ3R4Ijp7ImJvbGFTZWciOnsiY3VzdG9tZXJJZCI6ImNsaWVudCIsImNvbnRyYWN0cyI6WyJjb250cmFjdHMxIiwiY29udHJhY3RzMiIsImNvbnRyYWN0czMiLCJjb250cmFjdHM0Il19LCJhbGciOiJTSEEyNTYiLCJoYXNoIjoiUS8yYlJuY2lnV05qMUpKQWZ4L2tQZHpWZG9qekQ0eXcyTzZweE0wSFBiQT0ifX0.B6eIxXoBafhugFfGnHV2Ndj-YyTF9Ia4VLV69varyOY";
        //secretKey = "secret";

        //New publicKey and JWT token for RS384 Algorithm
        /*
        publicKeyString = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAllmXiQx/tlrnQ8jbQB5P" +
                "9FLr9V/fnUqt+J6AJAdz+QuQxbMQ+k2w0H0EKgQEKKW9PkGiJ/Zzk1mGkyBVspi3" +
                "H1h/sR33xTyTUVAeMZLS/S6UhCDfmHcXjB0O4rQgDTm9yYXlvricUZy70s2KWU0R" +
                "iUs+IDHin+TtRHzYlJmXtEL2pS+R7r/cIgB0QQft570+hV4o2CY14E5FC2jRTR7k" +
                "mLSDgGC/nVPbjzQaLwAC4dSUKz/er1dvWxdaE2Zg9WdZHfw80CeE58CEDVBMwDFQ" +
                "hkys9q1OSQwXyJGw83U5Pvc7p8I0UILYqbRFSFtcHnsdVpRwsPs/bpFugb4FbATE" +
                "TwIDAQAB";

        jwtToken = "eyJhbGciOiJSUzM4NCIsInR5cCI6IkpXVCIsImtpZCI6InN0c19TSEExd2l0aFJTQSJ9.eyJpc3MiOiJzdHMiLCJzdWIiOiJ1aWQ6VGVzdFVzZXIiLCJhdWQiOlsiYXVkMSIsImF1ZDIiLCJhdWQzIl0sIm5iZiI6MTcxMjEzMTY2OSwiZXhwIjoxNzEyMTM1MjY5LCJpYXQiOjE3MTIxMzE2NjksImp0aSI6ImlkMTIzNDU2Iiwic2lkIjoic2Vzc2lvbklkIiwic2VjQ3R4Ijp7ImJvbGFTZWciOnsiY3VzdG9tZXJJZCI6ImNsaWVudCIsImNvbnRyYWN0cyI6WyJjb250cmFjdHMxIiwiY29udHJhY3RzMiIsImNvbnRyYWN0czMiLCJjb250cmFjdHM0Il19LCJhbGciOiJTSEEyNTYiLCJoYXNoIjoiUS8yYlJuY2lnV05qMUpKQWZ4L2tQZHpWZG9qekQ0eXcyTzZweE0wSFBiQT0ifX0.IYj2JQYGtD77ltnlrhqoTeybx9HB1TpN8ONt0GZMLnZsyL8Q59hQryhik-sPS_zxJuOFiZful0t1wN40IgupjtGwUutgvZkx5YjJuLqssG9lLwwl-8Mbov8HJR5n8NXKxmv52qU8sN96ui5feR2og3T3Rjt4W1hi5s_BGm3n6FvwTDnB-THbTk6BgKIMyighLi0-qXEBCL6lInT66K0shRBfMeeRPawwei27Xv-DsUwNHa1Cs-vJlKS2I1l34pGNQO76Jr8d3pASIcvwehtlHh2_MbT9EYH59TjS-XiQoI3u06Cbq-KHhOOGdIMsJbrm2fRAnwoOQRbAC6T-q9CpeQ";
        */
        //New publicKey and JWT token for RS512 Algorithm
        /*publicKeyString = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAllmXiQx/tlrnQ8jbQB5P" +
                "9FLr9V/fnUqt+J6AJAdz+QuQxbMQ+k2w0H0EKgQEKKW9PkGiJ/Zzk1mGkyBVspi3" +
                "H1h/sR33xTyTUVAeMZLS/S6UhCDfmHcXjB0O4rQgDTm9yYXlvricUZy70s2KWU0R" +
                "iUs+IDHin+TtRHzYlJmXtEL2pS+R7r/cIgB0QQft570+hV4o2CY14E5FC2jRTR7k" +
                "mLSDgGC/nVPbjzQaLwAC4dSUKz/er1dvWxdaE2Zg9WdZHfw80CeE58CEDVBMwDFQ" +
                "hkys9q1OSQwXyJGw83U5Pvc7p8I0UILYqbRFSFtcHnsdVpRwsPs/bpFugb4FbATE" +
                "TwIDAQAB";

        jwtToken = "eyJhbGciOiJSUzUxMiIsInR5cCI6IkpXVCIsImtpZCI6IkFQSW1JbnRlcm5l" +
                "dEdlcm1hbnlfUlM1MTIifQ.eyJpc3MiOiJzdHMiLCJzdWIiOiJ1aWQ6VGVzdFVzZXIiLCJ" +
                "hdWQiOlsiYXVkMSIsImF1ZDIiLCJhdWQzIl0sIm5iZiI6MTcxMjA1MDE1MiwiZXhwIjoxN" +
                "zEyMDUzNzUyLCJpYXQiOjE3MTIwNTAxNTIsImp0aSI6ImlkMTIzNDU2Iiwic2lkIjoic2V" +
                "zc2lvbklkIiwic2VjQ3R4Ijp7ImJvbGFTZWciOnsiY3VzdG9tZXJJZCI6ImNsaWVudCIsI" +
                "mNvbnRyYWN0cyI6WyJjb250cmFjdHMxIiwiY29udHJhY3RzMiIsImNvbnRyYWN0czMiLCJ" +
                "jb250cmFjdHM0Il19LCJhbGciOiJTSEEyNTYiLCJoYXNoIjoiUS8yYlJuY2lnV05qMUpKQ" +
                "WZ4L2tQZHpWZG9qekQ0eXcyTzZweE0wSFBiQT0ifX0.Y68T8ExTqDZrtj4syMoJws-eG0r" +
                "dpExiPm7y31iY9aYsAbaNZlXBgBJ5NvulVdv2816egM7kRW27bWqm2MoLC1PYh6ywzj0cM" +
                "W9YLUSHe9qyHVsop5UMsK_dPWjcSTUPRuGnbjNDtJdYJUHiRtYCtgrijnctxL3M-xs7QNT" +
                "W8CEcVp4Bi1SFR4yAiF_Mc88CaexrQjRzttbYPuzDV4i3dSpIzIymil80iTXn3bZy7OQy9" +
                "Ywoup-FmD2qztkrL2gotlR0EI1J0DitQl7Y54aSQoi5Xs1QYMfqsgkV88LFgTLubTuQo6X" +
                "D0cjEGUTLLULTcbY0MddqANeNpi4zr9zwQQ";
        */

        // Hardcoded public key and JWT token for ES256 Algorithm
        /*
        publicKeyString = "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEoBUyo8CQAFPeYPvv78ylh5MwFZjT" +
                "CLQeb042TjiMJxG+9DLFmRSMlBQ9T/RsLLc+PmpB1+7yPAR+oR5gZn3kJQ==";

        jwtToken = "eyJhbGciOiJFUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6InN0c19TSEExd2l0aFJTQSJ9.eyJpc3MiOiJzdHMiLCJzdWIiOiJ1aWQ6VGVzdFVzZXIiLCJhdWQiOlsiYXVkMSIsImF1ZDIiLCJhdWQzIl0sIm5iZiI6MTcxMjA1MTY0OCwiZXhwIjoxNzEyMDU1MjQ4LCJpYXQiOjE3MTIwNTE2NDgsImp0aSI6ImlkMTIzNDU2Iiwic2lkIjoic2Vzc2lvbklkIiwic2VjQ3R4Ijp7ImJvbGFTZWciOnsiY3VzdG9tZXJJZCI6ImNsaWVudCIsImNvbnRyYWN0cyI6WyJjb250cmFjdHMxIiwiY29udHJhY3RzMiIsImNvbnRyYWN0czMiLCJjb250cmFjdHM0Il19LCJhbGciOiJTSEEyNTYiLCJoYXNoIjoiUS8yYlJuY2lnV05qMUpKQWZ4L2tQZHpWZG9qekQ0eXcyTzZweE0wSFBiQT0ifX0.4ZcDMM50H4GVqddKXpR8rsj_l4isuZq_VwPMwwC9LpPaqdPU-byYSYdHL53OSNMxrnqm5Xh5vKB91qvZhSez-Q";
        */

        // Hardcoded public key and JWT token for ES384 Algorithm
        /*
        publicKeyString = "MHYwEAYHKoZIzj0CAQYFK4EEACIDYgAEC1uWSXj2czCDwMTLWV5BFmwxdM6PX9p+" +
                "Pk9Yf9rIf374m5XP1U8q79dBhLSIuaojsvOT39UUcPJROSD1FqYLued0rXiooIii" +
                "1D3jaW6pmGVJFhodzC31cy5sfOYotrzF";

        jwtToken = "eyJhbGciOiJFUzM4NCIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWUsImlhdCI6MTUxNjIzOTAyMn0.VUPWQZuClnkFbaEKCsPy7CZVMh5wxbCSpaAWFLpnTe9J0--PzHNeTFNXCrVHysAa3eFbuzD8_bLSsgTKC8SzHxRVSj5eN86vBPo_1fNfE7SHTYhWowjY4E_wuiC13yoj";
        */

        // Hardcoded public key and JWT token for ES512 Algorithm
        /*
        publicKeyString = "MIGbMBAGByqGSM49AgEGBSuBBAAjA4GGAAQBgc4HZz+/fBbC7lmEww0AO3NK9wVZ" +
                "PDZ0VEnsaUFLEYpTzb90nITtJUcPUbvOsdZIZ1Q8fnbquAYgxXL5UgHMoywAib47" +
                "6MkyyYgPk0BXZq3mq4zImTRNuaU9slj9TVJ3ScT3L1bXwVuPJDzpr5GOFpaj+WwM" +
                "Al8G7CqwoJOsW7Kddns=";

        jwtToken = "eyJhbGciOiJFUzUxMiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWUsImlhdCI6MTUxNjIzOTAyMn0.AbVUinMiT3J_03je8WTOIl-VdggzvoFgnOsdouAs-DLOtQzau9valrq-S6pETyi9Q18HH-EuwX49Q7m3KC0GuNBJAc9Tksulgsdq8GqwIqZqDKmG7hNmDzaQG1Dpdezn2qzv-otf3ZZe-qNOXUMRImGekfQFIuH_MjD2e8RZyww6lbZk";
        */

        // Hardcoded public key and JWT token for PS256 Algorithm
        /*
        publicKeyString = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAu1SU1LfVLPHCozMxH2Mo" +
                "4lgOEePzNm0tRgeLezV6ffAt0gunVTLw7onLRnrq0/IzW7yWR7QkrmBL7jTKEn5u" +
                "+qKhbwKfBstIs+bMY2Zkp18gnTxKLxoS2tFczGkPLPgizskuemMghRniWaoLcyeh" +
                "kd3qqGElvW/VDL5AaWTg0nLVkjRo9z+40RQzuVaE8AkAFmxZzow3x+VJYKdjykkJ" +
                "0iT9wCS0DRTXu269V264Vf/3jvredZiKRkgwlL9xNAwxXFg0x/XFw005UWVRIkdg" +
                "cKWTjpBP2dPwVZ4WWC+9aGVd+Gyn1o0CLelf4rEjGoXbAAEgAqeGUxrcIlbjXfbc" +
                "mwIDAQAB";

        jwtToken = "eyJhbGciOiJQUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWUsImlhdCI6MTUxNjIzOTAyMn0.iOeNU4dAFFeBwNj6qdhdvm-IvDQrTa6R22lQVJVuWJxorJfeQww5Nwsra0PjaOYhAMj9jNMO5YLmud8U7iQ5gJK2zYyepeSuXhfSi8yjFZfRiSkelqSkU19I-Ja8aQBDbqXf2SAWA8mHF8VS3F08rgEaLCyv98fLLH4vSvsJGf6ueZSLKDVXz24rZRXGWtYYk_OYYTVgR1cg0BLCsuCvqZvHleImJKiWmtS0-CymMO4MMjCy_FIl6I56NqLE9C87tUVpo1mT-kbg5cHDD8I7MjCW5Iii5dethB4Vid3mZ6emKjVYgXrtkOQ-JyGMh6fnQxEFN1ft33GX2eRHluK9eg";
        */

        // New public key and JWT token for PS256 Algorithm
        /*
        publicKeyString = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAllmXiQx/tlrnQ8jbQB5P" +
                "9FLr9V/fnUqt+J6AJAdz+QuQxbMQ+k2w0H0EKgQEKKW9PkGiJ/Zzk1mGkyBVspi3" +
                "H1h/sR33xTyTUVAeMZLS/S6UhCDfmHcXjB0O4rQgDTm9yYXlvricUZy70s2KWU0R" +
                "iUs+IDHin+TtRHzYlJmXtEL2pS+R7r/cIgB0QQft570+hV4o2CY14E5FC2jRTR7k" +
                "mLSDgGC/nVPbjzQaLwAC4dSUKz/er1dvWxdaE2Zg9WdZHfw80CeE58CEDVBMwDFQ" +
                "hkys9q1OSQwXyJGw83U5Pvc7p8I0UILYqbRFSFtcHnsdVpRwsPs/bpFugb4FbATE" +
                "TwIDAQAB";

        jwtToken = "eyJhbGciOiJQUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6InN0c19TSEExd2l0aFJTQSJ9.eyJpc3MiOiJzdHMiLCJzdWIiOiJ1aWQ6VGVzdFVzZXIiLCJhdWQiOlsiYXVkMSIsImF1ZDIiLCJhdWQzIl0sIm5iZiI6MTcxMjEyOTY3OSwiZXhwIjoxNzEyMTMzMjc5LCJpYXQiOjE3MTIxMjk2NzksImp0aSI6ImlkMTIzNDU2Iiwic2lkIjoic2Vzc2lvbklkIiwic2VjQ3R4Ijp7ImJvbGFTZWciOnsiY3VzdG9tZXJJZCI6ImNsaWVudCIsImNvbnRyYWN0cyI6WyJjb250cmFjdHMxIiwiY29udHJhY3RzMiIsImNvbnRyYWN0czMiLCJjb250cmFjdHM0Il19LCJhbGciOiJTSEEyNTYiLCJoYXNoIjoiUS8yYlJuY2lnV05qMUpKQWZ4L2tQZHpWZG9qekQ0eXcyTzZweE0wSFBiQT0ifX0.iqgFCxOcAHh7O7c6UlEKTfPBHM_lIEmtGkiwJNa4Ax2CAMyjPmgt0AN38qQxnduIMU9jM-V9YGvW89q475MVU4TEuplYw58h_7oFtf8cOgV32tQ0DQkAGg0HGpjMUObc0fKbFWXoyMlBG9sSGpEoOdbVz3z0HUP5vWTx_ljraKVbs9eAdxo2K1yboZWFL3DP7aignDCl77Ki8XJUVwHMkbFO6_xtbnPSJ5X0K7pzZT3aDUSBKOpQtFMCl5CyceolYBuyMACR8HVqKKKKEisbm4ynpzQVDpSYkk_SBQ1hSwf0ClTc9Y5bMihLlFJecQRusIZzMZyJ7jBoi3L6ELhiFg";
        */
        // Hardcoded public key and JWT token for PS384 Algorithm
        /*
        publicKeyString = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAu1SU1LfVLPHCozMxH2Mo" +
                "4lgOEePzNm0tRgeLezV6ffAt0gunVTLw7onLRnrq0/IzW7yWR7QkrmBL7jTKEn5u" +
                "+qKhbwKfBstIs+bMY2Zkp18gnTxKLxoS2tFczGkPLPgizskuemMghRniWaoLcyeh" +
                "kd3qqGElvW/VDL5AaWTg0nLVkjRo9z+40RQzuVaE8AkAFmxZzow3x+VJYKdjykkJ" +
                "0iT9wCS0DRTXu269V264Vf/3jvredZiKRkgwlL9xNAwxXFg0x/XFw005UWVRIkdg" +
                "cKWTjpBP2dPwVZ4WWC+9aGVd+Gyn1o0CLelf4rEjGoXbAAEgAqeGUxrcIlbjXfbc" +
                "mwIDAQAB";

        jwtToken = "eyJhbGciOiJQUzM4NCIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWUsImlhdCI6MTUxNjIzOTAyMn0.Lfe_aCQme_gQpUk9-6l9qesu0QYZtfdzfy08w8uqqPH_gnw-IVyQwyGLBHPFBJHMbifdSMxPjJjkCD0laIclhnBhowILu6k66_5Y2z78GHg8YjKocAvB-wSUiBhuV6hXVxE5emSjhfVz2OwiCk2bfk2hziRpkdMvfcITkCx9dmxHU6qcEIsTTHuH020UcGayB1-IoimnjTdCsV1y4CMr_ECDjBrqMdnontkqKRIM1dtmgYFsJM6xm7ewi_ksG_qZHhaoBkxQ9wq9OVQRGiSZYowCp73d2BF3jYMhdmv2JiaUz5jRvv6lVU7Quq6ylVAlSPxeov9voYHO1mgZFCY1kQ";
        */

        // New public key and JWT token for PS384 Algorithm
        /*
        publicKeyString = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAllmXiQx/tlrnQ8jbQB5P" +
                "9FLr9V/fnUqt+J6AJAdz+QuQxbMQ+k2w0H0EKgQEKKW9PkGiJ/Zzk1mGkyBVspi3" +
                "H1h/sR33xTyTUVAeMZLS/S6UhCDfmHcXjB0O4rQgDTm9yYXlvricUZy70s2KWU0R" +
                "iUs+IDHin+TtRHzYlJmXtEL2pS+R7r/cIgB0QQft570+hV4o2CY14E5FC2jRTR7k" +
                "mLSDgGC/nVPbjzQaLwAC4dSUKz/er1dvWxdaE2Zg9WdZHfw80CeE58CEDVBMwDFQ" +
                "hkys9q1OSQwXyJGw83U5Pvc7p8I0UILYqbRFSFtcHnsdVpRwsPs/bpFugb4FbATE" +
                "TwIDAQAB";

        jwtToken = "eyJhbGciOiJQUzM4NCIsInR5cCI6IkpXVCIsImtpZCI6InN0c19TSEExd2l0aFJTQSJ9.eyJpc3MiOiJzdHMiLCJzdWIiOiJ1aWQ6VGVzdFVzZXIiLCJhdWQiOlsiYXVkMSIsImF1ZDIiLCJhdWQzIl0sIm5iZiI6MTcxMjEyOTk2NSwiZXhwIjoxNzEyMTMzNTY1LCJpYXQiOjE3MTIxMjk5NjUsImp0aSI6ImlkMTIzNDU2Iiwic2lkIjoic2Vzc2lvbklkIiwic2VjQ3R4Ijp7ImJvbGFTZWciOnsiY3VzdG9tZXJJZCI6ImNsaWVudCIsImNvbnRyYWN0cyI6WyJjb250cmFjdHMxIiwiY29udHJhY3RzMiIsImNvbnRyYWN0czMiLCJjb250cmFjdHM0Il19LCJhbGciOiJTSEEyNTYiLCJoYXNoIjoiUS8yYlJuY2lnV05qMUpKQWZ4L2tQZHpWZG9qekQ0eXcyTzZweE0wSFBiQT0ifX0.BK21MtFphpam6REoGf9DdNU5smJwcJl6Bb__hF5NSG2SXndnJKjltjqUhH7Z683H4mJH3w4QghT9dmCh2-LvPtyR_XzZGTgTqYSI0zXOFvTGMV9vkdmeSSjelf2manZb78hVsgDllXgYVowyfV_F0x_wDbdE2s_sAGO6HClLy9QdspB5-RVeeeTWwT9hUwgKmpGgqA4xduzIt7bl9MqqQcIUAhuSw-LbhKNFOBNRsGR6r01RFFvC_l--YjiCdO_SgTFCkglDkPXlkZqD4FMLCFi67pCy_CxgEWymrF4X56ek1d2nhEge1ZtdqLtCsP_qSvUGZYckL5gKOCu-WeqAzA";
        */

        // Hardcoded public key and JWT token for PS512 Algorithm
        /*
        publicKeyString = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAu1SU1LfVLPHCozMxH2Mo" +
                "4lgOEePzNm0tRgeLezV6ffAt0gunVTLw7onLRnrq0/IzW7yWR7QkrmBL7jTKEn5u" +
                "+qKhbwKfBstIs+bMY2Zkp18gnTxKLxoS2tFczGkPLPgizskuemMghRniWaoLcyeh" +
                "kd3qqGElvW/VDL5AaWTg0nLVkjRo9z+40RQzuVaE8AkAFmxZzow3x+VJYKdjykkJ" +
                "0iT9wCS0DRTXu269V264Vf/3jvredZiKRkgwlL9xNAwxXFg0x/XFw005UWVRIkdg" +
                "cKWTjpBP2dPwVZ4WWC+9aGVd+Gyn1o0CLelf4rEjGoXbAAEgAqeGUxrcIlbjXfbc" +
                "mwIDAQAB";

        jwtToken = "eyJhbGciOiJQUzUxMiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWUsImlhdCI6MTUxNjIzOTAyMn0.J5W09-rNx0pt5_HBiydR-vOluS6oD-RpYNa8PVWwMcBDQSXiw6-EPW8iSsalXPspGj3ouQjAnOP_4-zrlUUlvUIt2T79XyNeiKuooyIFvka3Y5NnGiOUBHWvWcWp4RcQFMBrZkHtJM23sB5D7Wxjx0-HFeNk-Y3UJgeJVhg5NaWXypLkC4y0ADrUBfGAxhvGdRdULZivfvzuVtv6AzW6NRuEE6DM9xpoWX_4here-yvLS2YPiBTZ8xbB3axdM99LhES-n52lVkiX5AWg2JJkEROZzLMpaacA_xlbUz_zbIaOaoqk8gB5oO7kI6sZej3QAdGigQy-hXiRnW_L98d4GQ";
        */

        // New public key and JWT token for PS512 Algorithm
        /*
        publicKeyString = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAllmXiQx/tlrnQ8jbQB5P" +
                "9FLr9V/fnUqt+J6AJAdz+QuQxbMQ+k2w0H0EKgQEKKW9PkGiJ/Zzk1mGkyBVspi3" +
                "H1h/sR33xTyTUVAeMZLS/S6UhCDfmHcXjB0O4rQgDTm9yYXlvricUZy70s2KWU0R" +
                "iUs+IDHin+TtRHzYlJmXtEL2pS+R7r/cIgB0QQft570+hV4o2CY14E5FC2jRTR7k" +
                "mLSDgGC/nVPbjzQaLwAC4dSUKz/er1dvWxdaE2Zg9WdZHfw80CeE58CEDVBMwDFQ" +
                "hkys9q1OSQwXyJGw83U5Pvc7p8I0UILYqbRFSFtcHnsdVpRwsPs/bpFugb4FbATE" +
                "TwIDAQAB";

        jwtToken = "eyJhbGciOiJQUzUxMiIsInR5cCI6IkpXVCIsImtpZCI6InN0c19TSEExd2l0aFJTQSJ9.eyJpc3MiOiJzdHMiLCJzdWIiOiJ1aWQ6VGVzdFVzZXIiLCJhdWQiOlsiYXVkMSIsImF1ZDIiLCJhdWQzIl0sIm5iZiI6MTcxMjEzMDA3MSwiZXhwIjoxNzEyMTMzNjcxLCJpYXQiOjE3MTIxMzAwNzEsImp0aSI6ImlkMTIzNDU2Iiwic2lkIjoic2Vzc2lvbklkIiwic2VjQ3R4Ijp7ImJvbGFTZWciOnsiY3VzdG9tZXJJZCI6ImNsaWVudCIsImNvbnRyYWN0cyI6WyJjb250cmFjdHMxIiwiY29udHJhY3RzMiIsImNvbnRyYWN0czMiLCJjb250cmFjdHM0Il19LCJhbGciOiJTSEEyNTYiLCJoYXNoIjoiUS8yYlJuY2lnV05qMUpKQWZ4L2tQZHpWZG9qekQ0eXcyTzZweE0wSFBiQT0ifX0.iFSPLcZZ-ANRGrmNyoaP680LVA1Peb2DvkOEsOJxw_M4eU5nZbG043nNqkudJ31FS2AZCZZPC2z0tBDRuW6WOQXWwxEDHGyjUOsCn8w3foO5-AIEtzfI7N00rp6AbD73yijH66vpWontIh7Ly6XYGLi0x2rXw_ooSlLVxhLHX_ehuQsmuktpyLBxhAFWXjODlTqe45WBz1M_1Xwx6lOn-n_C_evJ6y-3rs16L95KgpgBywO1Zv34xSpK4IAm08ifsPGFdd_UQ1LC4CGOqebFFZFXOQfHKSnfmbBbzQ6EXUG68wDF7j3RaE6s3noVSyBH70k6qX2C4a0MANyfn3zomA";
        */
        // Verify the JWT token using the public key
        /*
        int verificationResult = 0;

        if (publicKeyString != null && jwtToken != null){
            verificationResult = verifyJWTToken(publicKeyString, jwtToken, secretKey);
        } else if (jwtToken != null && secretKey != null) {
            verificationResult = verifyJWTToken(null, jwtToken, secretKey);
        }*/

        int verificationResult = verifyJWTToken(publicKeyString, jwtToken, secretKey);
        //System.exit(verificationResult);

        if (verificationResult == 200) {
            System.out.println("<<<<<<<<<<<<JWT Token is valid.>>>>>>>>>>>Thank you.>>>>>>>>>");
            //System.exit(200);
            System.exit(verificationResult);
        } else {
            System.out.println("<<<<<<<<<JWT Token is invalid.>>>>>>>Sorry, Try again.>>>>>>>>>");
            //System.exit(400);
            System.exit(verificationResult);
        }
        //System.exit(200);
    }

    public static int verifyJWTToken(String publicKeyString, String jwtToken, String secretKey) {
        try {
            String[] jwtParts = jwtToken.split("\\.");

            // Decode header and payload
            String header = new String(android.util.Base64.decode(jwtParts[0], android.util.Base64.DEFAULT));
            System.out.println("header: "+header);

            Map<String,String> headerDictionary = jsonStringToDictionary(header);
            System.out.println(headerDictionary);

            System.out.println("The Algorithm is: "+headerDictionary.get("alg"));

            Optional<Algorithm> algorithm = Optional.empty();

            //byte[] publicKeyByteArr = Base64.getDecoder().decode(publicKeyString);

            if(Objects.equals(headerDictionary.get("alg"), "PS256")){
                System.out.println("Start working: "+headerDictionary.get("alg"));

                // Decode public key
                byte[] keyBytes = android.util.Base64.decode(publicKeyString, android.util.Base64.DEFAULT);
                X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                PublicKey publicKey = keyFactory.generatePublic(keySpec);

                // Verify signature
                Signature signature = Signature.getInstance("SHA256withRSA");
                signature.initVerify(publicKey);
                signature.update((jwtParts[0] + "." + jwtParts[1]).getBytes());

                System.out.println("The token has been successfully verified.");

                //return true;

            } else if(Objects.equals(headerDictionary.get("alg"), "PS384")) {
                System.out.println("Start working: "+headerDictionary.get("alg"));
                // Decode public key
                byte[] keyBytes = android.util.Base64.decode(publicKeyString, android.util.Base64.DEFAULT);
                X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                PublicKey publicKey = keyFactory.generatePublic(keySpec);

                // Verify signature
                Signature signature = Signature.getInstance("SHA384withRSA");
                signature.initVerify(publicKey);
                signature.update((jwtParts[0] + "." + jwtParts[1]).getBytes());

                System.out.println("The token has been successfully verified.");

                //return true;
            } else if(Objects.equals(headerDictionary.get("alg"), "PS512")) {
                System.out.println("Start working: "+headerDictionary.get("alg"));

                // Decode public key
                byte[] keyBytes = android.util.Base64.decode(publicKeyString, android.util.Base64.DEFAULT);
                X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                PublicKey publicKey = keyFactory.generatePublic(keySpec);

                // Verify signature
                Signature signature = Signature.getInstance("SHA512withRSA");
                signature.initVerify(publicKey);
                signature.update((jwtParts[0] + "." + jwtParts[1]).getBytes());

                System.out.println(signature);
                //signature.verify(android.util.Base64.decode(jwtParts[2], android.util.Base64.DEFAULT));

                System.out.println("The token has been successfully verified.");

                //return true;
            } else if(Objects.equals(headerDictionary.get("alg"), "RS256")){
                System.out.println("Start working: "+headerDictionary.get("alg"));

                byte[] publicKeyByteArr = Base64.getDecoder().decode(publicKeyString);

                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                RSAPublicKey rsaPublicKey = (RSAPublicKey) keyFactory.generatePublic(new X509EncodedKeySpec(publicKeyByteArr));

                //If the token has an invalid signature, JWTVerificationException will raise.
                algorithm = Optional.of(Algorithm.RSA256(rsaPublicKey, null));

                JWTVerifier verifier = JWT.require(algorithm.get())
                        //.withIssuer("auth0")
                        .build(); //Reusable verifier instance
                DecodedJWT jwt = verifier.verify(jwtToken);

                System.out.println("The token has been successfully verified.");

                //return true;

            } else if(Objects.equals(headerDictionary.get("alg"), "RS384")) {
                System.out.println("Start working: "+headerDictionary.get("alg"));

                byte[] publicKeyByteArr = Base64.getDecoder().decode(publicKeyString);

                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                RSAPublicKey rsaPublicKey = (RSAPublicKey) keyFactory.generatePublic(new X509EncodedKeySpec(publicKeyByteArr));

                //If the token has an invalid signature, JWTVerificationException will raise.
                algorithm = Optional.of(Algorithm.RSA384(rsaPublicKey, null));

                JWTVerifier verifier = JWT.require(algorithm.get())
                        //.withIssuer("auth0")
                        .build(); //Reusable verifier instance
                DecodedJWT jwt = verifier.verify(jwtToken);

                System.out.println("The token has been successfully verified.");

                //return true;
            } else if(Objects.equals(headerDictionary.get("alg"), "RS512")) {
                System.out.println("Start working: "+headerDictionary.get("alg"));

                byte[] publicKeyByteArr = Base64.getDecoder().decode(publicKeyString);

                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                RSAPublicKey rsaPublicKey = (RSAPublicKey) keyFactory.generatePublic(new X509EncodedKeySpec(publicKeyByteArr));

                //If the token has an invalid signature, JWTVerificationException will raise.
                algorithm = Optional.of(Algorithm.RSA512(rsaPublicKey, null));

                JWTVerifier verifier = JWT.require(algorithm.get())
                        //.withIssuer("auth0")
                        .build(); //Reusable verifier instance
                DecodedJWT jwt = verifier.verify(jwtToken);

                System.out.println("The token has been successfully verified.");

                //return true;
            } else if(Objects.equals(headerDictionary.get("alg"), "HS256")){
                System.out.println("Start working: "+headerDictionary.get("alg"));

                //If the token has an invalid signature, JWTVerificationException will raise.
                algorithm = Optional.of(Algorithm.HMAC256(secretKey));

                JWTVerifier verifier = JWT.require(algorithm.get()).build();
                verifier.verify(jwtToken);

                System.out.println("The token has been successfully verified.");

                //return true;
            } else if(Objects.equals(headerDictionary.get("alg"), "HS384")){
                System.out.println("Start working: "+headerDictionary.get("alg"));

                //If the token has an invalid signature, JWTVerificationException will raise.
                algorithm = Optional.of(Algorithm.HMAC384(secretKey));

                JWTVerifier verifier = JWT.require(algorithm.get()).build();
                verifier.verify(jwtToken);

                System.out.println("The token has been successfully verified.");

                //return true;
            } else if(Objects.equals(headerDictionary.get("alg"), "HS512")){
                System.out.println("Start working: "+headerDictionary.get("alg"));

                //If the token has an invalid signature, JWTVerificationException will raise.
                algorithm = Optional.of(Algorithm.HMAC512(secretKey));

                JWTVerifier verifier = JWT.require(algorithm.get()).build();
                verifier.verify(jwtToken);

                System.out.println("The token has been successfully verified.");

                //return true;
            } else if(Objects.equals(headerDictionary.get("alg"), "ES256")){
                System.out.println("Start working: "+headerDictionary.get("alg"));

                byte[] publicKeyByteArr = Base64.getDecoder().decode(publicKeyString);

                X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyByteArr);

                KeyFactory keyFactory = KeyFactory.getInstance("EC");
                ECPublicKey publicKey = (ECPublicKey) keyFactory.generatePublic(keySpec);

                algorithm = Optional.of(Algorithm.ECDSA256(publicKey, null));

                JWTVerifier verifier = JWT.require(algorithm.get()).build();
                verifier.verify(jwtToken);

                System.out.println("The token has been successfully verified.");

                //return true;
            } else if(Objects.equals(headerDictionary.get("alg"), "ES384")){
                System.out.println("Start working: "+headerDictionary.get("alg"));

                byte[] publicKeyByteArr = Base64.getDecoder().decode(publicKeyString);

                X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyByteArr);

                KeyFactory keyFactory = KeyFactory.getInstance("EC");
                ECPublicKey publicKey = (ECPublicKey) keyFactory.generatePublic(keySpec);

                algorithm = Optional.of(Algorithm.ECDSA384(publicKey, null));

                JWTVerifier verifier = JWT.require(algorithm.get()).build();
                verifier.verify(jwtToken);

                System.out.println("The token has been successfully verified.");

                //return true;
            } else if(Objects.equals(headerDictionary.get("alg"), "ES512")){
                System.out.println("Start working: "+headerDictionary.get("alg"));

                byte[] publicKeyByteArr = Base64.getDecoder().decode(publicKeyString);

                X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyByteArr);

                KeyFactory keyFactory = KeyFactory.getInstance("EC");
                ECPublicKey publicKey = (ECPublicKey) keyFactory.generatePublic(keySpec);

                algorithm = Optional.of(Algorithm.ECDSA512(publicKey, null));

                JWTVerifier verifier = JWT.require(algorithm.get()).build();
                verifier.verify(jwtToken);

                System.out.println("The token has been successfully verified.");
                //return true;
            }

            return 200;

        }catch(NoSuchAlgorithmException | JWTVerificationException | InvalidKeySpecException | JSONException |
               InvalidKeyException | SignatureException e) {
            System.out.println("JWT verification is failed");
            //throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
            return 400;
        }
    }

    public static Map<String, String> jsonStringToDictionary(String jsonString) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonString);

        Map<String, String> dict = new HashMap<>();

        Iterator<String> keys = jsonObject.keys();

        while (keys.hasNext()){
            String key = keys.next();
            String value = jsonObject.getString(key);
            dict.put(key, value);
        }
        return dict;
    }

}
