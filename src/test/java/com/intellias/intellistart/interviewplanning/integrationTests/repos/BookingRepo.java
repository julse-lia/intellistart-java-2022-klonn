package com.intellias.intellistart.interviewplanning.integrationTests.repos;

import com.intellias.intellistart.interviewplanning.models.CandidateTimeSlot;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepo extends JpaRepository<CandidateTimeSlot, UUID> {
}
