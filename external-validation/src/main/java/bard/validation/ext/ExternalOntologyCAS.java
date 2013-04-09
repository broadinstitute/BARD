package bard.validation.ext;

import java.util.ArrayList;
import java.util.List;

/**
 * Checks validity (but not existence) of Chemical Abstract System (CAS) numbers
 * 
 * @author southern
 * 
 */
@Deprecated
public class ExternalOntologyCAS extends ExternalOntologyAPI {

	/**
	 * Reference:
	 * http://depth-first.com/articles/2011/10/20/how-to-validate-cas-
	 * registry-numbers-in-javascript/
	 */
	public static boolean checkCas(String cas) {
		// 1-7 digits, dash, 2 digits, dash, check digit
		if (!cas.matches("^\\d{1,7}-\\d{2}-\\d$"))
			return false;
		cas = cas.replaceAll("-", "");
		int sum = 0;
		for (int i = cas.length() - 2; i >= 0; i--) {
			String substr = cas.substring(i, i + 1);
			sum += Integer.parseInt(substr) * (cas.length() - i - 1);
		}
		String substr = cas.substring(cas.length() - 1);
		boolean valid = Integer.parseInt(substr) == (sum % 10);
		return valid;
	}

	@Override
	public ExternalItem findById(String id) throws ExternalOntologyException {
		return getCas(id);
	}

	@Override
	public ExternalItem findByName(String name) throws ExternalOntologyException {
		return getCas(name);
	}

	@Override
	public List<ExternalItem> findMatching(String name, int limit) throws ExternalOntologyException {
		List<ExternalItem> list = new ArrayList<ExternalItem>(1);
		list.add(getCas(name));
		return list;
	}

	protected ExternalItem getCas(String name) {
		String term = cleanName(name);
		if (checkCas(term))
			return new ExternalItem(term, term);
		return null;
	}

	@Override
	public String getExternalURL(String id) {
		return "http://www.cas.org";
	}
}