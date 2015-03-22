package gui;

import javax.inject.Named;

/**
 * Constants for use with PrimeFaces DataTable.
 * Arguably some should be in a resource.properties type file...
 */
@Named("pfTableConstants")
public class PfTableConst {

	final static int ROWS = 10;
 
	final static String PAGINATOR_TEMPLATE =
		"{CurrentPageReport}  {FirstPageLink} {PreviousPageLink} {PageLinks} {NextPageLink} {LastPageLink} {RowsPerPageDropdown}";

	final static String ROWS_PER_PAGE_TEMPLATE="5,10,15,25";
}
