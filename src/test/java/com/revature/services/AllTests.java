package com.revature.services;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ AccountServiceTest.class, BankTest.class, UserServiceTest.class })
public class AllTests {

}
