module com.darwinsys.ee {

	// Java and Jakarta APIs
	requires transitive java.desktop;
	requires transitive java.prefs;
	requires transitive java.sql;
	requires java.net.http;
	requires java.sql.rowset;
	requires jakarta.annotation;
	requires jakarta.cdi;
	requires jakarta.ejb;
	requires jakarta.faces;
	requires jakarta.inject;
	requires jakarta.mail;
	requires jakarta.persistence;
	requires jakarta.servlet.jsp;
	requires jakarta.servlet;
	requires jakarta.validation;

	// Third-party APIs
	requires com.darwinsys.api;

	// What we make available
	exports action;
	exports auth;
	exports com.darwinsys.jsptags;
	exports com.darwinsys.servlet;
	exports entity;
	exports gui;
	exports jsf;
	exports model;
}
