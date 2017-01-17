package com.crayons_2_0.service.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.crayons_2_0.model.Course;
import com.crayons_2_0.model.graph.Graph;
import com.crayons_2_0.model.graph.Unit;
import com.crayons_2_0.model.graph.Unit.UnitType;

public class UnitDAO {

	@Autowired
    JdbcTemplate jdbcTemplate;

    public void createDbTable() {
        jdbcTemplate.execute("create table if not exists units (title varchar(100), unitType varchar(100))");
    }

    public List<Unit> findAll() {
        String query = "select * from units";
        RowMapper mapper = new RowMapper() {

            public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                
            	String title = rs.getString("title");
                UnitType unitType = createUnityType(rs.getString("unitType"));
                Graph graph = getGraphFromAnyway();

                Unit unit = new Unit(unitType, true, graph);
                return unit;
            }

			
        };
        return jdbcTemplate.query(query, mapper);
    }


	public void save(Course course) {
        String query = "insert into courses (name) values (?)";
        jdbcTemplate.update(query, new Object[]{course.getTitle()});
    }
    
	
	
    private UnitType createUnityType(String unitType) {
    	// TODO!!!!!!!!!!!!!!!!!!!!!!!! (Ergänzen)
		if (unitType.equals(UnitType.START)) {
			return UnitType.START;
		} else if (unitType.equals(UnitType.LEARNING)) {
			return UnitType.LEARNING;
		} else {
			return null;
		}
	}
    
    protected Graph getGraphFromAnyway() {
    	// TODO !!!!!!!!!!!!!!!!!!!!!!!!!
		return null;
	}
	
}