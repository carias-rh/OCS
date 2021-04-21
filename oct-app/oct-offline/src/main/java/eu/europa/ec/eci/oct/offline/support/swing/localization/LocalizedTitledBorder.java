package eu.europa.ec.eci.oct.offline.support.swing.localization;

import eu.europa.ec.eci.oct.offline.support.localization.adapter.AbstractLocalizationAdapter;

import javax.swing.border.TitledBorder;

/**
 * @author: micleva
 * @created: 11/11/11
 * @project OCT
 */
public class LocalizedTitledBorder extends TitledBorder {

	private static final long serialVersionUID = 8561401494980025374L;

	public LocalizedTitledBorder(String titleKey) {
        super(titleKey);

        final TitledBorder titledBorder = this;
        new AbstractLocalizationAdapter(titleKey) {
			private static final long serialVersionUID = -3105252343849658022L;

			@Override
            protected void setLocalizedText(String localizedText) {
                titledBorder.setTitle(localizedText);
            }
        };
    }
}
