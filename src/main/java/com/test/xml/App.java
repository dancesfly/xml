package com.test.xml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;
import org.dom4j.io.SAXReader;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.core.util.QuickWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.XppDriver;

import net.sf.json.JSONObject;

public class App {
	private static final Class String = null;

	public static void main(String[] args) throws Exception {
		/* xml to map--不循环 */
		Map<String, String> map = new HashMap<String, String>();
		map = parseXml("C:\\a.xml");
		// System.out.println(map);

		/* xml to map --循环 */
		Map<String, Object> map1 = new HashMap<String, Object>();
		map1 = xml2map("C:\\b.xml");
		System.out.println(map1);

		/* xml to json --循环 */
		System.out.println(xml2Json("C:\\b.xml"));
		
		/* Obj to xml */
		
		User user = new User();
		user.setId(11);
		user.setName("vivi");
		List<String> list = new ArrayList<String>();
//		list.add(new Item("hello"));
//		list.add(new Item("world"));
		list.add("a");
		list.add("b");
		user.setListA(list);
		System.out.println(Obj2Xml(user));
		
//		Map<String, Object> map2 = new HashMap<String, Object>();
//		
//		map2.put("begin", user);
//		System.out.println(Obj2Xml(user));
	}

	private static List add(String string) {
		// TODO Auto-generated method stub
		return null;
	}

	/* xml to map--不循环 */
	public static Map<String, String> parseXml(String path) throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		InputStream inputStream = new FileInputStream(path);
		SAXReader reader = new SAXReader();
		Document document = reader.read(inputStream);
		Element root = document.getRootElement();
		List<Element> elementList = root.elements();

		for (Element e : elementList)
			map.put(e.getName(), e.getText());

		inputStream.close();
		inputStream = null;

		return map;
	}

	/* xml to map --循环 */
	public static void element2Map(Map<String, Object> map, Element rootElement) {

		String value = null;
		// 获得当前节点的子节点
		List<Element> elements = rootElement.elements();
		if (elements.size() == 0) {
			// 没有子节点说明当前节点是叶子节点，直接取值
			map.put(rootElement.getName(), rootElement.getText());
		} else if (elements.size() == 1) {
			// 只有一个子节点说明不用考虑list的情况，继续递归
			Map<String, Object> tempMap = new HashMap<String, Object>();
			element2Map(tempMap, elements.get(0));
			map.put(rootElement.getName(), tempMap);
		} else {
			// 多个子节点的话就要考虑list的情况了，特别是当多个子节点有名称相同的字段时
			Map<String, Object> tempMap = new HashMap<String, Object>();
			for (Element element : elements) {
				tempMap.put(element.getName(), null);
			}
			Set<String> keySet = tempMap.keySet();
			for (String string : keySet) {
				Namespace namespace = elements.get(0).getNamespace();
				List<Element> sameElements = rootElement.elements(new QName(string, namespace));
				// 如果同名的数目大于1则表示要构建list
				if (sameElements.size() > 1) {
					List<String> list = new ArrayList<String>();
					for (Element element : sameElements) {
						Map<String, Object> sameTempMap = new HashMap<String, Object>();
						element2Map(sameTempMap, element);
						value = (String) sameTempMap.entrySet().iterator().next().getValue();
						list.add(value);
					}
					map.put(string, list);
				} else {
					// 同名的数量不大于1直接递归
					Map<String, Object> sameTempMap = new HashMap<String, Object>();
					element2Map(sameTempMap, sameElements.get(0));
					value = (String) sameTempMap.entrySet().iterator().next().getValue();
					map.put(string, value);
				}
			}
		}
	}

	public static Map<String, Object> xml2map(String path) throws FileNotFoundException {
		Document doc = null;
		try {
			InputStream inputStream = new FileInputStream(path);
			SAXReader reader = new SAXReader();
			doc = reader.read(inputStream);
		} catch (DocumentException e) {
			System.out.println("parse text error : " + e);
		}
		Element rootElement = doc.getRootElement();
		Map<String, Object> mapXml = new HashMap<String, Object>();
		element2Map(mapXml, rootElement);
		return mapXml;
	}

	public static String xml2Json(String path) throws FileNotFoundException {
		Document doc = null;
		try {
			InputStream inputStream = new FileInputStream(path);
			SAXReader reader = new SAXReader();
			doc = reader.read(inputStream);
		} catch (DocumentException e) {
			System.out.println("parse text error : " + e);
		}
		Element rootElement = doc.getRootElement();
		Map<String, Object> mapXml = new HashMap<String, Object>();
		element2Map(mapXml, rootElement);
		String jsonXml = JSONObject.fromObject(mapXml).toString();
		return jsonXml;
	}

	/* object to xml */
	public static String Obj2Xml(Object obj) {
		xstream.alias("xml", obj.getClass());
		xstream.alias("item", new String().getClass());
		return xstream.toXML(obj);
	}
	
	private static XStream xstream = new XStream(new XppDriver() {  
        public HierarchicalStreamWriter createWriter(Writer out) {  
            return new PrettyPrintWriter(out) {  
                boolean cdata = true;
                
                public void startNode(String name, Class clazz) {  
                    super.startNode(name, clazz);  
                }  
       
                protected void writeText(QuickWriter writer, String text) {  
                    if (cdata) {  
                        writer.write("<![CDATA[");  
                        writer.write(text);  
                        writer.write("]]>");  
                    } else {  
                        writer.write(text);  
                    }  
                }  
            };  
        }  
    });  
}
