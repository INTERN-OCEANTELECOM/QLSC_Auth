package com.ocena.qlsc;

import com.ocena.qlsc.model.User;
import com.ocena.qlsc.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication
public class QlscApplication {

	public static void main(String[] args) {
		SpringApplication.run(QlscApplication.class, args);

	}

}
