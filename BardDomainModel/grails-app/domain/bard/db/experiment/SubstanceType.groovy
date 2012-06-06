package bard.db.experiment

public enum SubstanceType {
	
	SMALL_MOLECULE("small molecule"),
	PROTEIN("protein"),
	PEPTIDE("peptide"),
	ANTIBODY("antibody"),
	CELL("cell"),
	OLIGONUCLEOTIDE("oligonucleotide")

    String value

	SubstanceType(String value){
		this.value = value
	}
	
	String toString() {
		value
	}
	
	String getKey() {
		name()
	}
	
	static list(){
		[SMALL_MOLECULE, PROTEIN, PEPTIDE, ANTIBODY, CELL, OLIGONUCLEOTIDE]
	}
}
