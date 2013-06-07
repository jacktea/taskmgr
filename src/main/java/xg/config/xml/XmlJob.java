package xg.config.xml;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;

import xg.config.JobConfig;
import xg.task.ProcessTask;
import xg.task.TaskStatus;
import xg.task.cmd.Command;
import xg.task.cmd.CommandFactory;
import xg.util.Assert;


@SuppressWarnings("unchecked")
public class XmlJob implements JobConfig{
	
	static final Logger log = LoggerFactory.getLogger(XmlJob.class);
	
	static final Logger errorlog = LoggerFactory.getLogger("task-error");

	Element root;
	
	XmlValueParser parser;
	
	private XmlJob(XmlValueParser parser){
		this.parser = parser;
	}
	
	/**
	 * 创建Job
	 * @param fileLocation	任务配置文件地址
	 * @param parser		xml配置项解析器
	 * @return
	 */
	public static XmlJob createJob(String fileLocation,XmlValueParser parser){	
		File file;
		Assert.notNull(parser);
		try {
			file = ResourceUtils.getFile(fileLocation);
			SAXReader reader = new SAXReader();
			XmlJob job = new XmlJob(parser);
			Document doc;
			try {
				doc = reader.read(file);
				job.root = doc.getRootElement();
				return job;
			} catch (DocumentException e) {
				errorlog.error("解析xml出错",e);
			}
		} catch (FileNotFoundException e1) {
			errorlog.error("文件没找到",e1);
		}
		return null;
	}
	
	public static XmlJob createJob(String fileLocation){	
		return createJob(fileLocation, new DefaultXmlValueParser());
	}

	@Override
	public List<ProcessTask> allTask() {
		List<ProcessTask> ret = new ArrayList<ProcessTask>();
		List<Element> list = root.selectNodes("taskDefinitions/taskDefinition");
		for (Element element : list) {
			ret.add(create(element, ProcessTask.class));
		}
		return ret;
	}

	@Override
	public Map<String, String> taskRelationship() {
		Map<String, String> ret = new HashMap<String, String>();
		List<Element> list = root
				.selectNodes("taskRelationships/taskRelationship");
		for (Element el : list) {
			ret.put(el.element("taskId").getTextTrim(),
					el.element("relationalExpr").getTextTrim());
		}
		return ret;
	}

	<T> T instance(Class<T> clazz) {
		try {
			return clazz.newInstance();
		} catch (Exception e) {
			errorlog.error("实例化失败。",e);
		}
		return null;
	}

	Command createCommand(Element element) {
		if (null != element) {
			String type = element.attribute("type").getValue();
			element = element.element("props");
			List<Element> props = element.elements();
			Map<String, Object> config = new HashMap<String, Object>();
			for (Element el : props) {
				Attribute attr = el.attribute("key");
				String key = attr.getValue();
				Object value = null;
				attr = el.attribute("value");
				if (null != attr) {
					value = parser.parser(attr.getValue());
				} else if (el.isTextOnly()) {
					value = parser.parser(el.getTextTrim());
				} else {
					List<Command> commands = new ArrayList<Command>();
					List<Element> commandSetEl = el.elements();
					for (Element setEl : commandSetEl) {
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

	ProcessTask create(Element element, Class<? extends ProcessTask> clazz) {
		ProcessTask task = instance(clazz);
		Element _el = element.element("taskId");
		task.setTaskId(_el.getText());
		_el = element.element("status");
		if (null != _el) {
			task.setStatus(TaskStatus.getInstance(Integer.parseInt(_el
					.getTextTrim())));
		}
		_el = element.element("beforeCommand");
		_el = (Element) element.selectSingleNode("beforeCommand/commandDef");
		task.setBeforeCommand(createCommand(_el));
		_el = (Element) element.selectSingleNode("command/commandDef");
		task.setCommand(createCommand(_el));
		_el = (Element) element.selectSingleNode("afterSuccess/commandDef");
		task.setAfterSuccess(createCommand(_el));
		_el = (Element) element.selectSingleNode("afterError/commandDef");
		task.setAfterError(createCommand(_el));
		_el = (Element) element.selectSingleNode("finalCommand/commandDef");
		task.setFinalCommand(createCommand(_el));
		return task;
	}
	
	static class DefaultXmlValueParser implements XmlValueParser{

		@Override
		public Object parser(String input) {
			return input;
		}
		
	}

}
