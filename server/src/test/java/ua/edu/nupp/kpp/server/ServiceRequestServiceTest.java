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
    void isAllowedTransition_newToCompleted_returnsFalse() {
        assertFalse(ServiceRequestService.isAllowedTransition(
                ServiceRequestState.NEW, ServiceRequestState.COMPLETED));
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
    void update_nullRequest_throwsBeforeDb() {
        ServiceRequestService service = new ServiceRequestService();
        assertThrows(IllegalArgumentException.class,
                () -> service.update(null));
    }
}
