package bard.validation.ext.util;

import javax.sql.DataSource;

import bard.util.BoneCPUtil;
import bard.validation.ext.ExternalOntologyException;
import bard.validation.ext.ExternalOntologyGO;

public class GOUtil {

private static volatile DataSource DEFAULT_GO_DATASOURCE;
	
	public static DataSource getEBIDataSource() throws ExternalOntologyException {
		if(DEFAULT_GO_DATASOURCE == null)
			synchronized(ExternalOntologyGO.class) {
				if( DEFAULT_GO_DATASOURCE == null)
					try {
						DEFAULT_GO_DATASOURCE = BoneCPUtil.configureDataSource("com.mysql.jdbc.Driver", "jdbc:mysql://mysql.ebi.ac.uk:4085/go_latest", "go_select", "amigo");	
					}
					catch(Exception ex) {
						throw new ExternalOntologyException(String.format("Could not create default (EBI) GO datasource"), ex);
					}
					 
			}
		return DEFAULT_GO_DATASOURCE;
	}

}
