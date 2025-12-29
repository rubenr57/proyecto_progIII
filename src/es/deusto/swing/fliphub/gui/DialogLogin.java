package es.deusto.swing.fliphub.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

//Login para la interfaz
public class DialogLogin extends JDialog {

    //campos del formulario
    private JTextField txtUser;
    private JPasswordField txtPass;
    private JCheckBox chkShow;

    //resultado del login
    private boolean ok = false;

    public DialogLogin(JFrame parent) {
        super(parent, "Login", true);
        initUI();
        setLocationRelativeTo(parent);
    }

    private void initUI() {
        setSize(420, 260);
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        //panel con degradado
        GradientPanel root = new GradientPanel(
                new Color(20, 30, 45),
                new Color(45, 75, 110)
        );
        root.setLayout(new BorderLayout(10, 10));
        root.setBorder(new EmptyBorder(14, 14, 14, 14));
        setContentPane(root);

        // Cabecera: logo + texto 
        JPanel header = new JPanel(new BorderLayout(10, 0));
        header.setOpaque(false); // para que se vea el degradado

        JLabel lblLogo = new JLabel(loadIcon("/icons/LogoFlipHub.png", 48, 48));
        JLabel lblTitle = new JLabel("FlipHub", SwingConstants.LEFT);
        lblTitle.setFont(lblTitle.getFont().deriveFont(Font.BOLD, 22f));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSub = new JLabel("Inicia sesión para continuar");
        lblSub.setForeground(new Color(230, 235, 240));

        JPanel titleBox = new JPanel(new GridLayout(2, 1));
        titleBox.setOpaque(false);
        titleBox.add(lblTitle);
        titleBox.add(lblSub);

        header.add(lblLogo, BorderLayout.WEST);
        header.add(titleBox, BorderLayout.CENTER);

        root.add(header, BorderLayout.NORTH);

        //Centro: formulario 
        //Uso de IA generativa
        JPanel formCard = new JPanel(new GridBagLayout());
        formCard.setBorder(new EmptyBorder(12, 12, 12, 12));
        formCard.setBackground(Color.WHITE);
        formCard.setOpaque(true);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        //Usuario
        gbc.gridx = 0; gbc.gridy = 0;
        formCard.add(new JLabel("Usuario:"), gbc);

        gbc.gridx = 1; gbc.gridy = 0;
        txtUser = new JTextField();
        txtUser.setColumns(16);
        formCard.add(txtUser, gbc);

        //Contraseña
        gbc.gridx = 0; gbc.gridy = 1;
        formCard.add(new JLabel("Contraseña:"), gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        txtPass = new JPasswordField();
        txtPass.setColumns(16);
        formCard.add(txtPass, gbc);

        //Checkbox mostrar contraseña
        gbc.gridx = 1; gbc.gridy = 2;
        chkShow = new JCheckBox("Mostrar contraseña");
        chkShow.setBackground(Color.WHITE);
        formCard.add(chkShow, gbc);

        root.add(formCard, BorderLayout.CENTER);

        //Sur: botones
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttons.setOpaque(false);

        JButton btnCancel = new JButton("Cancelar");
        JButton btnLogin = new JButton("Entrar");

        buttons.add(btnCancel);
        buttons.add(btnLogin);

        root.add(buttons, BorderLayout.SOUTH);

        //Acciones

        //Mostrar/ocultar contraseña
        final char defaultEcho = txtPass.getEchoChar();
        chkShow.addActionListener(e -> {
            if (chkShow.isSelected()) {
                txtPass.setEchoChar((char) 0); //muestra texto
            } else {
                txtPass.setEchoChar(defaultEcho); //vuelve a ocultar
            }
        });

        //intenta login
        ActionListener doLogin = e -> tryLogin();
        btnLogin.addActionListener(doLogin);
        txtUser.addActionListener(doLogin);
        txtPass.addActionListener(doLogin);

        //cancelar
        btnCancel.addActionListener(e -> {
            ok = false;
            dispose();
        });

        //contraseña y usuario por defecto de prueba
        txtUser.setText("admin");
        txtPass.setText("admin");

        //enfocar el usuario al abrir
        SwingUtilities.invokeLater(() -> txtUser.requestFocusInWindow());
    }

    //valida la contraseña y el usuario
    private void tryLogin() {
        String user = txtUser.getText().trim();
        String pass = new String(txtPass.getPassword());

        if ("admin".equals(user) && "admin".equals(pass)) {
            ok = true;
            dispose();
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Usuario o contraseña incorrectos.\nPrueba: admin / admin",
                    "Login",
                    JOptionPane.WARNING_MESSAGE
            );
            txtPass.requestFocusInWindow();
            txtPass.selectAll();
        }
    }

    public boolean isOk() {
        return ok;
    }

    // Helpers 

    private Icon loadIcon(String resourcePath, int w, int h) {
        java.net.URL url = getClass().getResource(resourcePath);
        if (url == null) return null; // si no hay logo, no pasa nada
        Image img = new ImageIcon(url).getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }

    
     //Panel que pinta un degradado vertical
    private static class GradientPanel extends JPanel {
        private final Color top;
        private final Color bottom;

        public GradientPanel(Color top, Color bottom) {
            this.top = top;
            this.bottom = bottom;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            int w = getWidth();
            int h = getHeight();
            GradientPaint gp = new GradientPaint(0, 0, top, 0, h, bottom);
            g2.setPaint(gp);
            g2.fillRect(0, 0, w, h);
            g2.dispose();
        }
    }
}
