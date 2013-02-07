package bard.util.app;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.MultiKeyMap;
import org.apache.commons.collections.Transformer;
import org.apache.commons.dbutils.QueryRunner;
import org.dom4j.Document;
import org.dom4j.Node;

import bard.util.Util;
import bard.util.dbutil.ColumnConverterHandler;

import edu.scripps.fl.pubchem.web.entrez.EUtilsWebSession;
import edu.scripps.fl.pubchem.web.entrez.transformer.BioSystemsTransformer;
import edu.scripps.fl.pubchem.web.entrez.transformer.GeneTransformer;
import edu.scripps.fl.pubchem.web.entrez.transformer.MeshTransformer;
import edu.scripps.fl.pubchem.web.entrez.transformer.OmimTransformer;
import edu.scripps.fl.pubchem.web.entrez.transformer.ProteinOrNucleotideTransformer;
import edu.scripps.fl.pubchem.web.entrez.transformer.PubMedTransformer;
import edu.scripps.fl.pubchem.web.entrez.transformer.TaxonomyTransformer;

public class XRefLabelFetch {

	// transformers convert an xml node to a pretty print string. 
	Map<String, Transformer> dbs = new LinkedHashMap<String,Transformer>() {{
			put("biosystems", new BioSystemsTransformer());
			put("pubmed", new PubMedTransformer(500));
			put("gene", new GeneTransformer());
			put("mesh", new MeshTransformer());
			put("nucleotide", new ProteinOrNucleotideTransformer());
			put("omim", new OmimTransformer());
//			put("pcassay", new PCAssayTransformer());
			put("protein", new ProteinOrNucleotideTransformer());
			put("taxonomy", new TaxonomyTransformer());
	}};

	public MultiKeyMap process() throws Exception {
		MultiKeyMap map = new MultiKeyMap();
		for (String db : dbs.keySet()) {
			// could change this sql statement to get ids from bard measure_context_item directly.
			List<Long> ids = (List<Long>) new QueryRunner().query(Util.getConnection(),
					"select xref_identifier from xref where xref_database = ? and REGEXP_LIKE (xref_identifier, '^\\d+$')", new ColumnConverterHandler(1,
							Long.class), db);
			Document doc = EUtilsWebSession.getInstance().getSummariesAsDocument(ids, db);
			List<Node> docSumNodes = doc.selectNodes("/eSummaryResult/DocumentSummarySet/DocumentSummary");
			for (Node node : docSumNodes) {
				String uid = node.valueOf("@uid");
				String label = dbs.get(db).transform(node).toString();
				// field in bard is 500 chars. Pretty much only PubMed articles are longer than this and the transformer has its own solution.
				if( label.length() > 500 )
					label = label.substring(0, 496) + "...";
				map.put(uid, db, label);
			}
		}
		return map;
	}
}