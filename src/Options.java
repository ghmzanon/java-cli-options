import java.security.InvalidParameterException;
import java.util.Arrays;

public class Options {

    private final Option[] options;

    public Options(Option ...options) {
        this.options = options;
    }

    public boolean processAndStop(String[] args) throws InvalidParameterException {
        Option lastOption = null;
        for (String arg : args) {
            final Option option = getOption(arg);
            if (option != null) {
                lastOption = option;
                lastOption.initialize();
                continue;
            }
            if (lastOption == null) {
                throw new InvalidParameterException("Invalid option '" + arg + "'.");
            }
            lastOption.setParameter(arg);
        }
        if (lastOption == null) {
            throw new InvalidParameterException("Specify an option in order to proceed.");
        }
        for (final Option option : options) {
            if (!option.isOptional() && !option.wasInitialized()) {
                throw new InvalidParameterException("Required option " + Arrays.toString(option.getOptions()) + " not defined.");
            }
            if (option.wasInitialized() && option.finish()) {
                return true;
            }
        }
        return false;
    }

    private Option getOption(String arg) {
        for (Option option : options) {
            if (option.hasOption(arg)) {
                return option;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[");
        for (int i = 0; i < options.length - 1; i++) {
            stringBuilder.append("["+ options[i] + "], ");
        }
        stringBuilder.append("[" + options[options.length - 1] + "]");
        stringBuilder.append("]");
        return stringBuilder.toString();
    }

    public String listOptions() {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[Options]\n");
        for (int i = 0; i < options.length - 1; i++) {
            stringBuilder.append("\t" + options[i].describe() + "\n");
        }
        stringBuilder.append("\t" + options[options.length - 1].describe());
        return stringBuilder.toString();
    }
    
}
