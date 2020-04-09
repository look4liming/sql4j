package test.others;

import lee.bright.sql4j.conf.Configuration;
import lee.bright.sql4j.conf.NameValuePairs;

import test.Person;

public class NameValuePairsTest {

	public static void main(String[] args) {
		Person p = new Person();
		NameValuePairs pairs = new Configuration().newNameValuePairs(p);
		
		long time = System.currentTimeMillis();
		
		pairs.setValue("gender", "1");
		System.out.println(pairs.containsName("name"));
		System.out.println(pairs.containsName("gender"));
		System.out.println(pairs.containsName("birthdate"));
		System.out.println(p.getUsername());
		
		System.out.println(System.currentTimeMillis() - time);
	}

}
