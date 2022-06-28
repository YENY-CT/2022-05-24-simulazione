package it.polito.tdp.itunes.model;

//c.
//classe creata per glki archi 
public class Adiacenza {
	private Track t1;
	private Track t2;
	private int peso;
	
	public Adiacenza(Track t1, Track t2, int peso) {
		super();
		this.t1 = t1;
		this.t2 = t2;
		this.peso = peso;
	}

	public Track getT1() {
		return t1;
	}

	public void setT1(Track t1) {
		this.t1 = t1;
	}

	public Track getT2() {
		return t2;
	}

	public void setT2(Track t2) {
		this.t2 = t2;
	}

	public int getPeso() {
		return peso;
	}

	public void setPeso(int peso) {
		this.peso = peso;
	}

	//per stampare quello che vogliamo ossia il titolo delle due canzoni e il lr peso
	@Override
	public String toString() {
		return t1.getName() + " - " + t2.getName() + " = " + this.peso;
	}
	
	
	
	
}
