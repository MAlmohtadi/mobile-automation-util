package jo.aspire.api.automationUtil.configuration.services;

import com.google.gson.JsonElement;
import jo.aspire.generic.Parsers;
import jo.aspire.web.automationUtil.StateHelper;
import org.apache.http.Header;
import org.apache.http.ParseException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.List;

/**
 * Created by MSrour on 11/24/2016.
 */
public class ResponseResult {

    private ThreadLocal<String> _httpResponseResultAsString = new ThreadLocal();
    private ThreadLocal<String> _valueToCompare = new ThreadLocal();
    private ThreadLocal<String> _jsonFilePath = new ThreadLocal();
    private ThreadLocal<List<HttpServiceConfigurations.JSONCheckRule>> _jsonCheckRules = new ThreadLocal();
    private ThreadLocal<Header[]> _headers = new ThreadLocal();
    private ThreadLocal<Integer> _statusCode = new ThreadLocal();
    private ThreadLocal<String> _serviceName = new ThreadLocal();
    private ThreadLocal<HttpServiceRequest> _httpServiceRequest = new ThreadLocal();

    ResponseResult(
            HttpServiceRequest httpServiceRequest,
            String serviceName,
            String httpResponseResultAsString,
            String valueToCompare,
            String jsonFilePath,
            List<HttpServiceConfigurations.JSONCheckRule> jsonCheckRules,
            Header[] headers,
            int statusCode
    ) {
        this._httpServiceRequest.set(httpServiceRequest);
        this._httpResponseResultAsString.set(httpResponseResultAsString);
        this._valueToCompare.set(valueToCompare);
        this._jsonFilePath.set(jsonFilePath);
        this._jsonCheckRules.set(jsonCheckRules);
        this._statusCode.set(statusCode);
        this._headers.set(headers);
        this._serviceName.set(serviceName);
    }

    public String getResultAsString() {
        return this._httpResponseResultAsString.get();
    }

    public JsonElement getResultAsJson() {
        Parsers parser = new Parsers();
        JsonElement json = null;
        try {
            json = parser.asJson(getResultAsString());
        } catch (ParseException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return json;
    }

    public Document getResultAsXml() {
        Parsers parser = new Parsers();
        Document document = null;
        try {
            document = parser.asXML(getResultAsString());
        } catch (ParseException | IOException | ParserConfigurationException
                | SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return document;
    }

    public String getValueToCompare() {
        return this._valueToCompare.get();
    }

    public List<HttpServiceConfigurations.JSONCheckRule> getJsonCheckRules() {
        return _jsonCheckRules.get();
    }

    public int getStatusCode() {
        return this._statusCode.get();
    }

    public Header[] getHeaders() {
        return this._headers.get();
    }

    public String getJsonFilePath() {
        return this._jsonFilePath.get();
    }

    public String getResultAsStringFromStoryStore(String sotreKey) {
        return StateHelper.getStoryState(sotreKey).toString();
    }

    public String getResultAsStringFromStepStore(String sotreKey) {
        return StateHelper.getStepState(sotreKey).toString();
    }

    public String getServiceRequestBody()
    {
        return _httpServiceRequest.get().getServiceRequestBody();
    }
}
