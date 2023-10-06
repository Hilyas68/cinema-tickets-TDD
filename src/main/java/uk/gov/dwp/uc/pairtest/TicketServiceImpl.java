package uk.gov.dwp.uc.pairtest;

import java.util.Arrays;
import java.util.Optional;
import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest.Type;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

public class TicketServiceImpl implements TicketService {

  public static final int MIN_ACCOUNT_ID = 1;
  public static final String INVALID_ACCOUNT_ID_MESSAGE = "Invalid Account ID";

  private final TicketPaymentService paymentService;
  private final SeatReservationService reservationService;

  /**
   * Should only have private methods other than the one below.
   */

  public TicketServiceImpl(TicketPaymentService paymentService,
      SeatReservationService reservationService) {
    this.paymentService = paymentService;
    this.reservationService = reservationService;
  }

  @Override
  public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests)
      throws InvalidPurchaseException {

    if (accountId < MIN_ACCOUNT_ID) {
      throw new InvalidPurchaseException(INVALID_ACCOUNT_ID_MESSAGE);
    }

    if (ticketTypeRequests.length > 0) {

      int numberOfAdults = 0;
      int numChildren = 0;
      int numberOfInfants = 0;

      for (TicketTypeRequest request : ticketTypeRequests) {
        switch (request.getTicketType()) {
          case ADULT -> numberOfAdults += request.getNoOfTickets();
          case CHILD -> numChildren += request.getNoOfTickets();
          case INFANT -> numberOfInfants += request.getNoOfTickets();
        }
      }

      if (numberOfAdults == 0) {
        throw new InvalidPurchaseException("Adult ticket is required");
      }

      int totalTickets = numberOfAdults + numChildren + numberOfInfants;
      if (totalTickets > 20) {
        throw new InvalidPurchaseException("Maximum ticket exceeded");
      }

      paymentService.makePayment(accountId, -1);
      reservationService.reserveSeat(accountId, -1);
    }
  }

}
