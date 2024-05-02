package de.htwberlin.dbtech_demo;

import java.sql.Connection;
import java.sql.SQLException;

import de.htwberlin.domain.Kunde;
import de.htwberlin.utils.DbCred;
import de.htwberlin.utils.JdbcUtils;

public class JdbcDemo {

	public static VersicherungJdbc versicherung;
	
	public static void main(String[] args) {
		

		JdbcUtils.loadDriver(DbCred.driverClass);
		try (Connection connection = JdbcUtils.getConnectionViaDriverManager(
			DbCred.url, DbCred.user, DbCred.password)) {
			versicherung.setConnection(connection);
			printAllProducts();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		
	}

	public static void printAllProducts() {
		 System.out.println(versicherung.kurzBezProdukte().toString());
	}


	public static void printKundeById() {
		Kunde kunde = versicherung.findKundeById(1);	
		System.out.println(kunde.toString());
	}

}
