import java.security.InvalidParameterException;
import java.util.Arrays;

public class App {

    private static String singleParam;

    private static String[] multipleParams;

    private static NoParamCallback versionCallback = new NoParamCallback() {
        public boolean performActionAndStop() {
            System.out.println("Version 1.0.0");
            return true;
        };
    };

    private static NoParamCallback helpCallback = new NoParamCallback() {
        public boolean performActionAndStop() {
            System.out.println(options.listOptions());
            return true;
        };
    };

    private static SingleParamCallback setSingleParamCallback = new SingleParamCallback() {
        public boolean performActionAndStop(String param) {
            singleParam = param;
            return false;
        };  
    };

    private static MultipleParamsCallback setMultipleParamsCallback = new MultipleParamsCallback() {
        public boolean performActionAndStop(String[] params) {
            multipleParams = params;
            return false;
        };
    };

    private static Options options = new Options(
        new Option(versionCallback, true, "Show version", "-v", "--version"),
        new Option(helpCallback, true, "Show help", "-h", "--help"),
        new Option(setSingleParamCallback, true, "Specify a single parameter", "-s", "--single"),
        new Option(setMultipleParamsCallback, false, "Specify multiple parameters", "-m", "--multiple")
    );

    public static void main(String[] args) {
        try {
            if (!options.processAndStop(args)) {
                System.out.println("SingleParam: " + singleParam);
                System.out.println("MultipleParams: " + Arrays.toString(multipleParams));
            }
        } catch (InvalidParameterException e) {
            System.out.println("Error: " + e.getMessage());
            System.out.println(options.listOptions());
        }
    }

}
