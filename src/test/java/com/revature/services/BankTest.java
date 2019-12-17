package com.revature.services;

import static org.junit.Assert.*;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.revature.Bank;
import com.revature.models.User;

public class BankTest {
	private static Bank b;
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		b = new Bank();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		b.us.insert(new User("ServiceTestUser", DigestUtils.sha256Hex("randgenpw"), "Client", 0));
	}

	@After
	public void tearDown() throws Exception {
		b.us.delete(new User("ServiceTestUser", "randgenpw", "Client", 0));
	}

	@Test
	public void testVerifyLogIn() {
		assertTrue(b.verifyLogIn("ServiceTestUser" , "randgenpw"));
	}
	
	@Test
	public void testVerifyLogInDifferentPassWord() {
		assertFalse(b.verifyLogIn("ServiceTestUser", "shouldbefalse"));
	}
	
	@Test public void testVerifyLogInDifferentCasePW() {
		assertFalse(b.verifyLogIn("ServiceTestUser", "RanDgenPw"));
	}

	@Test
	public void testCheckUser() {
		assertTrue(b.checkUser("ServiceTestUser"));
	}
	
	@Test
	public void testCheckNonExistentUser() {
		assertFalse(b.checkUser("johndoe"));
	}


	@Test
	public void testMoneyFormatter() {
		assertEquals(new Double(19.02), new Double(b.moneyFormatter(19.0231)));
	}
	
	@Test
	public void testMoneyFormatterRound() {
		assertEquals(new Double(66), new Double(b.moneyFormatter(65.99999)));
	}
	
	@Test
	public void testMoneyFormatterNegative() {
		assertEquals(new Double(-55), new Double(b.moneyFormatter(-55.001001)));
	}

}
