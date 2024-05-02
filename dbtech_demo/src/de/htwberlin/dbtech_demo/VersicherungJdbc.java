package de.htwberlin.dbtech_demo;

import java.math.BigDecimal;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


import de.htwberlin.domain.Kunde;
import de.htwberlin.exceptions.DataException;
import de.htwberlin.jdbc.IVersicherungJdbc;

public class VersicherungJdbc implements IVersicherungJdbc {


	private Connection connection;
	@Override
	public void setConnection(Connection connection) {
		 this.connection = connection;
		 if (connection == null) {
		      throw new DataException("Connection not set");
		  }
	}

	@Override
	public List<String> kurzBezProdukte() {
	    List<String> kurzBezList = new ArrayList<>();
	    String query = "SELECT KurzBez FROM Produkt";
	    try (PreparedStatement statement = connection.prepareStatement(query)) {
	        try (ResultSet resultSet = statement.executeQuery()) {
	            while (resultSet.next()) {
	                String kurzBez = resultSet.getString("KurzBez");
	                kurzBezList.add(kurzBez);
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return null;
	    }
	    return kurzBezList;
		
	}

	@Override
	public Kunde findKundeById(Integer id) {
        String query = "SELECT * FROM Kunden WHERE ID = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    // Kunden-Daten aus dem ResultSet lesen
                    String name = resultSet.getString("Name");
                    LocalDate geburtsdatum = resultSet.getDate("Geburtsdatum").toLocalDate();
                    // Kunden-Objekt erstellen und zurückgeben
                    return new Kunde(id, name, geburtsdatum);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
	}

	@Override
	public void createVertrag(Integer id, Integer produktId, Integer kundenId, LocalDate versicherungsbeginn) {
		  String query = "INSERT INTO Vertrag (ID, ProduktID, KundenID, Versicherungsbeginn) VALUES (?, ?, ?, ?)";
	        try (PreparedStatement statement = connection.prepareStatement(query)) {
	            statement.setInt(1, id);
	            statement.setInt(2, produktId);
	            statement.setInt(3, kundenId);
	            statement.setDate(4, java.sql.Date.valueOf(versicherungsbeginn));
	            statement.executeUpdate();
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
		
	}

	
	@Override
	public BigDecimal calcMonatsrate(Integer vertragsId) {
		 String query = "SELECT DATEDIFF(Versicherungsende, Versicherungsbeginn) AS days FROM Vertrag WHERE ID = ?";
		    try (PreparedStatement statement = connection.prepareStatement(query)) {
		        statement.setInt(1, vertragsId);
		        try (ResultSet resultSet = statement.executeQuery()) {
		            if (resultSet.next()) {
		                int days = resultSet.getInt("days");
		                // Annahme: Die monatliche Rate beträgt 1% des Gesamtbetrags
						BigDecimal monatsrate = BigDecimal.valueOf(days).divide(BigDecimal.valueOf(30), 2, BigDecimal.ROUND_HALF_UP);
		                return monatsrate;
		            }
		        }
		    } catch (SQLException e) {
		        e.printStackTrace();
		    }
		    return null;
	}

}
