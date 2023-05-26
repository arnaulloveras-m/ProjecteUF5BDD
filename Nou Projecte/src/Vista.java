package src;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Vista extends JPanel implements ActionListener {

    private JLabel jlab;
    private JLabel jlab2;
    private JButton b1, b2;
    private JTextField textFieldNom, textFieldPreu, textFieldCategoria, textFieldNom2;
    private JLabel labelImatge;
    private JLabel imagenLabel;
    private String rutaImatge;

    JPanel area = new JPanel();
    JFrame jfrm = new JFrame("MENÚ FOSTERS HOLLYWOOD");
    JPanel inputPanel = new JPanel(new GridLayout(5, 2, 5, 5));
    JPanel inputPanel2 = new JPanel(new GridLayout(1, 2, 5, 5));

    private Connection conn;



    public Vista() {

        jfrm.setLayout(new BorderLayout());
        jfrm.setSize(375, 300);
        jfrm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        jlab = new JLabel("Menú Fosters Hollywood");
        jlab.setVerticalAlignment(SwingConstants.CENTER);

        area.setLayout(new BoxLayout(area, BoxLayout.Y_AXIS));

        // Agregar un espacio vertical entre el texto y los botones
        int verticalSpacing = 10;
        jlab.setBorder(new EmptyBorder(verticalSpacing, 0, 0, 0));

        JPanel textPanel = new JPanel();
        textPanel.add(jlab);
        area.add(textPanel);

        inputPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel labelNom = new JLabel("Nom del producte:");
        textFieldNom = new JTextField(20);
        JLabel labelPreu = new JLabel("Preu del producte:");
        textFieldPreu = new JTextField(20);
        JLabel labelCategoria = new JLabel("Categoria del producte:");
        textFieldCategoria = new JTextField(20);
        labelImatge = new JLabel("Imatge del producte:");

        JLabel labelNom2 = new JLabel("Nom del producte:");
        textFieldNom2 = new JTextField(20);

        inputPanel.add(labelNom);
        inputPanel.add(textFieldNom);
        inputPanel.add(labelPreu);
        inputPanel.add(textFieldPreu);
        inputPanel.add(labelCategoria);
        inputPanel.add(textFieldCategoria);
        inputPanel2.add(labelNom2);
        inputPanel2.add(textFieldNom2);
        inputPanel2.setVisible(false);

        area.add(inputPanel);
        area.add(inputPanel2);

        JButton buttonSeleccionarImatge = new JButton("Seleccionar");
        buttonSeleccionarImatge.addActionListener(this);
        inputPanel.add(labelImatge);
        inputPanel.add(buttonSeleccionarImatge);

        this.b1 = new JButton("Inserir");
        this.b1.addActionListener(this);
        this.b2 = new JButton("Mostrar");
        this.b2.addActionListener(this);

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new FlowLayout());

        Dimension buttonSize = new Dimension(80, 30);
        b1.setPreferredSize(buttonSize);
        b2.setPreferredSize(buttonSize);
        b1.setBackground(Color.cyan);
        b2.setBackground(Color.cyan);

        buttonsPanel.add(b1);
        buttonsPanel.add(b2);

        area.add(buttonsPanel);

        jlab2 = new JLabel("");
        area.add(jlab2);

        imagenLabel = new JLabel();
        area.add(imagenLabel);

        jfrm.add(area);

        Dimension pantalla = Toolkit.getDefaultToolkit().getScreenSize();
        int height = pantalla.height;
        int width = pantalla.width;
        jfrm.setSize(width / 2, height / 2);
        jfrm.setLocationRelativeTo(null);
        jfrm.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == b1) {
            if (inputPanel.isVisible()) {
                String nomProducte = textFieldNom.getText();
                String preuProducte = textFieldPreu.getText();
                String categoriaProducte = textFieldCategoria.getText();

                insertarProducto(nomProducte, preuProducte, categoriaProducte, rutaImatge);

                jlab2.setText("Producte insertat en la base de dades.");

                textFieldNom.setText("");
                textFieldPreu.setText("");
                textFieldCategoria.setText("");
                rutaImatge = null;
                imagenLabel.setIcon(null);

                imagenLabel.setIcon(null);
            } else {
                inputPanel.setVisible(true);
                jlab2.setText("");
                imagenLabel.setIcon(null);
                inputPanel2.setVisible(false);
            }
        } else if (e.getSource() == b2) {
            if (inputPanel2.isVisible()) {
                jlab2.setText("");
                inputPanel.setVisible(false);
                textFieldPreu.setText("");
                textFieldCategoria.setText("");
                rutaImatge = null;

                String nomProducte = textFieldNom2.getText();
                mostrarProducto(nomProducte);
            } else {
                inputPanel2.setVisible(true);
                inputPanel.setVisible(false);
            }
        } else if (e.getSource() instanceof JButton) {
            JFileChooser fileChooser = new JFileChooser();
            int seleccion = fileChooser.showOpenDialog(this);
            if (seleccion == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                rutaImatge = file.getAbsolutePath();
            }
        }
    }

    private void insertarProducto(String nomProducte, String preuProducte, String categoriaProducte, String rutaImatge) {
        try (Connection conn = connexio.getConnection()){
            String sql = "INSERT INTO producte (nomProducte, preuProducte, categoriaProducte, imatgeProducte) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, nomProducte);
            statement.setString(2, preuProducte);
            statement.setString(3, categoriaProducte);
            statement.setString(4, rutaImatge);
            statement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void mostrarProducto(String nomProducte) {
        try (Connection conn = connexio.getConnection()){
            String sql = "SELECT * FROM producte WHERE nomProducte = ?";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, nomProducte);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String nombre = resultSet.getString("nomProducte");
                String precio = resultSet.getString("preuProducte");
                String categoria = resultSet.getString("categoriaProducte");
                String imagen = resultSet.getString("imatgeProducte");

                jlab2.setText("Nom: " + nombre + ", Preu: " + precio + "€, Categoria: " + categoria);

                if (imagen != null) {
                    mostrarImagen(imagen);
                } else {
                    imagenLabel.setIcon(null);
                }
            } else {
                jlab2.setText("No s'ha trobat cap producte amb aquest nom.");
                imagenLabel.setIcon(null);
            }
        } catch (SQLException ex) {
            System.out.println("Error: " + ex);
        }
    }

    private void mostrarImagen(String rutaImagen) {
        ImageIcon imagen = new ImageIcon(rutaImagen);
        Image img = imagen.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        imagenLabel.setIcon(new ImageIcon(img));
    }
}