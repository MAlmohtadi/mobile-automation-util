package jo.aspire.api.automationUtil;

public class Constants {
	public static String CONFIG_FILE_CONNETION_STRING="ConnectionString";
	public static String CONFIG_FILE_DRIVER="Driver";
	public static String CONFIG_FILE_AUTHORIZATION="Authorization";
	public static String FIEDLS_TOKEN="[FIELDS_TOKEN]";
	public static String URL_PARAMETER="[parameter]";
	
	
	public static String DATA_CHECK_QUERIES_FILE="/config/data_check_queries.txt";
	public static String SERVICE_MAPPING_URLS_FILE = "/src/configs/service_url_mapping.txt";
	public static String COLUMN_MAPPER_FILE="/src/configs/column_mapper.xml";
	public static String SERVICE_INPUT_FILE="/src/configs/service_inputs.xml";
	public static String CONFIG_FILE="/src/configs/config.xml";
	public static String CONFIG_FILE_SERVICES_URL = "ServiceURL";
	public static String DATA_NOT_IN_DB = "Cannot find data in DB. Query %s";
	public static String PARAMETERS_MISMATCH = "parameter count mismatch";
	public static String PARAMETERS_FAILED = "parameter %s failed";
	public static String PARAMETERS_DYNAMIC_TOKEN = "@dcontent@";
	public static String PARAMETERS_TOKEN="@param@";
	public static String PARAMETERS_QUERY="@select";
}
