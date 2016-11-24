package jo.aspire.generic;


import jo.aspire.api.automationUtil.configuration.services.HttpServiceConfigurations;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.JSONCompareResult;
import org.skyscreamer.jsonassert.comparator.DefaultComparator;
import org.skyscreamer.jsonassert.comparator.JSONCompareUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class JsonComparator extends DefaultComparator {

    public static final String CONTAINS = "contains";
    public static final String CONTAINS_OPERATOR = "~";
    public static final String NOT_NULL = "notNull";
    public static final String NOT_NULL_OPERATOR = "!";
    public static final String IGNORE = "ignore";
    public static final String IGNORE_OPERATOR = "";

    private ThreadLocal<List<String>> _nodesToIgnore = new ThreadLocal<>();
    private ThreadLocal<List<String>> _nodesToCheckNull = new ThreadLocal<>();
    private ThreadLocal<List<String>> _nodesToCheckContains = new ThreadLocal<>();

    public JsonComparator(List<HttpServiceConfigurations.JSONCheckRule> checkRules, JSONCompareMode mode) {
        super(mode);
        this.setNodesToIgnore(new ArrayList<String>());
        this.setNodesToCheckNull(new ArrayList<String>());
        this.setNodesToCheckContains(new ArrayList<String>());

        if(checkRules != null)
        {
            for ( HttpServiceConfigurations.JSONCheckRule rule : checkRules) {
                switch (rule.check)
                {
                    case CONTAINS:
                    case CONTAINS_OPERATOR:
                    {
                        getNodesToCheckContains().add(rule.node);
                        break;
                    }
                    case NOT_NULL:
                    case NOT_NULL_OPERATOR:
                    {
                        getNodesToCheckNull().add(rule.node);
                        break;
                    }
                    case IGNORE:
                    case IGNORE_OPERATOR:
                    {
                        getNodesToIgnore().add(rule.node);
                        break;
                    }
                    default://IGNORE, IGNORE_OPERATOR
                    {
                        getNodesToIgnore().add(rule.node);
                        break;
                    }
                }
            }
        }

    }
    public JsonComparator(JSONCompareMode mode, List<String> nodesToIgnore) {
        super(mode);
        this.setNodesToIgnore(nodesToIgnore);
        this.setNodesToCheckNull(new ArrayList<String>());
        this.setNodesToCheckContains(new ArrayList<String>());
    }

    public JsonComparator(JSONCompareMode mode, List<String> nodesToIgnore, List<String> nodesToCheckNull) {
        super(mode);
        this.setNodesToIgnore(nodesToIgnore);
        this.setNodesToCheckNull(nodesToCheckNull);
        this.setNodesToCheckContains(new ArrayList<String>());
    }

    public JsonComparator(JSONCompareMode mode, List<String> nodesToIgnore, List<String> nodesToCheckNull, List<String> nodesToCheckContains) {
        super(mode);
        this.setNodesToIgnore(nodesToIgnore);
        this.setNodesToCheckContains(nodesToCheckContains);
        this.setNodesToCheckNull(nodesToCheckNull);
    }

    public JsonComparator(JSONCompareMode mode) {
        super(mode);
    }

    public JsonComparator() {
        this(JSONCompareMode.STRICT);
    }

    @Override
    protected void checkJsonObjectKeysExpectedInActual(String prefix, JSONObject expected, JSONObject actual, JSONCompareResult result) throws JSONException {
        Set expectedKeys = JSONCompareUtil.getKeys(expected);
        Iterator i$ = expectedKeys.iterator();

        while (i$.hasNext()) {
            String key = (String) i$.next();
            Object expectedValue = expected.get(key);
            if (actual.has(key)) {
                Object actualValue = actual.get(key);
                if (shouldIgnorePath(JSONCompareUtil.qualify(prefix, key))) {
                    System.err.println("Ignoring JSON under key: " + key);
                } else {
                    this.compareValues(JSONCompareUtil.qualify(prefix, key), expectedValue, actualValue, result);
                }
            } else {
                result.missing(prefix, key);
            }
        }
    }

    @Override
    public void compareValues(String prefix, Object expectedValue, Object actualValue, JSONCompareResult result) throws JSONException {

        if (shouldIgnorePath(prefix)) {
            System.err.println("Ignoring JSON under key: " + prefix);
            return;//nothing to compare
        }

        if (expectedValue instanceof Number && actualValue instanceof Number) {
            if (((Number) expectedValue).doubleValue() != ((Number) actualValue).doubleValue()) {
                result.fail(prefix, expectedValue, actualValue);
            }
        } else if (expectedValue.getClass().isAssignableFrom(actualValue.getClass())) {
            if (expectedValue instanceof JSONArray) {
                compareJSONArray(prefix, (JSONArray) expectedValue, (JSONArray) actualValue, result);
            } else if (expectedValue instanceof JSONObject) {
                compareJSON(prefix, (JSONObject) expectedValue, (JSONObject) actualValue, result);
            } else if (shouldCheckNullPath(prefix)) {
                if (actualValue == null || actualValue.toString().isEmpty()) {
                    result.fail(formatFailureMessage("not null", prefix, "Not Null or Empty", actualValue));
                }
            } else if (shouldCheckContainsPath(prefix)) {
                expectedValue = expectedValue != null ? expectedValue : "";
                if (actualValue == null || !actualValue.toString().toLowerCase().contains(expectedValue.toString().toLowerCase())) {
                    result.fail(formatFailureMessage("contains", prefix, expectedValue, actualValue));
                }
            } else if (!expectedValue.equals(actualValue)) {
                result.fail(formatFailureMessage("equal", prefix, expectedValue, actualValue));
            }
        } else {
            result.fail(formatFailureMessage("general", prefix, expectedValue, actualValue));
        }
    }

    private Boolean shouldIgnorePath(String path) {
        if(getNodesToIgnore() == null) return false;
        return getNodesToIgnore().contains(path);
    }

    private Boolean shouldCheckNullPath(String path) {
        if(getNodesToCheckNull() == null) return false;
       return getNodesToCheckNull().contains(path);
    }

    private Boolean shouldCheckContainsPath(String path) {
        if(getNodesToCheckContains() == null) return false;
            return getNodesToCheckContains().contains(path);
    }

    private String formatFailureMessage(String action, String field, Object expected, Object actual) {
        return "Action: "
                + action
                + "\nnode: "
                + field
                + "\nExpected: "
                + describe(expected)
                + "\n     got: "
                + describe(actual)
                + "\n";
    }

    private static String describe(Object value) {
        if (value instanceof JSONArray) {
            return "a JSON array";
        } else if (value instanceof JSONObject) {
            return "a JSON object";
        } else {
            return value.toString();

        }
    }

    private List<String> getNodesToIgnore() {
        return _nodesToIgnore.get();
    }

    private void setNodesToIgnore(List<String> nodesToIgnore) {
        _nodesToIgnore.set(nodesToIgnore != null ? nodesToIgnore : new ArrayList<String>());
    }

    private List<String> getNodesToCheckNull() {
        return _nodesToCheckNull.get();
    }

    private void setNodesToCheckNull(List<String> nodesToCheckNull) {
        _nodesToCheckNull.set(nodesToCheckNull != null ? nodesToCheckNull : new ArrayList<String>());
    }

    private List<String> getNodesToCheckContains() {
        return _nodesToCheckContains.get();
    }

    private void setNodesToCheckContains(List<String> nodesToCheckContains) {
        _nodesToCheckContains.set(nodesToCheckContains != null ? nodesToCheckContains : new ArrayList<String>());
    }
}
