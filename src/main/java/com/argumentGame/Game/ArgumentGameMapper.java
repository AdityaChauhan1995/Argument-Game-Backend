package com.argumentGame.Game;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class ArgumentGameMapper implements RowMapper<RequestBodySaveMap> {
   public RequestBodySaveMap mapRow(ResultSet rs, int rowNum) throws SQLException {
	   RequestBodySaveMap value = new RequestBodySaveMap();
	   value.setNodes(rs.getString("NodesJson"));
	   value.setEdges(rs.getString("EdgesJson"));
      return value;
   }
}