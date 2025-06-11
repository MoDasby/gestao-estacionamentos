package com.modasby.gestaoestacionamentos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class GestaoEstacionamentosApplication {

    public static void main(String[] args) {
        SpringApplication.run(GestaoEstacionamentosApplication.class, args);
    }

}
