package com.intellias.intellistart.interviewplanning.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.intellias.intellistart.interviewplanning.exceptions.InvalidInterviewerPeriodException;
import com.intellias.intellistart.interviewplanning.exceptions.ResourceNotFoundException;
import com.intellias.intellistart.interviewplanning.models.Booking;
import com.intellias.intellistart.interviewplanning.models.InterviewerBookingLimit;
import com.intellias.intellistart.interviewplanning.models.InterviewerTimeSlot;
import com.intellias.intellistart.interviewplanning.models.Period;
import com.intellias.intellistart.interviewplanning.models.User;
import com.intellias.intellistart.interviewplanning.models.UserRole;
import com.intellias.intellistart.interviewplanning.repositories.BookingRepository;
import com.intellias.intellistart.interviewplanning.repositories.InterviewerBookingLimitRepository;
import com.intellias.intellistart.interviewplanning.repositories.InterviewerTimeSlotRepository;
import com.intellias.intellistart.interviewplanning.repositories.PeriodRepository;
import com.intellias.intellistart.interviewplanning.repositories.UserRepository;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InterviewerServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private PeriodRepository periodRepository;

  @Mock
  private InterviewerTimeSlotRepository interviewerTimeSlotRepository;

  @Mock
  private InterviewerBookingLimitRepository interviewerBookingLimitRepository;

  @Mock
  private BookingRepository bookingRepository;

  @InjectMocks
  private InterviewerService interviewerService;

  private User interviewer;

  private Period period;

  @BeforeEach
  public void setup() {
    interviewer = User.builder().
        id(UUID.fromString("123e4567-e89b-42d3-a456-556642440000"))
        .email("interviewer@gmail.com")
        .role(UserRole.INTERVIEWER)
        .build();

    period = Period.builder()
        .id(UUID.fromString("123e4567-e89b-42d3-a456-556642440000"))
        .from(LocalDateTime.of(LocalDateTime.now().getYear(), LocalDateTime.now().getMonth(),
            LocalDateTime.now().plusWeeks(1).getDayOfMonth(), 15, 30))
        .to(LocalDateTime.of(LocalDateTime.now().getYear(), LocalDateTime.now().getMonth(),
            LocalDateTime.now().plusWeeks(1).getDayOfMonth(), 17, 0))
        .build();
  }

  @Test
  void givenInterviewerTimeSlot_whenCreateInterviewerSlot_thenReturnInterviewerTimeSlot(){
    given(periodRepository.findById(period.getId())).willReturn(Optional.of(period));

    given(userRepository.findById(interviewer.getId())).willReturn(Optional.of(interviewer));

    InterviewerTimeSlot interviewerTimeSlot = InterviewerTimeSlot.builder()
        .id(UUID.fromString("123e4567-e89b-42d3-a456-556642440000"))
        .periodId(period.getId())
        .interviewerId(interviewer.getId())
        .dayOfWeek(DayOfWeek.MONDAY)
        .build();

    given(interviewerTimeSlotRepository.save(interviewerTimeSlot)).willReturn(interviewerTimeSlot);

    InterviewerTimeSlot savedSlot = interviewerService.createSlot(interviewerTimeSlot);

    System.out.println(savedSlot);

    assertThat(savedSlot).isNotNull();

  }

  @Test
  void givenNonExistPeriodIdAndInterviewerId_whenCreateInterviewerSlot_thenThrowsException() {
    InterviewerTimeSlot interviewerTimeSlot = InterviewerTimeSlot.builder()
        .id(UUID.fromString("123e4567-e89b-42d3-a456-556642440000"))
        .periodId(period.getId())
        .interviewerId(interviewer.getId())
        .build();

    assertThrows(ResourceNotFoundException.class,
        () -> interviewerService.createSlot(interviewerTimeSlot));

    verify(interviewerTimeSlotRepository, never()).save(any(InterviewerTimeSlot.class));
  }

  @Test
  void givenEndPeriodTimeBiggerThanStartPeriodTime_whenCreateInterviewerSlot_thenThrowsException() {
    Period period = Period.builder()
        .id(UUID.fromString("123e4567-e89b-42d3-a456-556642440000"))
        .from(LocalDateTime.of(LocalDateTime.now().getYear(), LocalDateTime.now().getMonth(),
            LocalDateTime.now().plusWeeks(1).getDayOfMonth(), 16, 30))
        .to(LocalDateTime.of(LocalDateTime.now().getYear(), LocalDateTime.now().getMonth(),
            LocalDateTime.now().plusWeeks(1).getDayOfMonth(), 15, 0))
        .build();

    given(periodRepository.findById(period.getId())).willReturn(Optional.of(period));

    given(userRepository.findById(interviewer.getId())).willReturn(Optional.of(interviewer));

    InterviewerTimeSlot interviewerTimeSlot = InterviewerTimeSlot.builder()
        .id(UUID.fromString("123e4567-e89b-42d3-a456-556642440000"))
        .periodId(period.getId())
        .interviewerId(interviewer.getId())
        .dayOfWeek(DayOfWeek.MONDAY)
        .build();

    assertThrows(InvalidInterviewerPeriodException.class,
        () -> interviewerService.createSlot(interviewerTimeSlot));

    verify(interviewerTimeSlotRepository, never()).save(any(InterviewerTimeSlot.class));
  }


  @Test
  void givenOutdatedPeriod_whenCreateInterviewerSlot_thenThrowsException() {
    Period period = Period.builder()
        .id(UUID.fromString("123e4567-e89b-42d3-a456-556642440000"))
        .from(LocalDateTime.of(2017, 11, 13, 15, 30))
        .to(LocalDateTime.of(2017, 11, 13, 17, 0))
        .build();

    given(periodRepository.findById(period.getId())).willReturn(Optional.of(period));

    given(userRepository.findById(interviewer.getId())).willReturn(Optional.of(interviewer));

    InterviewerTimeSlot interviewerTimeSlot = InterviewerTimeSlot.builder()
        .id(UUID.fromString("123e4567-e89b-42d3-a456-556642440000"))
        .periodId(period.getId())
        .interviewerId(interviewer.getId())
        .dayOfWeek(DayOfWeek.MONDAY)
        .build();

    assertThrows(InvalidInterviewerPeriodException.class,
        () -> interviewerService.createSlot(interviewerTimeSlot));

    verify(interviewerTimeSlotRepository, never()).save(any(InterviewerTimeSlot.class));
  }


  @Test
  void givenUnroundedPeriod_whenCreateInterviewerSlot_thenThrowsException() {
    Period period = Period.builder()
        .id(UUID.fromString("123e4567-e89b-42d3-a456-556642440000"))
        .from(LocalDateTime.of(LocalDateTime.now().getYear(), LocalDateTime.now().getMonth(),
            LocalDateTime.now().plusWeeks(1).getDayOfMonth(),15, 27))
        .to(LocalDateTime.of(LocalDateTime.now().getYear(), LocalDateTime.now().getMonth(),
            LocalDateTime.now().plusWeeks(1).getDayOfMonth(),17, 0)).build();

    given(periodRepository.findById(period.getId())).willReturn(Optional.of(period));

    given(userRepository.findById(interviewer.getId())).willReturn(Optional.of(interviewer));

    InterviewerTimeSlot interviewerTimeSlot = InterviewerTimeSlot.builder()
        .id(UUID.fromString("123e4567-e89b-42d3-a456-556642440000"))
        .periodId(period.getId())
        .interviewerId(interviewer.getId())
        .dayOfWeek(DayOfWeek.MONDAY)
        .build();

    assertThrows(InvalidInterviewerPeriodException.class,
        () -> interviewerService.createSlot(interviewerTimeSlot));

    verify(interviewerTimeSlotRepository, never()).save(any(InterviewerTimeSlot.class));
  }

  @Test
  void givenSmallPeriod_whenCreatInterviewerSlot_thenThrowsException() {
    Period period = Period.builder()
        .id(UUID.fromString("123e4567-e89b-42d3-a456-556642440000"))
        .from(LocalDateTime.of(LocalDateTime.now().getYear(), LocalDateTime.now().getMonth(),
            LocalDateTime.now().plusWeeks(1).getDayOfMonth(),15, 0))
        .to(LocalDateTime.of(LocalDateTime.now().getYear(), LocalDateTime.now().getMonth(),
            LocalDateTime.now().plusWeeks(1).getDayOfMonth(), 16, 0))
        .build();

    given(periodRepository.findById(period.getId())).willReturn(Optional.of(period));

    given(userRepository.findById(interviewer.getId())).willReturn(Optional.of(interviewer));

    InterviewerTimeSlot interviewerTimeSlot = InterviewerTimeSlot.builder()
        .id(UUID.fromString("123e4567-e89b-42d3-a456-556642440000"))
        .periodId(period.getId())
        .interviewerId(interviewer.getId())
        .dayOfWeek(DayOfWeek.MONDAY)
        .build();

    assertThrows(InvalidInterviewerPeriodException.class,
        () -> interviewerService.createSlot(interviewerTimeSlot));

    verify(interviewerTimeSlotRepository, never()).save(any(InterviewerTimeSlot.class));
  }

  @Test
  void givenPeriodBefore8AM_whenCreatInterviewerSlot_thenThrowsException() {
    Period period = Period.builder()
        .id(UUID.fromString("123e4567-e89b-42d3-a456-556642440000"))
        .from(LocalDateTime.of(LocalDateTime.now().getYear(), LocalDateTime.now().getMonth(),
            LocalDateTime.now().plusWeeks(1).getDayOfMonth(),7, 0))
        .to(LocalDateTime.of(LocalDateTime.now().getYear(), LocalDateTime.now().getMonth(),
            LocalDateTime.now().plusWeeks(1).getDayOfMonth(), 16, 0))
        .build();

    given(periodRepository.findById(period.getId())).willReturn(Optional.of(period));

    given(userRepository.findById(interviewer.getId())).willReturn(Optional.of(interviewer));

    InterviewerTimeSlot interviewerTimeSlot = InterviewerTimeSlot.builder()
        .id(UUID.fromString("123e4567-e89b-42d3-a456-556642440000"))
        .periodId(period.getId())
        .interviewerId(interviewer.getId())
        .dayOfWeek(DayOfWeek.MONDAY)
        .build();

    assertThrows(InvalidInterviewerPeriodException.class,
        () -> interviewerService.createSlot(interviewerTimeSlot));

    verify(interviewerTimeSlotRepository, never()).save(any(InterviewerTimeSlot.class));
  }

  @Test
  void givenPeriodAfter10PM_whenCreatInterviewerSlot_thenThrowsException() {
    Period period = Period.builder()
        .id(UUID.fromString("123e4567-e89b-42d3-a456-556642440000"))
        .from(LocalDateTime.of(LocalDateTime.now().getYear(), LocalDateTime.now().getMonth(),
            LocalDateTime.now().plusWeeks(1).getDayOfMonth(),10, 0))
        .to(LocalDateTime.of(LocalDateTime.now().getYear(), LocalDateTime.now().getMonth(),
            LocalDateTime.now().plusWeeks(1).getDayOfMonth(), 23, 0))
        .build();

    given(periodRepository.findById(period.getId())).willReturn(Optional.of(period));

    given(userRepository.findById(interviewer.getId())).willReturn(Optional.of(interviewer));

    InterviewerTimeSlot interviewerTimeSlot = InterviewerTimeSlot.builder()
        .id(UUID.fromString("123e4567-e89b-42d3-a456-556642440000"))
        .periodId(period.getId())
        .interviewerId(interviewer.getId())
        .dayOfWeek(DayOfWeek.MONDAY)
        .build();

    assertThrows(InvalidInterviewerPeriodException.class,
        () -> interviewerService.createSlot(interviewerTimeSlot));

    verify(interviewerTimeSlotRepository, never()).save(any(InterviewerTimeSlot.class));
  }

  @Test
  void givenPeriodsStartsAtDifferentDays_whenCreatInterviewerSlot_thenThrowsException() {
    Period period = Period.builder()
        .id(UUID.fromString("123e4567-e89b-42d3-a456-556642440000"))
        .from(LocalDateTime.of(LocalDateTime.now().getYear(), LocalDateTime.now().getMonth(),
            LocalDateTime.now().plusWeeks(1).getDayOfMonth(),10, 0))
        .to(LocalDateTime.of(LocalDateTime.now().getYear(), LocalDateTime.now().getMonth(),
            LocalDateTime.now().plusWeeks(2).getDayOfMonth(), 17, 0))
        .build();

    given(periodRepository.findById(period.getId())).willReturn(Optional.of(period));

    given(userRepository.findById(interviewer.getId())).willReturn(Optional.of(interviewer));

    InterviewerTimeSlot interviewerTimeSlot = InterviewerTimeSlot.builder()
        .id(UUID.fromString("123e4567-e89b-42d3-a456-556642440000"))
        .periodId(period.getId())
        .interviewerId(interviewer.getId())
        .dayOfWeek(DayOfWeek.MONDAY)
        .build();

    assertThrows(InvalidInterviewerPeriodException.class,
        () -> interviewerService.createSlot(interviewerTimeSlot));

    verify(interviewerTimeSlotRepository, never()).save(any(InterviewerTimeSlot.class));
  }

  @Test
  void givenPeriodsStartsAtWeekends_whenCreatInterviewerSlot_thenThrowsException() {
    Period period = Period.builder()
        .id(UUID.fromString("123e4567-e89b-42d3-a456-556642440000"))
        .from(LocalDateTime.of(LocalDateTime.now().getYear(), LocalDateTime.now().getMonth(),
            LocalDateTime.now().plusWeeks(1).getDayOfMonth(),10, 0))
        .to(LocalDateTime.of(LocalDateTime.now().getYear(), LocalDateTime.now().getMonth(),
            LocalDateTime.now().plusWeeks(2).getDayOfMonth(), 17, 0))
        .build();

    given(periodRepository.findById(period.getId())).willReturn(Optional.of(period));

    given(userRepository.findById(interviewer.getId())).willReturn(Optional.of(interviewer));

    InterviewerTimeSlot interviewerTimeSlot = InterviewerTimeSlot.builder()
        .id(UUID.fromString("123e4567-e89b-42d3-a456-556642440000"))
        .periodId(period.getId())
        .interviewerId(interviewer.getId())
        .dayOfWeek(DayOfWeek.SUNDAY)
        .build();

    assertThrows(InvalidInterviewerPeriodException.class,
        () -> interviewerService.createSlot(interviewerTimeSlot));

    verify(interviewerTimeSlotRepository, never()).save(any(InterviewerTimeSlot.class));
  }

  @Test
  void givenInterviewerSlotObject_whenUpdateInterviewerSlot_thenReturnUpdatedInterviewerSlot() {

    InterviewerTimeSlot interviewerTimeSlot = InterviewerTimeSlot.builder()
        .id(UUID.fromString("123e4567-e89b-42d3-a456-556642440002"))
        .periodId(period.getId())
        .interviewerId(interviewer.getId())
        .dayOfWeek(DayOfWeek.MONDAY)
        .build();

    given(interviewerTimeSlotRepository.save(interviewerTimeSlot)).willReturn(interviewerTimeSlot);

    given(interviewerTimeSlotRepository.findById(interviewerTimeSlot.getId())).willReturn(
        Optional.of(interviewerTimeSlot));

    Period newPeriod = Period.builder()
        .id(UUID.fromString("123e4567-e89b-42d3-a456-556642440002"))
        .from(LocalDateTime.of(LocalDateTime.now().getYear(), LocalDateTime.now().getMonth(),
            LocalDateTime.now().plusWeeks(1).getDayOfMonth(), 14, 0))
        .to(LocalDateTime.of(LocalDateTime.now().getYear(), LocalDateTime.now().getMonth(),
            LocalDateTime.now().plusWeeks(1).getDayOfMonth(), 16, 0))
        .build();

    interviewerTimeSlot.setPeriodId(newPeriod.getId());

    InterviewerTimeSlot updatedSlot = interviewerService.updateSlot(interviewerTimeSlot);

    assertThat(updatedSlot.getPeriodId()).isEqualTo(UUID.fromString("123e4567-e89b-42d3-a456-556642440002"));
  }

  @Test
  void givenInterviewerBookingLimit_whenSetMaximumBookingsForNextWeek_thenReturnInterviewerBookingLimit(){
    InterviewerBookingLimit interviewerBookingLimit = InterviewerBookingLimit.builder()
        .id(UUID.fromString("123e4567-e89b-42d3-a456-556642440000"))
        .interviewerId(interviewer.getId())
        .weekBookingLimit(3)
        .currentBookingCount(0)
        .build();

    given(userRepository.findById(interviewer.getId())).willReturn(Optional.of(interviewer));
    given(interviewerBookingLimitRepository.save(interviewerBookingLimit)).willReturn(interviewerBookingLimit);

    InterviewerBookingLimit savedInterviewerBookingLimit = interviewerService.setMaximumBookingsForNextWeek(interviewerBookingLimit);


    assertThat(savedInterviewerBookingLimit.getWeekBookingLimit()).isEqualTo(3);
    assertThat(savedInterviewerBookingLimit.getInterviewerId()).isEqualTo(UUID.fromString("123e4567-e89b-42d3-a456-556642440000"));
  }

  //cant get access to fields, gonna fix this in future commits
