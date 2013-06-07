package xg.config.xml;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import xg.task.ProcessTask;
import xg.task.TaskStatus;
import xg.task.cmd.Command;
import xg.task.cmd.CommandFactory;



public class XmlTest {
	@SuppressWarnings("unchecked")
	public static void readNode(Element root, String prefix) {  
        if (root == null) return;  
        // 获取属性  
        List<Attribute> attrs = root.attributes();  
        if (attrs != null && attrs.size() > 0) {  
            System.err.print(prefix);  
            for (Attribute attr : attrs) {  
                System.err.print(attr.getValue() + " ");  
            }  
            System.err.println();  
        }  
        // 获取他的子节点  
        List<Element> childNodes = root.elements();  
        prefix += "\t";  
        for (Element e : childNodes) {  
        	System.out.print(prefix); 
        	if(e.isTextOnly())
        		System.out.println(e.getName()+":"+e.getText());
        	else
        		System.out.println(e.getName());
        	
            readNode(e, prefix);  
        }  
    }
	
	static<T> T instance(Class<T> clazz){
		try {
			return clazz.newInstance();
		} catch (InstantiationException e) {
		} catch (IllegalAccessException e) {
		}
		return null;
	}
	
	static Command createCommand(Element element){
		if(null!=element){
			String type = element.attribute("type").getValue();
			System.out.println(type);
			element = element.element("props");
			@SuppressWarnings("unchecked")
	        List<Element> props = element.elements();
			Map<String,Object> config = new HashMap<String, Object>();
			for(Element el : props){
				Attribute attr = el.attribute("key");
				String key = attr.getValue();
				Object value = null;
				attr = el.attribute("value");
				if(null!=attr){
					value = attr.getValue();
				}else if(el.isTextOnly()){
					value = el.getTextTrim();
				}else{
					List<Command> commands = new ArrayList<Command>();
					@SuppressWarnings("unchecked")
			        List<Element> commandSetEl = el.elements();
					for(Element setEl : commandSetEl){
						commands.add(createCommand(setEl));
					}
					value = commands;
				}
				config.put(key, value);
			}
			return CommandFactory.create(type, config);
		}		
		return null;
	}
	
	static ProcessTask create(Element element,Class<? extends ProcessTask> clazz){
		ProcessTask task = instance(clazz);
		Element _el = element.element("taskId");
		task.setTaskId(_el.getText());	
		_el = element.element("status");
		if(null!=_el){
			task.setStatus(TaskStatus.getInstance(Integer.parseInt(_el.getTextTrim())));
		}
		_el = element.element("beforeCommand");
		_el = (Element)element.selectSingleNode("beforeCommand/commandDef");
		task.setBeforeCommand(createCommand(_el));
		_el = (Element)element.selectSingleNode("command/commandDef");
		task.setCommand(createCommand(_el));
		_el = (Element)element.selectSingleNode("afterSuccess/commandDef");
		task.setAfterSuccess(createCommand(_el));
		_el = (Element)element.selectSingleNode("afterError/commandDef");
		task.setAfterError(createCommand(_el));
		_el = (Element)element.selectSingleNode("finalCommand/commandDef");
		task.setFinalCommand(createCommand(_el));
		return task;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args)throws Exception {
		
		SAXReader reader = new SAXReader();  
        InputStream in = XmlTest.class.getClassLoader().getResourceAsStream("taskconfig.xml");  
        Document doc = reader.read(in);  
        Element root = doc.getRootElement();
        @SuppressWarnings("unchecked")
        List<Element> list = root.selectNodes("taskDefinitions/taskDefinition");
        for(Element element : list){
        	create(element, ProcessTask.class);
        }
        //readNode(root,"");

	}

}
