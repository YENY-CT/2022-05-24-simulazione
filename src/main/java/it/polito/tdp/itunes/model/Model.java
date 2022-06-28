package it.polito.tdp.itunes.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.itunes.db.ItunesDAO;

public class Model {
	
	private ItunesDAO dao;
	
	//b.
	//per creare grafo
	private Graph<Track,DefaultWeightedEdge> grafo; //i vertici sono di tipo track 
	private Map<Integer,Track> idMap;
	
	
	//la parte2 essendo un problema di ottimizzazione mi definisco una struttura dati per tenere traccia delle soluzione migliore:
	private List<Track> listaMigliore; 

	
	public Model() {
		dao = new ItunesDAO();
		idMap = new HashMap<>();
		
		//voglio recuperarmi tt le canzoni del database e riempirmi la mappa
		//modifico nel dao il metodod getAllTrack in modo che nn mi ritorni una lista ma riceve la mappa e me lo rimpia 
		this.dao.getAllTracks(idMap);
	}
	
	//a.
	//metodo per recuperare tt i generi e poi passarlo al controllore per rempire la tendina :
	public List<Genre> getGeneri(){ 
		return dao.getAllGenres();
	}
	
	//b.
	public void creaGrafo(Genre genere) {//<-- metodo scatenato al click del bottone
		//creo il grafo:
		this.grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		
		//aggiungo i VERTICI:
		//VERTICI sono tutte le CANZONI (Track) di genere g.
		Graphs.addAllVertices(this.grafo, this.dao.getVertici(genere, this.idMap));
		//avere un idmap dv metto tt i possibili oggetti che recupero dal database 
		//poi dalla mappa mi recuperero le possibili canzoni che saranno i miei vertici
		//quindi ho bisogno di una mappa di identità in cui la chiave è ID che è intero, i valori saranno invece delle track
		
		
		//aggiungo gli ARCHI:
		//Due CANZONI sono COLLEGATE tra loro SE CONDIVIDONO lo STESSO FORMATO di file (MediaType). 
	    //Il PESO dell’arco, sempre positivo, rappresenta il valore assoluto della DIFFERENZA di DURATA tra le due canzoni (delta durata),
		//espressa in millisecondi.
		for(Adiacenza a : this.dao.getArchi(genere, idMap)) { //nella query abbiamo già escluso le coppie invertite quindi avrò un arco una sola volta o altrimenti l'arco viene sovrascritto ma va bene in quanto è un arco nn orientato 
			Graphs.addEdgeWithVertices(this.grafo, a.getT1(), a.getT2(), a.getPeso());
		}
		
		System.out.println("Grafo creato!");
		System.out.println(String.format("# Vertici: %d", this.grafo.vertexSet().size()));
		System.out.println(String.format("# Archi: %d", this.grafo.edgeSet().size()));
	}
	
	
	//ci serve un metodo che ci restituisca il numero di vertici:
	public int nVertici() {
		 return this.grafo.vertexSet().size();
	}
	
	public int nArchi() {
		return this.grafo.edgeSet().size();
	}
	
	//d.
	// cerca arco di peso max
	// con la possibilità di avere + di un arco avente lo stesso peso max
	public List<Adiacenza> getDeltaMassimo() {
		List<Adiacenza> result = new ArrayList<Adiacenza>();
		//cerca delta max:
		int max = 0;
		for(DefaultWeightedEdge e : this.grafo.edgeSet()) {
			int peso = (int) this.grafo.getEdgeWeight(e);
			if(peso > max) {
				result.clear();
				result.add(new Adiacenza(this.grafo.getEdgeSource(e),
										 this.grafo.getEdgeTarget(e), peso));
				max = peso;
			} else if (peso == max) {
				result.add(new Adiacenza(this.grafo.getEdgeSource(e),//nn cancelliamo ma lo andiamo a mettere di fianco
										 this.grafo.getEdgeTarget(e), peso));
			}
		}
		return result;
	}
	


	
	//c.
	public boolean grafoCreato() {//metodo che ci dice se il grafo è stato creato o no
		if(this.grafo == null)
			return false;
		else 
			return true;
	}
	
