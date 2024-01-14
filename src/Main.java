import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List; // Added import for List

public class Main {

    private JFrame frame;
    private JTextArea log;
    private JTextField apkInput;

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

        JButton sideload = new JButton("Sideload APK"); // Changed Button to JButton
        sideload.addActionListener(new SideloadButtonListener());

        frame.add(log);
        frame.add(apkInput);
        frame.add(sideload);

        frame.setVisible(true);
    }

    private class SideloadButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            sideload();
        }
    }

    public void sideload() {
        log.append("Trying: " + apkInput.getText() + "\n");

        if (apkInput.getText().toLowerCase().endsWith(".apk")) {

            log.append("Path is valid!");

            if (apkInput.getText().startsWith("http")) {
                apkInput.setText("");
                log.append("Downloading APK …");
                return;
            }

            File apkFile = new File(apkInput.getText());

            if (apkFile.exists()) {
                List<String> adbDevicesOutput = CommandExecuter.execute("adb devices | head -n 2 | tail -n 1");
                log.append("\nSideloading " + apkFile + " to\n " + adbDevicesOutput.get(0));
                apkInput.setText("");
                log.append("\nADB Log ----------------------------");

                List<String> adbInstallOutput = CommandExecuter.execute("adb install " + apkFile);
                for (String line : adbInstallOutput) {
                    log.append("\n" + line);
                }

                log.append("\n-----------------------------------------");
                log.append("\nDone");
                return;
            }

            log.append("\nERROR: APK cannot be found!");
            apkInput.setText("");
            log.append("\n========================================");
            return;
        }

        log.append("ERROR: APK cannot be found!");
    }
}
