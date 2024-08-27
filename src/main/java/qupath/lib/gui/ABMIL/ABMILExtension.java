package qupath.lib.gui.ABMIL;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import org.controlsfx.control.action.Action;
import qupath.lib.gui.ActionTools;
import qupath.lib.gui.QuPathGUI;
import qupath.lib.gui.extensions.QuPathExtension;
import qupath.lib.gui.tools.MenuTools;

import static ij.Menus.getMenuBar;

public class ABMILExtension implements QuPathExtension {

    @Override
    public void installExtension(QuPathGUI qupath) {

        var openNewWindow = ActionTools.createAction(new ABMILExtensionCommand(qupath), "Run ABMIL");
        var showHistogram = ActionTools.createAction(new DisplayHistogramCommand(qupath), "Display Histogram");
        var filterOnSignificance = ActionTools.createAction(new SelectSignificanceLevelExtensionCommand(qupath), "Set P-value Threshold");

        MenuTools.addMenuItems(qupath.getMenu("Extensions>ABMIL", true), openNewWindow, showHistogram, filterOnSignificance);


    }


    @Override
    public String getName() {
        return "ABMIL extension";
    }

    @Override
    public String getDescription() {
        return "QuPath extension for ABMIL";
    }
}
