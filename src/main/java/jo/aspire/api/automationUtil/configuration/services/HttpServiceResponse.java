package jo.aspire.api.automationUtil.configuration.services;

import jo.aspire.api.automationUtil.configuration.services.HttpServiceConfigurations.HttpServiceConfiguration;
import jo.aspire.generic.Parsers;
import jo.aspire.web.automationUtil.StateHelper;
import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;

import java.io.IOException;

public class HttpServiceResponse {

    private ThreadLocal<CloseableHttpResponse> _httpResponse = new ThreadLocal();
    private ThreadLocal<String> _httpResponseResultAsString = new ThreadLocal();
    private ThreadLocal<String> _serviceName = new ThreadLocal();
    private ThreadLocal<HttpServiceConfiguration> _httpServiceConfiguration = new ThreadLocal();
    private ThreadLocal<ResponseResult> _responseResult = new ThreadLocal();

    public HttpServiceResponse(final CloseableHttpResponse httpResponse, final HttpServiceConfiguration httpServiceConfiguration, final String serviceName) {
        setHttpServiceConfiguration(httpServiceConfiguration);
        setServiceName(serviceName);
        setHttpResponse(httpResponse);
        setResultAsString(parseResultAsString());
        setResponseResult();
    }

    public HttpServiceResponse getHttpServiceResponse() {
        return this;
    }

    public HttpServiceResponse setResultAsStringToStoryStore(String sotreKey) {
        StateHelper.setStoryState(getServiceName() + sotreKey, getResultAsString());
        return this;
    }

    public String getResultAsStringFromStoryStore(String sotreKey) {
        return StateHelper.getStoryState(getServiceName() + sotreKey).toString();
    }

    public HttpServiceResponse setResultAsStringToStepStore(String sotreKey) {
        StateHelper.setStepState(getServiceName() + sotreKey, getResultAsString());
        return this;
    }

    public String getResultAsStringFromStepStore(String sotreKey) {
        return StateHelper.getStepState(getServiceName() + sotreKey).toString();
    }

    public String getResultAsString() {
        return _httpResponseResultAsString.get();
    }

    protected HttpServiceResponse setResultAsString(final String httpResponseResultAsString) {
        _httpResponseResultAsString = new ThreadLocal<String>() {
            @Override
            public String initialValue() {
                return httpResponseResultAsString;
            }
        };
        return this;
    }

    private String getValueToCompare() {
        return getHttpServiceConfiguration().getValueToCompare();
    }

    private void setHttpResponse(final CloseableHttpResponse httpResponse) {
        _httpResponse = new ThreadLocal<CloseableHttpResponse>() {
            @Override
            public CloseableHttpResponse initialValue() {
                return httpResponse;
            }
        };
    }

    private CloseableHttpResponse getHttpResponse() {
        return _httpResponse.get();
    }

    private void setServiceName(final String serviceName) {
        _serviceName = new ThreadLocal<String>() {
            @Override
            public String initialValue() {
                return serviceName;
            }
        };
    }

    private String getServiceName() {
        return _serviceName.get();
    }

    private void setHttpServiceConfiguration(final HttpServiceConfiguration httpServiceConfiguration) {
        _httpServiceConfiguration = new ThreadLocal<HttpServiceConfiguration>() {
            @Override
            public HttpServiceConfiguration initialValue() {
                return httpServiceConfiguration;
            }
        };
    }

    private HttpServiceConfiguration getHttpServiceConfiguration() {
        return _httpServiceConfiguration.get();
    }

    private String parseResultAsString() {
        Parsers parser = new Parsers();
        String value = null;
        try {
            value = parser.asString(getHttpResponse());
        } catch (ParseException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return value;
    }

    public ResponseResult getResponseResult() {
        return _responseResult.get();
    }

    private void setResponseResult() {
        this._responseResult.set(new ResponseResult(getServiceName(),
                getResultAsString(),
                getValueToCompare(),
                getHttpServiceConfiguration().getJSONFilePath(),
                getHttpServiceConfiguration().getJSONCheckRules(),
                getHttpResponse() != null ? getHttpResponse().getAllHeaders() : null,
                getHttpResponse() != null && getHttpResponse().getStatusLine() != null ? getHttpResponse().getStatusLine().getStatusCode() : 0
        ));
    }
}