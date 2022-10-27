package com.intellias.intellistart.interviewplanning.controllers;

import com.intellias.intellistart.interviewplanning.models.InterviewerBookingLimit;
import com.intellias.intellistart.interviewplanning.models.InterviewerTimeSlot;
import com.intellias.intellistart.interviewplanning.services.InterviewerService;
import java.util.List;
import java.util.UUID;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Interviewer controller.
 */
@RestController
public class InterviewerController {
  @Autowired
  private InterviewerService interviewerService;

  @GetMapping("/interviewers/booking-limits/{interviewerId}")
  public List<InterviewerBookingLimit> getLimits(
      @PathVariable("interviewerId") UUID interviewerId) {
    return interviewerService.getBookingLimitsByInterviewerId(interviewerId);
  }

  @PostMapping("/interviewers/booking-limits")
  @ResponseStatus(code = HttpStatus.CREATED)
  public InterviewerBookingLimit createInterviewerBookingLimit(
      @Valid @RequestBody InterviewerBookingLimit interviewerBookingLimit) {
    return interviewerService.setNextWeekInterviewerBookingLimit(interviewerBookingLimit);
  }

  @PostMapping(path = "/interviewers/{interviewer_id}/slots")
  @ResponseStatus(code = HttpStatus.CREATED)
  public InterviewerTimeSlot createInterviewerTimeSlot(
      @Valid @PathVariable("interviewer_id") UUID interviewerId,
      @RequestBody InterviewerTimeSlot timeSlot) {
    return interviewerService.createSlot(timeSlot, interviewerId);
  }

  @PostMapping("/interviewers/{interviewer_id}/slots/{slot_id}")
  public InterviewerTimeSlot updateInterviewerTimeSlot(
      @Valid @PathVariable("interviewer_id") UUID interviewerId,
      @Valid @PathVariable("slot_id") UUID slotId,
      @RequestBody InterviewerTimeSlot timeSlot) {
    return interviewerService.updateSlot(timeSlot, interviewerId, slotId);
  }
}
