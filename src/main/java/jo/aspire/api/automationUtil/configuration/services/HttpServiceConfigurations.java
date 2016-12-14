package jo.aspire.api.automationUtil.configuration.services;

import java.util.List;

public class HttpServiceConfigurations {

    class HttpServiceConfiguration {
        private String name;
        private String host;
        private String httpMethod;
        private String contentType;
        private String url;
        private String method;
        private List<NameValuePair> requestBodyParams;
        private List<NameValuePair> requestHeaders;
        private List<NameValuePair> requestBodyTemplates;
        private String valueToCompare;
        private String jsonFilePath;
        private List<JSONCheckRule> jsonCheckRules;

        String getName() {
            return name;
        }

        String getHost() {
            return host;
        }

        void setHost(String host) {
            this.host = host;
        }

        String getHttpMethod() {
            return httpMethod;
        }

        void setHttpMethod(String httpMethod) {
            this.httpMethod = httpMethod;
        }

        String getConentType() {
            return contentType;
        }

        void setConentType(String contentType) {
            this.contentType = contentType;
        }

        String getUrl() {
            return url;
        }

        void setUrl(String url) {
            this.url = url;
        }

        String getMethodName() {
            return method;
        }

        String getJSONFilePath() {
            return jsonFilePath;
        }

        List<JSONCheckRule> getJSONCheckRules() {
            return jsonCheckRules;
        }

        List<NameValuePair> getRequestBodyParams() {
            return requestBodyParams;
        }

        List<NameValuePair> getRequestHeaders() {
            return requestHeaders;
        }

        void setRequestHeaders(List<NameValuePair> headers) {
            requestHeaders = headers;
        }

        String getRequestBodyTemplateValue(String templateName) {
            if(null != requestBodyTemplates)
            {
                for (NameValuePair template  : requestBodyTemplates) {
                    if(template.name.equalsIgnoreCase(templateName))
                       return template.value;
                }
            }
            return "";
        }

        String getValueToCompare() {
            return valueToCompare;
        }
    }

    class NameValuePair {

        public String name;
        public String value;
    }

    public class JSONCheckRule {
        public String node;
        public String check;
    }

    class HttpServicesConfigurationCollection {

        private String defaultHost;
        private String defaultHttpMethod;
        private String defaultContentType;
        private List<HttpServiceConfiguration> services;
        private List<NameValuePair> defaultRequestHeaders;

        String getDefaultHost() {
            if (defaultHost == null || defaultHost.trim() == "") {
                try {
                    throw new Exception("Host cannot be empty, there is no value has been set for this configuration.");
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    e.getCause();
                }
            }
            return defaultHost;
        }

        void setDefaultHost(String defaultHost) {
            this.defaultHost = defaultHost;
        }

        String getDefaultHttpMethod() {
            if (defaultHttpMethod == null || defaultHttpMethod.trim() == "") {
                try {
                    throw new Exception("Http method cannot be empty, there is no value has been set for this configuration.");
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    e.getCause();
                }
            }
            return defaultHttpMethod;
        }

        void setDefaultHttpMethod(String defaultHttpMethod) {
            this.defaultHttpMethod = defaultHttpMethod;
        }

        String getDefaultContentType() {
            if (defaultContentType == null || defaultContentType.trim() == "") {
                try {
                    throw new Exception("Content type cannot be empty, there is no value has been set for this configuration.");
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    e.getCause();
                }
            }
            return defaultContentType;
        }

        void setDefaultContentType(String defaultContentType) {
            this.defaultContentType = defaultContentType;
        }

        List<HttpServiceConfiguration> getServices()
        {
            return services;
        };

        List<NameValuePair> getDefaultRequestHeaders() {
            return defaultRequestHeaders;
        }

        void setDefaultRequestHeaders(List<NameValuePair> defaultRequestHeaders) {
            this.defaultRequestHeaders = defaultRequestHeaders;
        }
    }
}
