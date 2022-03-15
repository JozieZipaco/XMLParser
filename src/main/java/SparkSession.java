
public class SparkSession {

    String Master;
    String AppName;
    String Config;

    void init(String master, String appname, String config) {
        this.Master = master;
        this.AppName = appname;
        this.Config= config ;
    }
}
