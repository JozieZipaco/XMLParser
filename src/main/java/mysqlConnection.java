public class mysqlConnection {

    String ip;
    String user;
    String password;

    mysqlConnection init (String host, String user, String pass){
        this.ip= host;
        this.user = user;
        this.password = password;
        return this;
    }

    void setUpConnection(){

    }
}
