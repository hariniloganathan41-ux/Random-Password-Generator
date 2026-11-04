import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.ActionEvent;
import java.security.SecureRandom;

public class PasswordGeneratorApp {

    // Character sets
    private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String DIGITS = "0123456789";
    private static final String SYMBOLS = "!@#$%^&*()_+[]{}|;:,.<>?";

    private SecureRandom random = new SecureRandom();

    // Swing components
    private JFrame frame;
    private JTextField passwordField;
    private JSlider lengthSlider;
    private JLabel lengthLabel;
    private JCheckBox lowerCheck, upperCheck, numberCheck, symbolCheck;
    private JLabel strengthLabel;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new PasswordGeneratorApp().createAndShowGUI();
        });
    }

    private void createAndShowGUI() {
        frame = new JFrame("Password Generator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(480, 320);
        frame.setLocationRelativeTo(null);

        JPanel main = new JPanel(new BorderLayout(10, 10));
        main.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        // Top - title
        JLabel title = new JLabel("🔐 Password Generator", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        main.add(title, BorderLayout.NORTH);

        // Center - controls and display
        JPanel center = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;

        // Password field + Copy button
        passwordField = new JTextField();
        passwordField.setEditable(false);
        passwordField.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JButton copyBtn = new JButton("Copy");
        copyBtn.addActionListener(this::onCopy);

        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        center.add(passwordField, c);

        c.gridx = 1;
        c.gridy = 0;
        c.gridwidth = 1;
        center.add(copyBtn, c);

        // Length slider and label
        lengthLabel = new JLabel("Length: 8");
        lengthSlider = new JSlider(4, 24, 8);
        lengthSlider.setMajorTickSpacing(4);
        lengthSlider.setPaintTicks(true);
        lengthSlider.setPaintLabels(false);
        lengthSlider.addChangeListener(this::onLengthChange);

        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 2;
        center.add(lengthLabel, c);

        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 2;
        center.add(lengthSlider, c);

        // Checkboxes
        JPanel checks = new JPanel(new GridLayout(2, 2, 8, 8));
        lowerCheck = new JCheckBox("Include Lowercase (a-z)", true);
        upperCheck = new JCheckBox("Include Uppercase (A-Z)", true);
        numberCheck = new JCheckBox("Include Numbers (0-9)", true);
        symbolCheck = new JCheckBox("Include Symbols (!@#...)", false);
        checks.add(lowerCheck);
        checks.add(upperCheck);
        checks.add(numberCheck);
        checks.add(symbolCheck);

        c.gridx = 0;
        c.gridy = 3;
        c.gridwidth = 2;
        center.add(checks, c);

        // Generate button
        JButton generateBtn = new JButton("Generate");
        generateBtn.addActionListener(this::onGenerate);

        c.gridx = 0;
        c.gridy = 4;
        c.gridwidth = 2;
        center.add(generateBtn, c);

        // Strength label
        strengthLabel = new JLabel("Strength: -", SwingConstants.CENTER);
        strengthLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        c.gridx = 0;
        c.gridy = 5;
        c.gridwidth = 2;
        center.add(strengthLabel, c);

        main.add(center, BorderLayout.CENTER);

        frame.setContentPane(main);
        frame.setVisible(true);
    }

    // Event: length slider changed
    private void onLengthChange(ChangeEvent e) {
        int len = lengthSlider.getValue();
        lengthLabel.setText("Length: " + len);
    }

    // Event: Generate button pressed
    private void onGenerate(ActionEvent e) {
        int length = lengthSlider.getValue();
        boolean useLower = lowerCheck.isSelected();
        boolean useUpper = upperCheck.isSelected();
        boolean useNumbers = numberCheck.isSelected();
        boolean useSymbols = symbolCheck.isSelected();

        if (!useLower && !useUpper && !useNumbers && !useSymbols) {
            JOptionPane.showMessageDialog(frame, "Please select at least one character type.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String password = generatePassword(length, useLower, useUpper, useNumbers, useSymbols);
        passwordField.setText(password);
        updateStrength(password);
    }

    // Event: Copy button pressed
    private void onCopy(ActionEvent e) {
        String text = passwordField.getText();
        if (text == null || text.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "No password to copy. Generate one first.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        StringSelection selection = new StringSelection(text);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);
        JOptionPane.showMessageDialog(frame, "Password copied to clipboard!", "Copied", JOptionPane.INFORMATION_MESSAGE);
    }

    // Generate password with validation to include required character types
    private String generatePassword(int length, boolean useLower, boolean useUpper, boolean useNumbers, boolean useSymbols) {
        StringBuilder pool = new StringBuilder();
        if (useLower) pool.append(LOWER);
        if (useUpper) pool.append(UPPER);
        if (useNumbers) pool.append(DIGITS);
        if (useSymbols) pool.append(SYMBOLS);

        String poolStr = pool.toString();
        StringBuilder pw = new StringBuilder(length);

        // Ensure at least one of each chosen type is included
        // Put required chars first, then fill rest randomly
        if (useLower) pw.append(randomCharFrom(LOWER));
        if (useUpper) pw.append(randomCharFrom(UPPER));
        if (useNumbers) pw.append(randomCharFrom(DIGITS));
        if (useSymbols) pw.append(randomCharFrom(SYMBOLS));

        // Fill remaining
        while (pw.length() < length) {
            pw.append(randomCharFrom(poolStr));
        }

        // Shuffle to avoid predictable prefixes
        return shuffleString(pw.toString());
    }

    private char randomCharFrom(String s) {
        return s.charAt(random.nextInt(s.length()));
    }

    private String shuffleString(String input) {
        char[] a = input.toCharArray();
        for (int i = a.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char tmp = a[i];
            a[i] = a[j];
            a[j] = tmp;
        }
        return new String(a);
    }

    // Update strength label (Weak / Medium / Strong) using simple scoring
    private void updateStrength(String password) {
        int score = 0;
        if (password.length() >= 8) score++;
        if (password.length() >= 12) score++; // bonus for long length
        if (password.matches(".*[A-Z].*")) score++;
        if (password.matches(".*[a-z].*")) score++;
        if (password.matches(".*[0-9].*")) score++;
        if (password.matches(".*[^A-Za-z0-9].*")) score++;

        // Determine result and color
        if (score <= 2) {
            strengthLabel.setText("\u2B24 Weak (🔴)"); // circle + text
            strengthLabel.setForeground(new Color(200, 40, 40));
        } else if (score <= 4) {
            strengthLabel.setText("\u25CF Medium (🟠)");
            strengthLabel.setForeground(new Color(220, 140, 20));
        } else {
            strengthLabel.setText("\u25CF Strong (🟢)");
            strengthLabel.setForeground(new Color(30, 140, 30));
        }
    }
}
