package bard.util;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.Transformer;
import org.apache.commons.dbutils.QueryRunner;
import org.dom4j.Document;
import org.dom4j.Node;

import edu.scripps.fl.pubchem.web.entrez.EUtilsWebSession;
import edu.scripps.fl.pubchem.web.entrez.transformer.*;

public class XRefLabelFetch {

	// transformers convert an xml node to a pretty print string. 
	Map<String, Transformer<Node, String>> dbs = new LinkedHashMap<String,Transformer<Node, String>>() {{
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

	public void process() throws Exception {
		for (String db : dbs.keySet()) {
			// could change this sql statement to get ids from bard measure_context_item directly.
			List<Long> ids = new QueryRunner().query(Util.getConnection2(),
					"select xref_identifier from xref where xref_database = ? and REGEXP_LIKE (xref_identifier, '^\\d+$')", new ColumnConverterHandler(1,
							Long.class), db);
			Document doc = EUtilsWebSession.getInstance().getSummariesAsDocument(ids, db);
			List<Node> docSumNodes = doc.selectNodes("/eSummaryResult/DocumentSummarySet/DocumentSummary");
			for (Node node : docSumNodes) {
				String uid = node.valueOf("@uid");
				String label = dbs.get(db).transform(node);
				// field in bard is 500 chars. Pretty much only PubMed articles are longer than this and the transformer has its own solution.
				if( label.length() > 500 )
					label = label.substring(0, 496) + "...";
				System.out.println(String.format("%s\t%s\t%s", uid, db, label));
			}
		}
	}

	public static void main(String[] args) throws Exception {
		new XRefLabelFetch().process();
	}
}