package eu.europa.ec.eci.oct.offline.support.summary;

/**
 * @author: micleva
 * @created: 11/14/11
 * @project OCT
 */
public interface SummaryCalculatorListener {

    /**
     * Called whenever the selection summary gets updated
     *
     * @param selectionSummary the updated selection summary
     */
    void afterSelectionSummaryChanged(SelectionSummary selectionSummary);
}