//  @Test
//  void givenInterviewerIdAndIsForCurrentWeekTrue_whenGetWeekTimeSlotsByInterviewerId_thenReturnInterviewerTimeSlotList() {
//
//    User interviewer = User.builder().
//        id(UUID.fromString("123e4567-e89b-42d3-a456-556642440000"))
//        .email("candidate@gmail.com")
//        .role(UserRole.INTERVIEWER)
//        .build();
//
//    InterviewerTimeSlot interviewerTimeSlot = InterviewerTimeSlot.builder()
//        .id(UUID.fromString("123e4567-e89b-42d3-a456-556642440000"))
//        .periodId(period.getId())
//        .interviewerId(interviewer.getId())
//        .dayOfWeek(DayOfWeek.MONDAY)
//        .build();
//
//    InterviewerTimeSlot interviewerTimeSlot1 = InterviewerTimeSlot.builder()
//        .id(UUID.fromString("123e4567-e89b-42d3-a456-556642440002"))
//        .periodId(period.getId())
//        .interviewerId(interviewer.getId())
//        .dayOfWeek(DayOfWeek.WEDNESDAY)
//        .build();
//
//    List<InterviewerTimeSlot> interviewerTimeSlots = List.of(interviewerTimeSlot, interviewerTimeSlot1);
//
//    given(userRepository.findById(interviewer.getId())).willReturn(Optional.of(interviewer));
//
//    given(interviewerTimeSlotRepository.findAll()).willReturn(interviewerTimeSlots);
//
//    for (InterviewerTimeSlot slot : interviewerTimeSlots){
//      given(periodRepository.findById(slot.getPeriodId())).willReturn(Optional.of(period));
//    }
//
//    List<InterviewerTimeSlot> slotList = interviewerService.getWeekTimeSlotsByInterviewerId(
//        interviewer.getId(), true);
//
//    assertThat(slotList).isNotNull();
//    assertThat(slotList).hasSize(2);
//  }
//
//
//  @Test
//  void givenInterviewerIdAndIsForCurrentWeekFalse_whenGetWeekTimeSlotsByInterviewerId_thenReturnInterviewerTimeSlotList() {
//
//    User interviewer = User.builder().
//        id(UUID.fromString("123e4567-e89b-42d3-a456-556642440000"))
//        .email("candidate@gmail.com")
//        .role(UserRole.INTERVIEWER)
//        .build();
//
//    InterviewerTimeSlot interviewerTimeSlot = InterviewerTimeSlot.builder()
//        .id(UUID.fromString("123e4567-e89b-42d3-a456-556642440000"))
//        .periodId(UUID.fromString("123e4567-e89b-42d3-a456-556642440000"))
//        .interviewerId(interviewer.getId())
//        .dayOfWeek(DayOfWeek.MONDAY)
//        .build();
//
//    InterviewerTimeSlot interviewerTimeSlot1 = InterviewerTimeSlot.builder()
//        .id(UUID.fromString("123e4567-e89b-42d3-a456-556642440000"))
//        .periodId(UUID.fromString("123e4567-e89b-42d3-a456-556642440000"))
//        .interviewerId(interviewer.getId())
//        .dayOfWeek(DayOfWeek.WEDNESDAY)
//        .build();
//
//    List<InterviewerTimeSlot> interviewerTimeSlots = List.of(interviewerTimeSlot, interviewerTimeSlot1);
//
//    given(userRepository.findById(interviewer.getId())).willReturn(Optional.of(interviewer));
//
//    given(interviewerTimeSlotRepository.findAll()).willReturn(interviewerTimeSlots);
//
//    List<InterviewerTimeSlot> slotList = interviewerService.getWeekTimeSlotsByInterviewerId(
//        UUID.fromString("123e4567-e89b-42d3-a456-556642440000"), false);
//
//    assertThat(slotList).isNotNull();
//    assertThat(slotList).hasSize(2);
//  }


  @Test
  void givenInterviewerTimeSlotId_whenGetBookingByInterviewerSlotId_thenReturnBookingList() {
    InterviewerTimeSlot interviewerTimeSlot = InterviewerTimeSlot.builder()
        .id(UUID.fromString("123e4567-e89b-42d3-a456-556642440000"))
        .periodId(UUID.fromString("123e4567-e89b-42d3-a456-556642440000"))
        .interviewerId(interviewer.getId())
        .build();
    interviewerTimeSlotRepository.save(interviewerTimeSlot);

    given(interviewerTimeSlotRepository.findById(interviewerTimeSlot.getId())).willReturn(
        Optional.of(interviewerTimeSlot));

    Booking booking1 = Booking.builder()
        .id(UUID.fromString("123e4567-e89b-42d3-a456-556642440000"))
        .candidateTimeSlotId(UUID.fromString("123e4567-e89b-42d3-a456-556642440010"))
        .interviewerTimeSlotId(interviewerTimeSlot.getId())
        .periodId(UUID.fromString("123e4567-e89b-42d3-a456-556642440005"))
        .build();

    Booking booking2 = Booking.builder()
        .id(UUID.fromString("123e4567-e89b-42d3-a456-556642440001"))
        .candidateTimeSlotId(UUID.fromString("123e4567-e89b-42d3-a456-556642440002"))
        .interviewerTimeSlotId(interviewerTimeSlot.getId())
        .periodId(UUID.fromString("123e4567-e89b-42d3-a456-556642440001"))
        .build();

    given(interviewerService.getBookingByInterviewerSlotId(interviewerTimeSlot.getId())).willReturn(
        List.of(booking1, booking2));

    List<Booking> bookings =
        interviewerService.getBookingByInterviewerSlotId(interviewerTimeSlot.getId());
    assertThat(bookings).isNotNull();
    assertThat(bookings).hasSize(2);
  }

}