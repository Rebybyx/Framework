package com.zh.activiti.util;

import com.zh.activiti.entity.Version;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.*;
import java.util.Iterator;





//import com.common.object.SmsSendResponseObject;

/**
 * @description 解析xml字符串
 */
public class XmlUtil {

	/**
	 * @description 将xml字符串转换成Version
	 * @return Version
	 */
	public static Version readStringXmlOut(String thefile) {
		Document doc = null;
		Version xmlBean = new Version();
		File f = new File(thefile);

		if (f.exists() == false) {// 文件不存在
			return xmlBean;

		} else {// 文件存在
			try {

				doc = readXMLByDom4j(f);

				Element rootElt = doc.getRootElement(); // 获取根节点

				Iterator startIter = rootElt.elementIterator();

				while (startIter.hasNext()) {
					Element recordEle = (Element) startIter.next();
					String name = recordEle.getName();
					String ss = recordEle.getStringValue();
					if (recordEle.getName().equals("version")) {
						xmlBean.setVersion(recordEle.getStringValue());
					} else if (recordEle.getName().equals("name")) {
						xmlBean.setName(recordEle.getStringValue());
					} else if (recordEle.getName().equals("date")) {
						xmlBean.setDate(recordEle.getStringValue());
					} else if (recordEle.getName().equals("url")) {
						xmlBean.setUrl(recordEle.getStringValue());
					}else if (recordEle.getName().equals("imageVersion")){
						xmlBean.setImageVersion(Integer.valueOf(recordEle.getStringValue()));
					}else if (recordEle.getName().equals("imageUrl")){
						xmlBean.setImageUrl(recordEle.getStringValue());
					}else if (recordEle.getName().equals("msg")){
						xmlBean.setMsg(recordEle.getStringValue());
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			return xmlBean;
		}
	}

	/**
	 * 读取xml内容
	 * 
	 * @param thefile
	 * @return
	 */
	private static Document readXMLByDom4j(File thefile) {
		BufferedReader bufferedreader = null;
		SAXReader saxreader = new SAXReader();
		Document doc = null;
		try {
			InputStream in = new FileInputStream(thefile);
			bufferedreader = new BufferedReader(new InputStreamReader(in,
					"UTF-8"));
			doc = (Document) saxreader.read(bufferedreader);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		} finally {
			try {
				bufferedreader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return doc;
	}


}
