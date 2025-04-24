package top.yihoxu.likesystem;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("top.yihoxu.likesystem.mapper")
public class LikeSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(LikeSystemApplication.class, args);
    }

}
