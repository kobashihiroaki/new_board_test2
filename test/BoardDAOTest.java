package test;

import static org.junit.Assert.*;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.dbunit.IDatabaseTester;
import org.dbunit.JdbcDatabaseTester;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import new_board.BoardDAO;
import new_board.BoardDTO;

public class BoardDAOTest {


//	@Test
//	public void testGetInstance() {
//		assertEquals(bdao, BoardDAO.getInstance());
//	}

	private IDatabaseTester databaseTester;
	private IDatabaseConnection connection;

	public BoardDAOTest() throws Exception {
		//テストクラスをインスタンス化するときに、DBに接続するためのtesterを作成する
		databaseTester = new JdbcDatabaseTester("com.mysql.cj.jdbc.Driver","jdbc:mysql://localhost/board_test?serverTimezone=JST&useUnicode=true&characterEncoding=utf8", "root", "hiroaki");

	}

	@Before
	public void before() throws Exception {
		//テーブルに初期化用のデータを投入する
		IDataSet dataSet =
				new FlatXmlDataSetBuilder().build(new File("data/test_data.xml"));
		databaseTester.setDataSet(dataSet);
		databaseTester.setSetUpOperation(DatabaseOperation.REFRESH);

		databaseTester.onSetup();
	}

	@After
	public void after() throws Exception {
		databaseTester.setTearDownOperation(DatabaseOperation.NONE);
		databaseTester.onTearDown();
	}


	@Test
	public void testGetTopics() throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(new File("data/test_data.xml"));
		Element element = doc.getDocumentElement();

		NodeList nodeList = element.getChildNodes();
		ArrayList<String> id = new ArrayList<String>();
		ArrayList<String> detail = new ArrayList<String>();
		ArrayList<String> start_at = new ArrayList<String>();
		for(int i = 0; i < nodeList.getLength(); i++) {
		    Node node = nodeList.item(i);
		    if(node.getNodeType() == Node.ELEMENT_NODE) {
		        Element name = (Element)node;
		        if(name.getNodeName().equals("board2")) {
		        	id.add(name.getAttribute("id"));
		            detail.add(name.getAttribute("detail"));
		            //日付のフォーマット変換
		            Date date = null;
		    		try {
		                String strDate = name.getAttribute("start_at");
		                SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		                date = sdFormat.parse(strDate);

		            } catch (ParseException e) {
		                e.printStackTrace();
		            }
		    		SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		            String newDate = df.format(date);
		    		start_at.add(newDate);
		        }
		    }
		}


		BoardDAO bdao = BoardDAO.getInstance();
		List <BoardDTO> topics = bdao.getTopics();

		for (int i = 0; i < topics.size(); i++) {
			BoardDTO topic = topics.get(i);
			assertEquals(Integer.parseInt(id.get(i)), topic.getId());
			assertEquals(detail.get(i), topic.getDetail());
			assertEquals(start_at.get(i), topic.getStart_at());
		}
	}



//	@Test
//	public void testPostTopic() throws Exception {
//		BoardDAO bdao = BoardDAO.getInstance();
//		BoardDTO bdto = new BoardDTO();
//		String str = "え？";
//		bdto.setDetail(str);
//		bdao.postTopic(bdto);
//
//		IDataSet expectedDataSet = new FlatXmlDataSetBuilder().build(new File("data/expected.xml"));
//		ITable expectedTable = expectedDataSet.getTable("board2");
//		ITable filteredExpectedTable = DefaultColumnFilter.excludedColumnsTable(expectedTable, new String[]{"id", "start_at"});
//		IDataSet databaseDataSet = databaseTester.getConnection().createDataSet();
//		ITable actualTable = databaseDataSet.getTable("board2");
//		ITable filteredActualTable = DefaultColumnFilter.excludedColumnsTable(actualTable, new String[]{"id", "start_at"});
//		Assertion.assertEquals(filteredExpectedTable, filteredActualTable);
//
//		ITable filteredId = DefaultColumnFilter.excludedColumnsTable(actualTable, new String[]{"detail", "start_at"});
//		ITable filteredStart_at = DefaultColumnFilter.excludedColumnsTable(actualTable, new String[]{"id", "detail"});
//		assertNotNull(filteredId);
//		assertNotNull(filteredStart_at);
//	}
//
//
//
//	@Test
//	public void testDeleteTopic() throws Exception {
//		BoardDAO bdao = BoardDAO.getInstance();
//		BoardDTO bdto = new BoardDTO();
//		bdto.setId(6);
//		bdao.deleteTopic(bdto);
//
//		IDataSet databaseDataSet = databaseTester.getConnection().createDataSet();
//		ITable actualTable = databaseDataSet.getTable("board2");
//		IDataSet expectedDataSet = new FlatXmlDataSetBuilder().build(new File("data/test_data.xml"));
//		ITable expectedTable = expectedDataSet.getTable("board2");
//
//		Assertion.assertEquals(expectedTable, actualTable);
//	}



}
