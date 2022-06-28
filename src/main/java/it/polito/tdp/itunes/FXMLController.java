/**
 * Sample Skeleton for 'Scene.fxml' Controller Class
 */

package it.polito.tdp.itunes;

import java.net.URL;
import java.util.ResourceBundle;

import it.polito.tdp.itunes.model.Adiacenza;
import it.polito.tdp.itunes.model.Genre;
import it.polito.tdp.itunes.model.Model;
import it.polito.tdp.itunes.model.Track;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class FXMLController {

	private Model model;
	
    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="btnCreaGrafo"
    private Button btnCreaGrafo; // Value injected by FXMLLoader

    @FXML // fx:id="btnCreaLista"
    private Button btnCreaLista; // Value injected by FXMLLoader

    @FXML // fx:id="btnMassimo"
    private Button btnMassimo; // Value injected by FXMLLoader

    @FXML // fx:id="cmbCanzone"
    private ComboBox<Track> cmbCanzone; // Value injected by FXMLLoader

    @FXML // fx:id="cmbGenere"
    private ComboBox<Genre> cmbGenere; // Value injected by FXMLLoader
    //il tipo di oggetti che conterra la tendina
    
    @FXML // fx:id="txtMemoria"
    private TextField txtMemoria; // Value injected by FXMLLoader

    @FXML // fx:id="txtResult"
    private TextArea txtResult; // Value injected by FXMLLoader

    
    // metodo invoca la ricorsione 
    @FXML
    void btnCreaLista(ActionEvent event) {
    	txtResult.clear(); //puliamo la tendina
    	Track c = this.cmbCanzone.getValue();
    	if(c == null) {
    		txtResult.appendText("Seleziona una canzone!");
    		return ;
    	}
    	int m;
    	try {
    		m = Integer.parseInt(txtMemoria.getText());
    	}catch (NumberFormatException e) {
    		txtResult.appendText("Inseririci un valore numerico per la memoria");
    		return ;
    	}
    	
    	//facio di nuovo il controllo : se il grfo nn è stato ancora creato allora nn puoi cliccare il bottone 
    	if(!this.model.grafoCreato()) {
    		txtResult.appendText("Crea prima il grafo!");
    		return ;
    	}
    	
    	//dopo i vari controlli cerchiamo la canzone
    	txtResult.appendText("LISTA CANZONI MIGLIORE: \n");
    	for(Track t : this.model.cercaLista(c, m)) {
    		txtResult.appendText(t + "\n");
    	}
    	
    }

    
   /**b. Alla PRESSIONE del BOTTONE “Crea Grafo”, si CREI un GRAFO SEMPLICE, NN ORIENTATO e PESATO,
      // i cui VERTICI sono tutte le CANZONI (Track) di genere g.*/
   /**c. Due CANZONI sono COLLEGATE tra loro SE CONDIVIDONO lo STESSO FORMATO di file (MediaType). 
    //   Il PESO dell’arco, sempre positivo, rappresenta il valore assoluto della DIFFERENZA di DURATA tra le due canzoni (delta durata),
		 espressa in millisecondi.*/
    @FXML
    void doCreaGrafo(ActionEvent event) {
    	txtResult.clear();
    	
    	Genre g = this.cmbGenere.getValue(); //dobbiamo recuperare gli input 
    	//controllo del errore:
    	if(g == null) {
    		txtResult.appendText("Seleziona un genere!");//clicca senza selezionare il genere
    		return ;//nn andiamo avanti
    	}
    	//se l'utente seleziona il genere 
    	//allora creiamo il grafo:
    	this.model.creaGrafo(g);
    	
    	txtResult.appendText("Grafo creato!\n");
    	txtResult.appendText("# Vertici : " + this.model.nVertici() + "\n");
    	txtResult.appendText("# Archi : " + this.model.nArchi() + "\n");
    	//----------------------------------------------------
    	
    	//PUNTO 2 
    	//riempiamo la tendina delle canzoni dp che creiamo il grafo:
    	this.cmbCanzone.getItems().clear();//devo pulire tt le canzoni precedenti
    	this.cmbCanzone.getItems().addAll(this.model.getVertici());
 
    }
    
    /**d. Alla pressione del bottone “Delta Massimo” 
    //TROVARE nel grafo e stampare a video la COPPIA di CANZONI COLLEGATE che abbia DELTA DURATA MAX, 
    //nel formato titolo canzone1, titolo canzone 2, delta durata 
    //Nel caso in cui ci sia più di una coppia che abbia delta durata massimo, stamparle tutte.*/
    @FXML
    void doDeltaMassimo(ActionEvent event) {
    	txtResult.clear();
    	
    	//qui nn c'è input.
    	//l'unico controllo che bisogna fare e che il grafo deve essere stato già creato
    	if(!this.model.grafoCreato()) {
    		txtResult.appendText("Crea prima il grafo!");
    		return ;
    	}
    	//se siamo qui il grafo è stato creato 
    	//allora posso richiamare un mio metodo:
    	for(Adiacenza a : this.model.getDeltaMassimo()) {
    		txtResult.appendText(a.toString() + "\n");
    	}
    	
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert btnCreaGrafo != null : "fx:id=\"btnCreaGrafo\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnCreaLista != null : "fx:id=\"btnCreaLista\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnMassimo != null : "fx:id=\"btnMassimo\" was not injected: check your FXML file 'Scene.fxml'.";
        assert cmbCanzone != null : "fx:id=\"cmbCanzone\" was not injected: check your FXML file 'Scene.fxml'.";
        assert cmbGenere != null : "fx:id=\"cmbGenere\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtMemoria != null : "fx:id=\"txtMemoria\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Scene.fxml'.";
        
    }
    
    public void setModel(Model model) {
    	this.model = model;
    	
    /**a. Permettere all’utente di SELEZIONARE, dall’apposita TENDINA, un GENERE
    	//rimpio la tendina con dei dati che ci arrivano dal modello:*/
    	this.cmbGenere.getItems().addAll(this.model.getGeneri());
    }

}
