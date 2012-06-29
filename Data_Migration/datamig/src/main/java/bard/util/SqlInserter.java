package bard.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public abstract class SqlInserter {
	abstract Collection<Long> getAids() throws Exception;
	abstract void processAid(Long aid) throws Exception;	

	public boolean passFilter(Long aid) {
		return true;
	}
	
	public void process(Collection<Long> aids) throws Exception {
		Set<Long> failures = new HashSet();
		for (Long aid : aids)
			try {
				if( ! passFilter(aid) )
					continue;
				processAid(aid);
				Util.getConnection().commit();
			} catch (Exception ex) {
				failures.add(aid);
				ex.printStackTrace();
				Util.getConnection().rollback();
				if (ex instanceof SQLException)
					throw ex;
			}
		System.err.println(failures.size() + " Failures: " + failures);
	}
}