	//punto 2
	//metodi ricorsivi quindi un metodo che imposta la ricorsione e uno che invoca il metodo veramente ricorsivo
	

	//1 MODO:
	/*public List<Track> cercaLista(Track c, int m){//la canzone migliore che l'utente seleziona dalla lista e tale canzone deve essre presente nella lista migliore, poi riceverà anche la memoria totale del mp3
													//da c recupero il formato
		//recupero la componenete connessa di c
		List<Track> canzoniValide = new ArrayList<Track>();  //rimepita dalle canzoni della componente connessa
		ConnectivityInspector<Track, DefaultWeightedEdge> ci = 
				new ConnectivityInspector<>(this.grafo);
		canzoniValide.addAll(ci.connectedSetOf(c));
		
		List<Track> parziale = new ArrayList<>();
		listaMigliore = new ArrayList<>();
		parziale.add(c);
		
		cerca(parziale,canzoniValide,m); //nn ci serve il livello per come lo staimo strutturando
		
		return listaMigliore;
	}
	
	private void cerca(List<Track> parziale, List<Track> canzoniValide, int m) {

		//controllo soluzione migliore
		if(parziale.size() > listaMigliore.size()) {
			listaMigliore = new ArrayList<>(parziale); /sovrascrivo la lista
		}
		
		for(Track t : canzoniValide) {
			if(!parziale.contains(t) && (sommaMemoria(parziale) + t.getBytes()) <= m) { //un filtro: facciamo la ricorsione sl se sappiamo che ci porta a strade utili
																						// vado avanti ssl nn contiene t altrimenti aggiungiamo sempre la prima track, inutile aggiungere canzoni che ci faccia forare la dimnsione max
																						 //questi sn condizioni di terminazione
				parziale.add(t);
				cerca(parziale, canzoniValide,m);// <--lancio la ricorsione
				parziale.remove(parziale.size()-1);// tolgo quello che ho messo prima
			}
		}
		
		
	}*/
	
	//2 MODO:
	public List<Track> cercaLista(Track c, int m){
		//recupero la componenete connessa di c
		Set<Track> componenteConnessa;
		ConnectivityInspector<Track, DefaultWeightedEdge> ci = new ConnectivityInspector<>(this.grafo);
		componenteConnessa = ci.connectedSetOf(c);
		
		List<Track> canzoniValide = new ArrayList<Track>();
		canzoniValide.add(c);
		componenteConnessa.remove(c);
		canzoniValide.addAll(componenteConnessa);
		
		List<Track> parziale = new ArrayList<>();
		listaMigliore = new ArrayList<>();
		parziale.add(c);
		
		cerca(parziale,canzoniValide,m, 1);
		
		return listaMigliore;
	}
	
	private void cerca(List<Track> parziale, List<Track> canzoniValide, int m, int L) {
		
		if(sommaMemoria(parziale) > m) //condizioni di temrminazione : controlliamo la memoria
			return;
		
		//parziale è valida
		if(parziale.size() > listaMigliore.size()) {
			listaMigliore = new ArrayList<>(parziale);
		}
		
		if(L == canzoniValide.size())//condizione di terminazione : controllo il livello 
			return;
		
		parziale.add(canzoniValide.get(L));
		cerca(parziale, canzoniValide,m, L +1);
		parziale.remove(canzoniValide.get(L));
		cerca(parziale,canzoniValide,m, L+1);
	}
	
	
	private int sommaMemoria (List<Track> canzoni) { //ci restituisca la memoria data una lista di track
		int somma = 0;
		for(Track t : canzoni) {
			somma += t.getBytes();
		}
		return somma;
	}
	
	
	
	public List<Track> getVertici(){// mi ritorna tt i vertici del grafo
		return new ArrayList<>(this.grafo.vertexSet());
	}
	
	
	

	
	
	
	
	
	
	
}
