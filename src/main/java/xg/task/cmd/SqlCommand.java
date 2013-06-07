package xg.task.cmd;

import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

public class SqlCommand implements Command {
	
	private String sql;
	
	private JdbcTemplate jdbcTemplate;

	@Override
	public ExecResult exec() {
		ExecResult r = new ExecResult(true);
		try {
			log.info("exec sql:{}",sql);
			jdbcTemplate.execute(sql);
			r.setDetail("exec sql:"+sql+" success");
		} catch (DataAccessException e) {
			e.printStackTrace();
			errorlog.error("exec sql \"{}\" failure:{}",sql,e.getMessage());
			r.setSuccess(false);
			r.setDetail("exec sql \""+sql+"\" failure:"+e.getMessage());
		}
		return r;
	}
	
	

	@Override
	public Command create(Map<String, Object> config) {
		if(null!=config){
			this.sql = (String)config.get("sql");
			//this.jdbcTemplate= (JdbcTemplate)config.get("jdbcTemplate");
		}
		return this;
	}



	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	

}
