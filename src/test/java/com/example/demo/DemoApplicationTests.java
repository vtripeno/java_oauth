package com.example.demo;

import com.sun.jna.platform.win32.Advapi32Util;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.servlet.http.HttpServletRequest;

//@RunWith(SpringRunner.class)
//@SpringBootTest
public class DemoApplicationTests {

	@Autowired
	HttpServletRequest servletRequest;

	@Test
	public void contextLoads() {
		System.out.println(servletRequest);
	}

	@Test
	public void advap() {
		for(Advapi32Util.Account account: Advapi32Util.getCurrentUserGroups()) {
			System.out.println("NAME " + account.name);
			System.out.println("FQN " + account.fqn);
			System.out.println("SID STRING " + account.sidString);
			System.out.println("DOMAIN " + account.domain);
		}

	}

}
