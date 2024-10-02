package gui;

import jakarta.inject.Named;
import jakarta.ejb.Singleton;

/**
 * Constants for use with PrimeFaces DataTable.
 * Arguably some should be in a resource.properties type file...
 */
@Singleton
@Named("pfTableConstants")
public class PfTableConst {

	final static int ROWS = 10;
 
	final static String PAGINATOR_TEMPLATE =
		"{CurrentPageReport}  {FirstPageLink} {PreviousPageLink} {PageLinks} {NextPageLink} {LastPageLink} {RowsPerPageDropdown}";

	final static String ROWS_PER_PAGE_TEMPLATE="5,10,15,25";

	public int getRows() {
		return ROWS;
	}

	public String getPaginatorTemplate() {
		return PAGINATOR_TEMPLATE;
	}

	public String getRowsPerPageTemplate() {
		return ROWS_PER_PAGE_TEMPLATE;
	}
}
