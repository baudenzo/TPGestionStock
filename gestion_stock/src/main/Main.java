package main;

import vue.VueConnexion;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            VueConnexion vueConnexion = new VueConnexion();
            vueConnexion.setVisible(true);
        });
    }
}