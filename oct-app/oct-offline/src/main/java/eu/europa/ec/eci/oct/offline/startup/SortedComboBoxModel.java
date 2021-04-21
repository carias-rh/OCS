package eu.europa.ec.eci.oct.offline.startup;

import java.util.*;
import javax.swing.*;

public class SortedComboBoxModel extends DefaultComboBoxModel {

	private static final long serialVersionUID = -4241075932805662360L;

	public SortedComboBoxModel() {
        super();
    }

	public SortedComboBoxModel(List<ComboMenuItemData> list){
    	Collections.sort(list);
        int size = list.size();
        for (int i = 0; i < size; i++) {
            super.addElement(list.get(i));
        }
    }
}