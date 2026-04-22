package hocjava;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HocjavaApplication {
	public static void main(String[] args) {
		SpringApplication.run(HocjavaApplication.class, args);
//		var encode = new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
//		var listPass = java.util.List.of("admin", "hoantv", "binhnv", "anlv", "quantt", "daipt");
//		for (var pass:listPass) {
//			System.out.println(encode.encode(pass));
//		}
	}

}
