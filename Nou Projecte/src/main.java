package src;

import java.sql.Connection;
import java.sql.SQLException;
import javax.swing.*;

public class main {

    public static void main(String[] args) {
        try (Connection conn = connexio.getConnection()) {

            System.out.printf("Connected to database %s "
                    + "successfully.%n", conn.getCatalog());
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Vista();
            }
        });
    }

}
