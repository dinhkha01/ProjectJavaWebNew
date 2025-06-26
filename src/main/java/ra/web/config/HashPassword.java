package ra.web.config;

import org.mindrot.jbcrypt.BCrypt;

public class HashPassword {
    public static void main(String[] args) {
        String plainPass = "123456$";
        System.out.println(BCrypt.hashpw(plainPass,BCrypt.gensalt(12)));
        System.out.println(BCrypt.hashpw(plainPass,BCrypt.gensalt(12)));
        System.out.println(BCrypt.hashpw(plainPass,BCrypt.gensalt(12)));

        System.out.println("check : "+ BCrypt.checkpw(plainPass,"$2a$12$ySp4q7aZd4bakZaGGMk8VugDUR/fh2L6wno8cLzow3M6UCJh5nv3G"));
    }
}
