package com.sg.jdbctcomplexexample.dao;

import com.sg.jdbctcomplexexample.entity.Employee;
import com.sg.jdbctcomplexexample.entity.Meeting;
import com.sg.jdbctcomplexexample.entity.Room;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

@Repository
public class MeetingDaoImpl implements MeetingDao {
    private JdbcTemplate jdbc;

    public MeetingDaoImpl(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public List<Meeting> getAllMeetings() {
        String sql = "SELECT * FROM meeting";
        List<Meeting> meetings = jdbc.query(sql, new MeetingMapper());

        addMeetingRoomAndEmployees(meetings);
        return meetings;
    }

    @Override
    public Meeting getMeetingByid(int id) {
        try {
            String sql = "SELECT * FROM meeting WHERE id = ?";
            Meeting meeting = jdbc.queryForObject(sql, new MeetingMapper(), id);
            assert meeting != null;
            meeting.setRoom(getRoomForMeeting(meeting));
            meeting.setAttendees(getEmployeesForMeeting(meeting));
            return meeting;
        }
        catch (DataAccessException e) {
            return null;
        }
    }

    @Override
    @Transactional
    public Meeting addMeeting(Meeting meeting) {
        String sql = "INSERT INTO meeting(name, time, roomId) VALUES(?,?,?)";
        jdbc.update(sql, meeting.getName(), Timestamp.valueOf(meeting.getTime()), meeting.getRoom().getId());

        @SuppressWarnings("DataFlowIssue")
        int newId = jdbc.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);
        meeting.setId(newId);

        insertMeetingEmployees(meeting);

        return meeting;
    }

    @Override
    public void updateMeeting(Meeting meeting) {
        String sql = "UPDATE meeting SET name = ?, time = ?, roomId = ? WHERE id = ?";
        jdbc.update(sql, meeting.getName(), Timestamp.valueOf(meeting.getTime()),
                    meeting.getRoom().getId(), meeting.getId());

        //Delete and re-enter meeting-employee relationships
        sql = "DELETE FROM meeting_employee WHERE meetingId = ?";
        jdbc.update(sql, meeting.getId());
        insertMeetingEmployees(meeting);
    }

    @Override
    public void deleteMeetingById(int id) {
        jdbc.update("DELETE FROM meeting_employee WHERE meetingId = ?", id);
        jdbc.update("DELETE FROM meeting WHERE id = ?", id);
    }

    @Override
    public List<Meeting> getMeetingsForRoom(Room room) {
        String sql = "SELECT * FROM meeting WHERE roomId = ?";
        List<Meeting> meetings = jdbc.query(sql, new MeetingMapper(), room.getId());

        addMeetingRoomAndEmployees(meetings);
        return meetings;
    }

    @Override
    public List<Meeting> getMeetingsForEmployee(Employee employee) {
        String sql = "SELECT m.* FROM meeting m INNER JOIN meeting_employee me ON m.id = me.meetingId " +
                     "WHERE me.employeeId = ?";
        List<Meeting> meetings = jdbc.query(sql, new MeetingMapper(), employee.getId());

        addMeetingRoomAndEmployees(meetings);
        return meetings;
    }

    private Room getRoomForMeeting(Meeting m) {
        String sql = "SELECT r.* FROM room r INNER JOIN meeting m ON m.roomId = r.id WHERE m.id = ?";
        return jdbc.queryForObject(sql, new RoomDaoImpl.RoomMapper(), m.getId());
    }

    private List<Employee> getEmployeesForMeeting(Meeting m) {
        String sql = "SELECT e.* FROM employee e INNER JOIN meeting_employee me ON e.id = me.employeeID WHERE me.meetingId = ?";
        return jdbc.query(sql, new EmployeeDaoImpl.EmployeeMapper(), m.getId());
    }

    private void addMeetingRoomAndEmployees(List<Meeting> meetings) {
        for (Meeting m : meetings) {
            m.setAttendees(getEmployeesForMeeting(m));
            m.setRoom(getRoomForMeeting(m));
        }
    }

    private void insertMeetingEmployees(Meeting m) {
        String sql = "INSERT INTO meeting_employee (meetingId, employeeId) VALUES (?,?)";
        for (Employee e : m.getAttendees()) {
            jdbc.update(sql, m.getId(), e.getId());
        }
    }

    public static final class MeetingMapper implements RowMapper<Meeting> {
        @Override
        public Meeting mapRow(ResultSet rs, int i) throws SQLException {
            Meeting meet = new Meeting();
            meet.setId(rs.getInt("id"));
            meet.setName(rs.getString("name"));
            meet.setTime(rs.getTimestamp("time").toLocalDateTime());
            return meet;
        }
    }
}
