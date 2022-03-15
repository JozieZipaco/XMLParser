import org.apache.spark.sql.*;
import org.apache.spark.sql.SparkSession;
import java.util.Properties;

public class Transformations {

    public static void main(String[] args) throws AnalysisException {

        String pathfile_parquet = args[0];
        String pathfile_xes = args[1];


        SparkSession spark = SparkSession
                .builder()
                .master("local")
                .appName("Parser")
                .config("spark.some.config.option", "some-value")
                .getOrCreate();

        spark.sql("set spark.sql.caseSensitive=true");

        String sql =  "CREATE TEMPORARY TABLE filexes USING com.databricks.spark.xml OPTIONS (path 'xes/Hospital_dcc_3_traces.xes',rowTag 'trace', value_tag 'event')";
        String explode_string_col =  "select explode(string) as trace_attribute, event from filexes";
        String sql_explode_event = "select trace_event._value as trace_id,explode(event) as element_event from trace_event";
        String explode_element_event = "select trace_id, explode(element_event.string) as string_event, element_event.date._key as date_key, element_event.date._value as  date_value from second_explode";
        String explain_string_struct = "select trace_id, string_event._key as string_key, string_event._value as string_value, date_key, date_value from thrid_explode";

        spark.sql(sql);

        Dataset explode_trace_attribute = spark.sql(explode_string_col);
        Dataset creazione_colonne =  explode_trace_attribute.select("trace_attribute._key","trace_attribute._value","event");

        Dataset trace_event = creazione_colonne.select("_value", "event").where("_key = 'concept:name'");
        trace_event.createTempView("trace_event");

        Dataset primo_explode_data = spark.sql(sql_explode_event);
        primo_explode_data.createTempView("second_explode");

        Dataset second_explode_event = spark.sql(explode_element_event);
        second_explode_event.createTempView("thrid_explode");

        Dataset data_explosure = spark.sql(explain_string_struct);
        data_explosure.createTempView("data_explosure");

        Dataset trace_activity = spark.sql("Select trace_id,string_value as activity_name from data_explosure where  string_key = 'concept:name'");
        trace_activity = trace_activity.repartition(trace_activity.col("trace_id"));
        trace_activity.printSchema();
        trace_activity.write().format("parquet").save("traceActivity.parquet");


    }
}