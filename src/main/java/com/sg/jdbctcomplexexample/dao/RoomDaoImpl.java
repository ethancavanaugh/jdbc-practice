package com.sg.jdbctcomplexexample.dao;

import com.sg.jdbctcomplexexample.entity.Room;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class RoomDaoImpl implements RoomDao {
    private final JdbcTemplate jdbc;

    public RoomDaoImpl(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public List<Room> getAllRooms() {
        String sql = "SELECT * FROM room";
        return jdbc.query(sql, new RoomMapper());
    }

    @Override
    public Room getRoomById(int id) {
        try {
            String sql = "SELECT * FROM room WHERE id = ?";
            return jdbc.queryForObject(sql, new RoomMapper(), id);
        } catch (DataAccessException e) {
            return null;
        }
    }

    @Override
    @Transactional
    public Room addRoom(Room room) {
        String sql = "INSERT INTO room(name, description) VALUES (?,?)";
        jdbc.update(sql, room.getName(), room.getDescription());

        @SuppressWarnings("DataFlowIssue")
        int newId = jdbc.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);
        room.setId(newId);
        return room;
    }

    @Override
    public void updateRoom(Room room) {
        String sql = "UPDATE room SET name = ?, description = ? WHERE id = ?";
        jdbc.update(sql, room.getName(), room.getDescription(), room.getId());
    }

    @Override
    @Transactional
    public void deleteRoomById(int id) {
        String deleteEmployeeByRoom = "DELETE me.* FROM meeting_employee me " +
                "INNER JOIN meeting m ON me.meetingId = m.id WHERE m.id = ?";
        jdbc.update(deleteEmployeeByRoom, id);

        String deleteMeetingByRoom = "DELETE FROM meeting WHERE roomId = ?";
        jdbc.update(deleteMeetingByRoom, id);

        String deleteRoom = "DELETE FROM room WHERE id = ?";
        jdbc.update(deleteRoom, id);
    }

    public static final class RoomMapper implements RowMapper<Room> {
        @Override
        public Room mapRow(ResultSet rs, int i) throws SQLException {
            Room rm = new Room();
            rm.setId(rs.getInt("id"));
            rm.setName(rs.getString("name"));
            rm.setDescription(rs.getString("description"));
            return rm;
        }
    }
}
