package com.intellias.intellistart.interviewplanning.integrationTests;

import com.intellias.intellistart.interviewplanning.models.Booking;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BookingRepo extends JpaRepository<Booking, UUID> {

  List<Booking> findBookingsByCandidateTimeSlotId(UUID candidateTimeSlotId);

  List<Booking> findBookingsByInterviewerTimeSlotId(UUID interviewerTimeSlotId);

  @Query(value = "select * from bookings b where b.interviewer_time_slot_id = ?1",
      nativeQuery = true)
  List<Booking> getBookingsByInterviewerSlotId(UUID candidateSlotId);
}
