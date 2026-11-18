package ua.edu.nupp.kpp.server;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

class ServiceRequestServiceTest {

    @Test
    void isAllowedTransition_newToDiagnosed_returnsTrue() {
        assertTrue(ServiceRequestService.isAllowedTransition(
                ServiceRequestState.NEW, ServiceRequestState.DIAGNOSED));
    }

    @Test
    void isAllowedTransition_diagnosedToInProgress_returnsTrue() {
        assertTrue(ServiceRequestService.isAllowedTransition(
                ServiceRequestState.DIAGNOSED, ServiceRequestState.IN_PROGRESS));
    }

    @Test
    void isAllowedTransition_inProgressToCompleted_returnsTrue() {
        assertTrue(ServiceRequestService.isAllowedTransition(
                ServiceRequestState.IN_PROGRESS, ServiceRequestState.COMPLETED));
    }

    @Test
    void isAllowedTransition_newToInProgress_returnsFalse() {
        assertFalse(ServiceRequestService.isAllowedTransition(
                ServiceRequestState.NEW, ServiceRequestState.IN_PROGRESS));
    }

    @Test
    void isAllowedTransition_newToCompleted_returnsFalse() {
        assertFalse(ServiceRequestService.isAllowedTransition(
                ServiceRequestState.NEW, ServiceRequestState.COMPLETED));
    }

    @Test
    void isAllowedTransition_completedToNew_returnsFalse() {
        assertFalse(ServiceRequestService.isAllowedTransition(
                ServiceRequestState.COMPLETED, ServiceRequestState.NEW));
    }

    @Test
    void isAllowedTransition_completedToDiagnosed_returnsFalse() {
        assertFalse(ServiceRequestService.isAllowedTransition(
                ServiceRequestState.COMPLETED, ServiceRequestState.DIAGNOSED));
    }

    @Test
    void isAllowedTransition_inProgressToDiagnosed_returnsFalse() {
        assertFalse(ServiceRequestService.isAllowedTransition(
                ServiceRequestState.IN_PROGRESS, ServiceRequestState.DIAGNOSED));
    }

    @Test
    void isAllowedTransition_sameState_returnsFalse() {
        assertFalse(ServiceRequestService.isAllowedTransition(
                ServiceRequestState.NEW, ServiceRequestState.NEW));
    }

    @Test
    void add_nullRequest_throwsBeforeDb() {
        ServiceRequestService service = new ServiceRequestService();
        assertThrows(IllegalArgumentException.class,
                () -> service.add(null));
    }

    @Test
    void add_blankDescription_throwsBeforeDb() {
        ServiceRequestService service = new ServiceRequestService();
        ServiceRequest r = new ServiceRequest(
                null, 1L, "", 50.0, 60.0, 70.0, 0.0, ServiceRequestState.NEW, LocalDate.now());
        assertThrows(IllegalArgumentException.class,
                () -> service.add(r));
    }

    @Test
    void add_f1OutOfRange_throwsBeforeDb() {
        ServiceRequestService service = new ServiceRequestService();
        ServiceRequest r = new ServiceRequest(
                null, 1L, "Опис", 101.0, 60.0, 70.0, 0.0, ServiceRequestState.NEW, LocalDate.now());
        assertThrows(IllegalArgumentException.class,
                () -> service.add(r));
    }

    @Test
    void add_f2OutOfRange_throwsBeforeDb() {
        ServiceRequestService service = new ServiceRequestService();
        ServiceRequest r = new ServiceRequest(
                null, 1L, "Опис", 50.0, -5.0, 70.0, 0.0, ServiceRequestState.NEW, LocalDate.now());
        assertThrows(IllegalArgumentException.class,
                () -> service.add(r));
    }

    @Test
    void add_f3OutOfRange_throwsBeforeDb() {
        ServiceRequestService service = new ServiceRequestService();
        ServiceRequest r = new ServiceRequest(
                null, 1L, "Опис", 50.0, 60.0, 150.0, 0.0, ServiceRequestState.NEW, LocalDate.now());
        assertThrows(IllegalArgumentException.class,
                () -> service.add(r));
    }

    @Test
    void add_nullState_throwsBeforeDb() {
        ServiceRequestService service = new ServiceRequestService();
        ServiceRequest r = new ServiceRequest(
                null, 1L, "Опис", 50.0, 60.0, 70.0, 0.0, null, LocalDate.now());
        assertThrows(IllegalArgumentException.class,
                () -> service.add(r));
    }

    @Test
    void add_zeroEquipmentId_throwsBeforeDb() {
        ServiceRequestService service = new ServiceRequestService();
        ServiceRequest r = new ServiceRequest(
                null, 0L, "Опис", 50.0, 60.0, 70.0, 0.0, ServiceRequestState.NEW, LocalDate.now());
        assertThrows(IllegalArgumentException.class,
                () -> service.add(r));
    }

    @Test
    void add_negativeEquipmentId_throwsBeforeDb() {
        ServiceRequestService service = new ServiceRequestService();
        ServiceRequest r = new ServiceRequest(
                null, -1L, "Опис", 50.0, 60.0, 70.0, 0.0, ServiceRequestState.NEW, LocalDate.now());
        assertThrows(IllegalArgumentException.class,
                () -> service.add(r));
    }

    @Test
    void update_nullRequest_throwsBeforeDb() {
        ServiceRequestService service = new ServiceRequestService();
        assertThrows(IllegalArgumentException.class,
                () -> service.update(null));
    }

    @Test
    void update_blankDescription_throwsBeforeDb() {
        ServiceRequestService service = new ServiceRequestService();
        ServiceRequest r = new ServiceRequest(
                1L, 1L, "", 50.0, 60.0, 70.0, 0.0, ServiceRequestState.NEW, LocalDate.now());
        assertThrows(IllegalArgumentException.class,
                () -> service.update(r));
    }
}
