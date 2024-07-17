package com.sg.jdbctcomplexexample.dao;

import com.sg.jdbctcomplexexample.entity.Employee;
import com.sg.jdbctcomplexexample.entity.Meeting;
import com.sg.jdbctcomplexexample.entity.Room;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MeetingDaoImpl implements MeetingDao {

    @Override
    public List<Meeting> getAllMeetings() {
        return null;
    }

    @Override
    public Meeting getMeetingByid(int id) {
        return null;
    }

    @Override
    public Meeting addMeeting(Meeting meeting) {
        return null;
    }

    @Override
    public void updateMeeting(Meeting meeting) {

    }

    @Override
    public void deleteMeetingById(int id) {

    }

    @Override
    public List<Meeting> getMeetingsForRoom(Room room) {
        return null;
    }

    @Override
    public List<Meeting> getMeetingsForEmployee(Employee employee) {
        return null;
    }
}
