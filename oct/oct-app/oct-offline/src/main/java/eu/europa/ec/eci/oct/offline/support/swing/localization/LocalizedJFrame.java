package eu.europa.ec.eci.oct.offline.support.swing.localization;

import javax.swing.JFrame;

import eu.europa.ec.eci.oct.offline.support.localization.adapter.AbstractLocalizationAdapter;

/**
 * @author: micleva
 * @created: 11/8/11
 * @project OCT
 */
public class LocalizedJFrame extends JFrame {

	private static final long serialVersionUID = -5710968363729984121L;

	public LocalizedJFrame(String messageKey) {
		super(messageKey);

		final JFrame jFrame = this;
		new AbstractLocalizationAdapter(messageKey) {
			private static final long serialVersionUID = -6859879681740576623L;

			@Override
			protected void setLocalizedText(String localizedText) {
				jFrame.setTitle(localizedText);
			}
		};
	}
}
