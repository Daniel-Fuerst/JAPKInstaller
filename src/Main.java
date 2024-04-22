import Utils.CommandExecuter;
import Utils.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;
import java.util.logging.*;

public class Main {

    private JFrame frame;
    private JTextArea log;
    private JTextField apkInput;
    private Logger logger;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Main installer = new Main();
            installer.initialize();
        });
    }

    public void initialize() {
        frame = new JFrame("JAPKInstaller");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(350, 250);
        frame.setLayout(new FlowLayout());
        frame.setResizable(false);

        apkInput = new JTextField();
        apkInput.setToolTipText("Path to APK / URL (must end with .apk)");
        apkInput.setPreferredSize(new Dimension(300, 25));

        log = new JTextArea();
        log.setPreferredSize(new Dimension(300, 140));
        log.setEditable(false);
        log.setAutoscrolls(true);

        JButton sideload = new JButton("Sideload APK");
        sideload.addActionListener(new SideloadButtonListener());

        frame.add(log);
        frame.add(apkInput);
        frame.add(sideload);

        frame.setVisible(true);

        // Initialize logger
        logger = Logger.getLogger(Main.class.getName());
        TextAreaHandler handler = new TextAreaHandler(log);
        handler.setLevel(Level.ALL);
        handler.setFormatter(new SimpleFormatter() {
            @Override
            public synchronized String format(LogRecord lr) {
                return lr.getMessage() + "\n";
            }
        });
        logger.addHandler(handler);
    }

    private class SideloadButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (Constants.OS().toLowerCase().contains("win")) {
                //sideloadWin();
                //TODO: Write Sideload function for Windows
            } else if (Constants.OS().toLowerCase().contains("lin") ||
                    Constants.OS().toLowerCase().contains("un")) {
                sideload();
            } else {
                logger.log(Level.WARNING, "Unsupported OS : " + Constants.OS());
            }
        }
    }

    //TODO: Make actual logging instead of displaying all at once
    public void sideload() {
        logger.log(Level.INFO, "Trying: " + apkInput.getText());

        if (apkInput.getText().toLowerCase().endsWith(".apk")) {

            logger.log(Level.INFO, "Path is valid!");

            if (apkInput.getText().startsWith("http")) {
                apkInput.setText("");
                logger.log(Level.INFO, "Downloading APK …");
                return;
            }

            File apkFile = new File(apkInput.getText());

            if (apkFile.exists()) {
                List<String> adbDevicesOutput = CommandExecuter.executeBash("adb devices | head -n 2 | tail -n 1");
                logger.log(Level.INFO, "Sideloading " + apkFile + " to\n " + adbDevicesOutput.get(0));
                apkInput.setText("");
                logger.log(Level.INFO, "ADB Log ----------------------------");

                // Update log as adb install command is executed
                logger.log(Level.INFO, "Installing APK...");
                List<String> adbInstallOutput = CommandExecuter.executeBash("adb install '" + apkFile + "'");
                for (String line : adbInstallOutput) {
                    logger.log(Level.INFO, line);
                }

                logger.log(Level.INFO, "-----------------------------------------");
                logger.log(Level.INFO, "Done");
                return;
            }

            logger.log(Level.SEVERE, "ERROR: APK cannot be found!");
            apkInput.setText("");
            logger.log(Level.INFO, "========================================");
            return;
        }

        logger.log(Level.SEVERE, "ERROR: APK cannot be found!");
    }

    // Custom handler to append log messages to JTextArea
    private static class TextAreaHandler extends Handler {
        private JTextArea textArea;
        private Formatter formatter = new SimpleFormatter();

        TextAreaHandler(JTextArea textArea) {
            this.textArea = textArea;
        }

        @Override
        public void publish(LogRecord record) {
            String message = formatter.format(record);
            SwingUtilities.invokeLater(() -> textArea.append(message));
        }

        @Override
        public void setFormatter(Formatter newFormatter) {
            formatter = newFormatter;
        }

        @Override
        public Formatter getFormatter() {
            return formatter;
        }

        @Override
        public void flush() {}

        @Override
        public void close() throws SecurityException {}
    }
}
