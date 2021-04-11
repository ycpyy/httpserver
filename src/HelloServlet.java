
import com.yc.javax.servlet.http.HttpServlet;
import com.yc.javax.servlet.http.HttpServletRequest;
import com.yc.javax.servlet.http.HttpServletResponse;

public class HelloServlet extends HttpServlet {

    public HelloServlet(){
        System.out.println("HelloServlet 的构造方法");
    }

    @Override
    public void init(){
        System.out.println("HelloServlet 的init方法");
    }


    protected void doPost(HttpServletRequest req, HttpServletResponse resp){
        System.out.println("HelloServlet 的doPost方法");
    }

    protected void doGet(HttpServletRequest req,HttpServletResponse resp){
        System.out.println("HelloServlet 的doGet方法");
    }

}
