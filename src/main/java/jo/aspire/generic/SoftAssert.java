package jo.aspire.generic;

import java.util.LinkedHashMap;
import java.util.Map;

import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;



/**
 * When an assertion fails, don't throw an exception but record the failure.
 * Calling {@code assertAll()} will cause an exception to be thrown if at
 * least one assertion failed.
 **/

public class SoftAssert{
  // LinkedHashMap to preserve the order
	private final LinkedHashMap<AssertionError, Matcher<?>> m_errors = new LinkedHashMap<AssertionError, Matcher<?>>();
	  

	private static final ThreadLocal<SoftAssert> softAssert = new ThreadLocal<SoftAssert>() {
		@Override
		public SoftAssert initialValue() {
			try {
				return new SoftAssert();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	};
	
	
	/**
	 *Remove all the failures
	 **/
	public static void clear() {
		softAssert.remove();
	}

	/**
	 *collates all the failures and decides 
	 *whether to fail the test or not at the end.
	 **/
	public static void assertAll() {
		if (!softAssert.get().m_errors.isEmpty()) {
			StringBuilder sb = new StringBuilder(
					"The following asserts failed:");
			boolean first = true;
			for (Map.Entry<AssertionError, Matcher<?>> ae : softAssert.get().m_errors
					.entrySet()) {
				if (first) {
					first = false;
				} else {
					sb.append(",");
				}
				sb.append("\n\t");
				sb.append(ae.getKey().getMessage());
			}
			throw new AssertionError(sb.toString());
		}
	}

	
	/**
	 *Adds items for soft assertion
	 * @param actual  actual result to assert
	 * @param matcher expected result
	 **/
	public static <T> void softAssertThat(T actual, Matcher<? super T> matcher) {

		try {
			MatcherAssert.assertThat("", actual, matcher);
		} catch (AssertionError ex) {
			softAssert.get().m_errors.put(ex, matcher);
		} finally {
		}
	}

	
	/**
	 *Adds items for soft assertion
	 * @param reason  custom message text
	 * @param actual  actual result to assert
	 * @param matcher expected result
	 **/
	public static <T> void softAssertThat(String reason, T actual,
			Matcher<? super T> matcher) {

		try {
			MatcherAssert.assertThat(reason, actual, matcher);
		} catch (AssertionError ex) {
			softAssert.get().m_errors.put(ex, matcher);
		} finally {
		}
	}
	
	
	
		
}