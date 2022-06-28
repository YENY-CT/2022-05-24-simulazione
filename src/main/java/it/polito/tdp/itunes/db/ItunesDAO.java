package it.polito.tdp.itunes.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.itunes.model.Adiacenza;
import it.polito.tdp.itunes.model.Album;
import it.polito.tdp.itunes.model.Artist;
import it.polito.tdp.itunes.model.Genre;
import it.polito.tdp.itunes.model.MediaType;
import it.polito.tdp.itunes.model.Playlist;
import it.polito.tdp.itunes.model.Track;

public class ItunesDAO {
	
	public List<Album> getAllAlbums(){
		final String sql = "SELECT * FROM Album";
		List<Album> result = new LinkedList<>();
		
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();

			while (res.next()) {
				result.add(new Album(res.getInt("AlbumId"), res.getString("Title")));
			}
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("SQL Error");
		}
		return result;
	}
	
	public List<Artist> getAllArtists(){
		final String sql = "SELECT * FROM Artist";
		List<Artist> result = new LinkedList<>();
		
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();

			while (res.next()) {
				result.add(new Artist(res.getInt("ArtistId"), res.getString("Name")));
			}
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("SQL Error");
		}
		return result;
	}
	
	public List<Playlist> getAllPlaylists(){
		final String sql = "SELECT * FROM Playlist";
		List<Playlist> result = new LinkedList<>();
		
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();

			while (res.next()) {
				result.add(new Playlist(res.getInt("PlaylistId"), res.getString("Name")));
			}
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("SQL Error");
		}
		return result;
	}
	
	
	//public List<Track> getAllTracks(){ //<-- lo modifico : riceve la mappa e me lo riempie
	public void getAllTracks(Map<Integer,Track> idMap){
		final String sql = "SELECT * FROM Track";
		// List<Track> result = new ArrayList<Track>(); <-- cancello in quanto è void
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();

			while (res.next()) {
				//result.add(new(Track(res.getInt("TrackId"), res.getString("Name"),...)
				if(!idMap.containsKey(res.getInt("TrackId"))) { //<-- aggiunto per vedere se la canzone si trova nella mappa se nn c'è mi creo la nuova canzone
					Track t = new Track(res.getInt("TrackId"), res.getString("Name"), 
							res.getString("Composer"), res.getInt("Milliseconds"), 
							res.getInt("Bytes"),res.getDouble("UnitPrice"));
					idMap.put(t.getTrackId(), t);//<-- aggiunto
				}
			
			}
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("SQL Error");
		}
		//return result; <-- cancello
	}
	
	
	public List<Genre> getAllGenres(){
		final String sql = "SELECT * FROM Genre";
		List<Genre> result = new LinkedList<>();
		
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();

			while (res.next()) {
				result.add(new Genre(res.getInt("GenreId"), res.getString("Name")));
			}
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("SQL Error");
		}
		return result;
	}
	
	public List<MediaType> getAllMediaTypes(){
		final String sql = "SELECT * FROM MediaType";
		List<MediaType> result = new LinkedList<>();
		
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();

			while (res.next()) {
				result.add(new MediaType(res.getInt("MediaTypeId"), res.getString("Name")));
			}
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("SQL Error");
		}
		return result;
	}

	//b.
	//vertici sono tutte le canzoni di genere g.	
	public List<Track> getVertici(Genre genere, Map<Integer,Track> idMap) {
		String sql = "select TrackId "
				+ "from track "
				+ "where GenreId = ?";
		
		List<Track> result = new ArrayList<>();
		
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, genere.getGenreId());
			ResultSet res = st.executeQuery();
			
			while(res.next())
				result.add(idMap.get(res.getInt("TrackId")));
			
			conn.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("SQL Error");
		  }
		
		return result;
	}
	
	//c.
	//Due canzoni sono collegate tra loro se condividono lo stesso formato di file (MediaType). 
	//Il peso dell’arco, sempre positivo, rappresenta il valore assoluto della differenza di durata tra le due canzoni, espressa in millisecondi.
	//per prendere gli archi:
	public List<Adiacenza> getArchi(Genre genere, Map<Integer,Track> idMap){
		
		String sql = "select t1.TrackId as t1, t2.TrackId as t2, abs(t1.milliseconds - t2.milliseconds) as delta " //metto abs per prendere solo il valore positivo
				+ "from track t1, track t2 "
				+ "where t1.TrackId > t2.TrackId and t1.MediaTypeId = t2.MediaTypeId and t1.GenreId = ? and t1.GenreId = t2.GenreId";
		
		List<Adiacenza> result = new ArrayList<>();
		
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, genere.getGenreId());
			ResultSet res = st.executeQuery();
			
			while(res.next()) {
				result.add(new Adiacenza(idMap.get(res.getInt("t1")), 
										 idMap.get(res.getInt("t2")), res.getInt("delta")));
			}
			
			conn.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("SQL Error");
		  }
		
		return result;
	}
	
	
	
}
