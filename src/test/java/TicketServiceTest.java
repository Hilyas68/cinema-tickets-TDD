import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.TicketService;
import uk.gov.dwp.uc.pairtest.TicketServiceImpl;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

@ExtendWith(MockitoExtension.class)
public class TicketServiceTest {

  @Mock
  private TicketPaymentService paymentService;

  @Mock
  private SeatReservationService reservationService;

  private TicketService ticketService;

  @BeforeEach
  public void setup() {
    ticketService = new TicketServiceImpl(paymentService, reservationService);
  }

  @ParameterizedTest
  @CsvSource({"0", "-1"})
  @DisplayName("Given an invalid account ID then throw an exception")
  public void givenInvalidAccountIdThrowException(long input) {

    InvalidPurchaseException exception = assertThrows(InvalidPurchaseException.class,
        () -> ticketService.purchaseTickets(input));

    assertEquals(exception.getMessage(), "Invalid Account ID", "should return invalid account ID");
  }

  @Test
  @DisplayName("Given a valid accountID and an empty ticket,then no payment and reservation is made")
  public void givenEmptyTicket() {

    ticketService.purchaseTickets(1L);

    verify(paymentService, never()).makePayment(anyLong(), anyInt());
    verify(reservationService, never()).reserveSeat(anyLong(), anyInt());
  }
}
