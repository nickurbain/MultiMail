package multimail;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class MultiMailTest {
	
	String p1 = "5635131325";
	String p2 = "5635902848";
	String p3 = "15635837722";
	TelToAddress T;

	@Before
	public void setUp() throws Exception {
		 T = new TelToAddress(p1);
	}

	@Test
	public void test() {
		//System.out.println(T.transform());
	
		SendMail sm = new SendMail(T.transform(1), p1);
	}

}
