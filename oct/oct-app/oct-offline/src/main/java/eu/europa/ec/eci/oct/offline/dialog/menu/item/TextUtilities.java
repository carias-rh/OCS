package eu.europa.ec.eci.oct.offline.dialog.menu.item;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: micleva
 * @created: 1/4/12
 * @project OCT
 */
class TextUtilities {

    private TextUtilities() {
    }

    public static Action findAction(Action actions[], String key) {
        Map<String, Action> commands = new HashMap<String, Action>();
        for (Action action : actions) {
            commands.put((String) action.getValue(Action.NAME), action);
        }
        return commands.get(key);
    }
}
