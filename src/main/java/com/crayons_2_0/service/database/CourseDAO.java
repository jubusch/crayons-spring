package com.crayons_2_0.service.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.crayons_2_0.model.Course;
import com.crayons_2_0.model.CrayonsUser;
import com.vaadin.spring.annotation.SpringComponent;
@Component
public class CourseDAO {


	@Autowired
    JdbcTemplate jdbcTemplate;
	
	@Autowired
	UserService userService;

    public void createDbTable() {
        jdbcTemplate.execute("create table if not exists courses (title varchar(100), description varchar(100), author varchar(100))");
    }

    public List<Course> findAll() {
        String query = "select * from courses";
        RowMapper mapper = new RowMapper() {

            public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                
            	String title = rs.getString("title");
                String description = rs.getString("description");
                //String authorMail = rs.getString("author");
                CrayonsUser author = userService.findByEMail(rs.getString("author"));

                Course course = new Course(title, description, author);
                return course;
            }
        };
        return jdbcTemplate.query(query, mapper);
        
    }

    public void insert(Course course) {
        
    	String title = course.getTitle();
    	String description = course.getDescription();
    	String author = course.getAuthor().geteMail();
        
        jdbcTemplate.update("insert into courses (title, description, author) VALUES (?, ?, ?)", title, description, author);
    }
    
    public void update(Course course) {
    	
    	String title = course.getTitle();
    	String description = course.getDescription();
    	String author = course.getAuthor().geteMail();
    	
        jdbcTemplate.update("UPDATE courses SET description=?, author=? WHERE title=? ", description, author, title);
    }

    
    public void remove(Course course) {
    	String deleteStatement = "DELETE FROM courses WHERE title=?";
    	try {
			jdbcTemplate.update(deleteStatement, course.getTitle());
		} catch (RuntimeException e) {
			//throw new CourseTitleNotFoundException("Course with Title:" + course.getTitle() + "doesnt exists!");
			throw new UsernameNotFoundException("Course with Title:" + course.getTitle() + "doesnt exists!");
		}
    }
	
}
