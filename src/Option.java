import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;

public class Option {

    private OptionCallback callback;

    private String description;

    private String[] options;

    private boolean init;

    private boolean optional;

    private String singleParam;

    private ArrayList<String> multipleParams;

    public Option(OptionCallback callback, boolean optional, String description, String ...options) {
        if (callback instanceof MultipleParamsCallback) {
            multipleParams = new ArrayList<>();
        }
        this.options = options;
        this.callback = callback;
        this.description = description;
        this.optional = optional;
        singleParam = null;
        init = false;
    }

    public String[] getOptions() {
        return options;
    }

    public boolean isOptional() {
        return optional;
    }

    public boolean hasOption(String arg) {
        for (String option : options) {
            if (option.equals(arg)) {
                return true;
            }
        }
        return false;
    }

    public void initialize() {
        init = true;
    }

    public boolean wasInitialized() {
        return init;
    }

    public void setParameter(String param) throws InvalidParameterException {
        if (callback instanceof NoParamCallback
                || (callback instanceof SingleParamCallback && singleParam != null)) {
            throw new InvalidParameterException("Invalid option '" + param + "'.");
        }
        if (callback instanceof SingleParamCallback) {
            singleParam = param;
        } else if (callback instanceof MultipleParamsCallback) {
            multipleParams.add(param);
        }
    }

    public boolean finish() throws InvalidParameterException {
        if (callback instanceof SingleParamCallback && singleParam == null) {
            throw new InvalidParameterException("Expected a single parameter but got none.");
        }
        if (callback instanceof MultipleParamsCallback && multipleParams.size() < 2) {
            throw new InvalidParameterException("Expected multiple parameter but got only " + multipleParams.size() + ".");
        }

        if (callback instanceof NoParamCallback) {
            final NoParamCallback noParamCallback = (NoParamCallback) callback;
            return noParamCallback.performActionAndStop();
        } else if (callback instanceof SingleParamCallback) {
            final SingleParamCallback singleParamCallback = (SingleParamCallback) callback;
            return singleParamCallback.performActionAndStop(singleParam);
        } else {
            final MultipleParamsCallback multipleParamsCallback = (MultipleParamsCallback) callback;
            String[] params = new String[multipleParams.size()];
            multipleParams.toArray(params);
            return multipleParamsCallback.performActionAndStop(params);
        }
    }

    @Override
    public String toString() {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("options: ");
        stringBuilder.append(Arrays.toString(options));
        stringBuilder.append(", description: \"");
        stringBuilder.append(description);
        stringBuilder.append("\"");
        if (callback instanceof SingleParamCallback) {
            stringBuilder.append(", parameter: " + singleParam);
        } else if (callback instanceof MultipleParamsCallback) {
            stringBuilder.append(", parameters: ");
            stringBuilder.append(multipleParams.toString());
        }
        stringBuilder.append(", wasInitialized: ");
        stringBuilder.append(init);
        return stringBuilder.toString();
    }

    public String describe() {
        final StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < options.length; i++) {
            stringBuilder.append(options[i]);
            if (i != options.length - 1) {
                stringBuilder.append(", ");
            }
        }
        stringBuilder.append("\t");
        stringBuilder.append(description);
        return stringBuilder.toString();
    }

}