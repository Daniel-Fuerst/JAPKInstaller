import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CommandExecuter {

    public static List<String> execute(String cmd) {
        List<String> outputLines = new ArrayList<>();
        Runtime r = Runtime.getRuntime();
        String[] commands = {"bash", "-c", cmd};

        try {
            Process p = r.exec(commands);
            p.waitFor();
            BufferedReader b = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;

            while ((line = b.readLine()) != null) {
                System.out.println(line);
                outputLines.add(line);
            }

            b.close();
        } catch (Exception e) {
            System.err.println("Failed to execute bash with command: " + cmd);
            e.printStackTrace();
        }

        return outputLines;
    }

    public static void main(String[] args) {
        // Example usage:
        String command = "ls -l; echo 'Hello, multi-line commands!';";
        List<String> result = execute(command);

        for (String line : result) {
            System.out.println("Result line: " + line);
        }
    }
}
