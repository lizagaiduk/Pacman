
package PacMan;

        import javax.swing.*;
        import java.awt.*;
        import java.awt.event.ActionEvent;
        import java.awt.event.ActionListener;
        import java.io.File;
        import java.io.IOException;

public class GameOver extends JFrame {
    private JLabel gameOverLogo, nameLabel;
    private static Font font;
    private JButton submitButton;
    private JTextField nameField;
    private int finalScore;

     static {
        try {
            font = Font.createFont(Font.TRUETYPE_FONT, new File(
                    "src/mainFont.ttf")).deriveFont(25f);
        } catch (FontFormatException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public GameOver(int finalScore) {
        this.finalScore=finalScore;
        setTitle("Game Over");
        setSize(1000, 800);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 400, 80));
        mainPanel.setBackground(Color.BLACK);

        ImageIcon logoIcon=new ImageIcon(new ImageIcon("src/images/LogoGameOver.png").getImage().getScaledInstance(420, 150, Image.SCALE_SMOOTH));
        gameOverLogo = new JLabel(logoIcon);
        mainPanel.add(gameOverLogo);

        nameLabel = new JLabel("Enter your name:");
        nameLabel.setFont(font);
        nameLabel.setForeground(Color.YELLOW);
        nameField = new JTextField();
        nameField.setPreferredSize(new Dimension(300,50));
        nameField.setForeground(Color.magenta);
        nameField.setFont(font);

        submitButton = new JButton("Submit");
        submitButton.setBackground(Color.BLUE);
        submitButton.setForeground(Color.cyan);
        submitButton.setSize(new Dimension(200,80));
        submitButton.setFont(font);
        submitButton.setFocusPainted(false);
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               try {
                   String playerName = nameField.getText().trim();
                   if (playerName.isEmpty()) {
                       JOptionPane.showMessageDialog(null, "Please enter a name.", "Input Error", JOptionPane.ERROR_MESSAGE);
                       return;
                   }
                   Record.addRecord(playerName, finalScore);
                   dispose();
                   Menu menu = new Menu();
                   menu.setVisible(true);
               }catch (Exception e1){
                   JOptionPane.showMessageDialog(null, "Failed to add record: " + e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
               }
            }
        });


        mainPanel.add(nameLabel);
        mainPanel.add(nameField);
        mainPanel.add(submitButton);

        add(mainPanel, BorderLayout.CENTER);

    }


}

