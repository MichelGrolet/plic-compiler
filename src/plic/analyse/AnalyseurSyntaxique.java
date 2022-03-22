package plic.analyse;

import plic.exception.*;
import plic.repint.*;

import java.io.File;

public class AnalyseurSyntaxique {
	private final AnalyseurLexical analex;
	private String uniteCourante;
	private Bloc bloc;

	public AnalyseurSyntaxique(File file) {
		this.analex = new AnalyseurLexical(file);
	}

	private void analyseProg() throws ErreurSyntaxique, DoubleDeclaration {
		if (!this.uniteCourante.equals("programme"))
			throw new ErreurSyntaxique("programme attendu");
		this.uniteCourante = this.analex.next();
		if (!this.estIdf()) throw new ErreurSyntaxique("idf attendu");
		this.uniteCourante = this.analex.next();
		this.analyseBloc();
	}

	private void analyseTerminal(String terminal) throws ErreurSyntaxique {
		if (!this.uniteCourante.equals(terminal))
			throw new ErreurSyntaxique(terminal + " attendu");
		this.uniteCourante = this.analex.next();
	}

	public boolean estIdf() {
		return this.uniteCourante.matches("[a-zA-Z]+");
	}

	public Bloc analyse() throws ErreurSyntaxique, DoubleDeclaration {
		this.uniteCourante = this.analex.next();
		this.analyseProg();
		if (!this.uniteCourante.equals("EOF"))
			throw new ErreurSyntaxique("Fin de programme attendue");
		return this.bloc;
	}
	
	private void analyseBloc() throws ErreurSyntaxique, DoubleDeclaration {
		this.analyseTerminal("{");
		// Itérer sur analyseDeclaration tant qu’il y a des déclarations
		while (this.uniteCourante.equals("entier"))
			this.analyseDeclaration();

		// bloc : liste des instructions
		this.bloc = new Bloc();
		this.analyseInstruction();
		// Itérer sur analyseInstruction tant qu’il y a des instructions
		while (this.estIdf() || this.uniteCourante.equals("ecrire"))
			this.analyseInstruction();
		this.analyseTerminal("}");
	}

	private void analyseDeclaration() throws ErreurSyntaxique, DoubleDeclaration {
		this.uniteCourante = this.analex.next();
		if (!this.estIdf()) throw new ErreurSyntaxique("idf attendu dans une déclaration");
		else {
			TDS tds = TDS.getInstance();
			tds.ajouter(new Entree(this.uniteCourante), new Symbole("entier"));
			this.uniteCourante = this.analex.next();
			if (!this.uniteCourante.equals(";")) throw new ErreurSyntaxique("; attendu");
			this.uniteCourante = this.analex.next();
		}
	}

	private void analyseInstruction() throws ErreurSyntaxique, DoubleDeclaration {
		if (this.uniteCourante.equals("ecrire"))
			this.analyseEcrire();
		else this.analyseAffectation();
	}

	private void analyseEcrire() throws ErreurSyntaxique {
		this.uniteCourante = this.analex.next();
		if (!this.uniteCourante.matches("[a-zA-Z]+"))
			throw new ErreurSyntaxique("idf attendu pour écrire");
		this.bloc.ajouterInstruction(new Ecrire(new Idf(this.uniteCourante)));
		this.uniteCourante = this.analex.next();
		if (!this.uniteCourante.equals(";"))
			throw new ErreurSyntaxique("; attendu");
		this.uniteCourante = this.analex.next();
	}

	private void analyseAffectation() throws ErreurSyntaxique {
		String nom = this.uniteCourante;
		this.uniteCourante = this.analex.next();
		if (!this.uniteCourante.equals(":="))
			throw new ErreurSyntaxique("idf attendu dans une affectation");
		this.uniteCourante = this.analex.next();
		System.out.println("xxxxxxxxxxxxx"+this.uniteCourante);
		Instruction i;
		if (this.estEntier()) {
			// Affectation d’un entier
			i = new Affectation(new Idf(nom), new Idf(this.uniteCourante));
		} else if (this.estIdf()) {
			// Affectation d’une variable
			i = new A
		}
		this.bloc.ajouterInstruction(i);
		this.uniteCourante = this.analex.next();
		this.uniteCourante = this.analex.next();
	}

	private boolean estEntier() {
		return this.uniteCourante.matches("[0-9]+");
	}
}
