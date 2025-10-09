import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

public class GenerateHash {
    public static void main(String[] args) throws Exception {
        char[] password = "1234".toCharArray();
        int iterations = 65536;
        int keyLength = 256;
        byte[] salt = new byte[16];
        SecureRandom sr = SecureRandom.getInstanceStrong();
        sr.nextBytes(salt);
        PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, keyLength);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] hash = skf.generateSecret(spec).getEncoded();
        String out = String.format("%d:%s:%s", iterations, Base64.getEncoder().encodeToString(salt), Base64.getEncoder().encodeToString(hash));
        System.out.println(out);
    }
}
