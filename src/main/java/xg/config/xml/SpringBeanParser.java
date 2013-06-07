package xg.config.xml;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class SpringBeanParser implements ApplicationContextAware,XmlValueParser {
	
	ApplicationContext ctx;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		ctx = applicationContext;
	}

	@Override
	public Object parser(String input) {
		if(input.startsWith("bean:")){
			input = input.substring(5);
			return ctx.getBean(input);
		}
		return input;
	}

}
