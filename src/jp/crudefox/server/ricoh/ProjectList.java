package jp.crudefox.server.ricoh;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.crudefox.server.ricoh.db.DBProjectTable;
import jp.crudefox.server.ricoh.db.DBProjectTable.ProjectRow;
import jp.crudefox.server.ricoh.db.DBSessionTable;
import jp.crudefox.server.ricoh.db.DBSessionTable.SessionRow;
import jp.crudefox.server.ricoh.util.CFUtil;
import jp.crudefox.server.ricoh.util.TextUtil;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * Servlet implementation class Login
 */
@WebServlet(
		description = "プロジェクトの一覧",
		urlPatterns = { "/ProjectList" },
		initParams = {
				@WebInitParam(name = "sid", value = "")
		})
public class ProjectList extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public ProjectList() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub


		doProc(request, response);

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub

		doProc(request, response);

	}

	private void doProc(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		CFUtil.initMySQLDriver();

		response.setContentType("application/json; charset=utf-8");


		try{

			String sid = CFUtil.getParam(request, "sid");
			//String project_id = CFUtil.getParam(request, "project_id");

			if(TextUtil.isEmpty(sid)) throw new Exception("non sid");
			//if(TextUtil.isEmpty(project_id)) throw new Exception("non project id.");


	         //データベースへの接続
			Connection cn = Const.getDefaultDBConnection();

	         DBSessionTable db_st = new DBSessionTable(cn);
	         SessionRow sr = db_st.getBySessionID(sid);

	         if(sr==null){
	        	 cn.close();
	        	 throw new Exception("not login.");
	         }

//			if(project_id == null) project_id = "LPWKfLCkJborUPAggfNIcWtfPPERzAlR";


			DBProjectTable db_pro = new DBProjectTable(cn);
//			ProjectRow pr = db_pro.getById(project_id);
//			if(pr==null) throw new Exception("not exist project.");

			List<ProjectRow> prlist = db_pro.listByAuthor(sr.user_id);
	         if(prlist==null) throw new Exception("err list project..");


			PrintWriter pw = response.getWriter();
			response.setStatus(HttpServletResponse.SC_OK);

	         ObjectMapper om = new ObjectMapper();
	         om.configure(SerializationFeature.INDENT_OUTPUT  , true);

	         LinkedHashMap<String, Object> b = new LinkedHashMap<String, Object>();
	         ArrayList<LinkedHashMap<String, Object>> bb = new ArrayList<LinkedHashMap<String,Object>>();

	         for(int i=0;i<prlist.size();i++){
	        	 ProjectRow pr = prlist.get(i);

	        	LinkedHashMap<String, Object> bi = new LinkedHashMap<String, Object>();
	        	bi.put("project_id", pr.id);
	        	bi.put("project_name", pr.name);
	        	bi.put("author_id", pr.author_id);
	        	bi.put("publish_ur", pr.publish_url);

	        	bb.add(bi);
	         }

	         b.put("result", "OK");
	         b.put("data_list", bb);

	         String json = om.writeValueAsString(b);
	         pw.write(json);

	         //接続のクローズ
	         cn.close();
	     }
	     catch(Exception e){
	        e.printStackTrace();

			//response.setContentType("application/json; charset=utf-8");

	        ///response.setStatus( HttpServletResponse.SC_FORBIDDEN );

	        response.sendError( HttpServletResponse.SC_BAD_REQUEST, e.getMessage() );

	     }

	}

//	private void outputImage(HttpServletResponse response, Blob image) throws IOException, SQLException{
//
//		response.setContentType("image/*");
//
//		if(image==null){
//			response.setStatus(HttpServletResponse.SC_NO_CONTENT);
//			return ;
//		}
//
//		ServletOutputStream os = response.getOutputStream();
//		InputStream is = image.getBinaryStream();
//
//		int len=0;
//		byte[] buf = new byte[1024];
//		while((len=is.read(buf))!=-1){
//			os.write(buf,0,len);
//		}
//
//	}
//
//	private void outputString(HttpServletResponse response, String str) throws IOException{
//
//		response.setContentType("text/plain; charset=utf-8");
//
//		if(str==null){
//			response.setStatus(HttpServletResponse.SC_NO_CONTENT);
//			return ;
//		}
//
//        PrintWriter pw = response.getWriter();
//        pw.write(str);
//
//	}






}

