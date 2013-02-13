package bard.util;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class SqlInserter {
	
	private static final Logger log = LoggerFactory.getLogger(SqlInserter.class);
	
	abstract Collection<Long> getAids() throws Exception;
	abstract void processAid(Long aid) throws Exception;
	
	Set<Long> failures = new TreeSet();
	Set<Long> successes = new TreeSet();

	public boolean passFilter(Long aid) {
		return true;
	}
	
	public void process(Collection<Long> aids) throws Exception {
		failures = new HashSet();
		for (Long aid : aids)
			try {
				if( ! passFilter(aid) ) {
					log.debug(String.format("AID %s did not pass the filter", aid));
					continue;
				}
				processAid(aid);
				log.debug("Committing AID " + aid);
				Util.getConnection().commit();
				successes.add(aid);
			} catch (Exception ex) {
				failures.add(aid);
				ex.printStackTrace();
				Util.getConnection().rollback();
				if (ex instanceof SQLException)
					throw ex;
			}
			try {
				Util.closeConnection();
			}
			catch(SQLException ex) {
				ex.printStackTrace();
			}
	}
}