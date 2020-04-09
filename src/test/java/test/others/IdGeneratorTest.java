package test.others;

import lee.bright.sql4j.util.IdGenerator;

public class IdGeneratorTest {

	public static void main(String[] args) {
		String id = IdGenerator.generateUUID();
		System.out.println(id);
	}

}
