package uk.gov.dwp.uc.pairtest.domain;

public class TicketDetailDto {

  private final int numberOfAdults;
  private final int numChildren;
  private final int numberOfInfants;
  private final int totalTickets;


  public TicketDetailDto(int numberOfAdults, int numChildren, int numberOfInfants,
      int totalTickets) {
    this.numberOfAdults = numberOfAdults;
    this.numChildren = numChildren;
    this.numberOfInfants = numberOfInfants;
    this.totalTickets = totalTickets;
  }

  public int getNumberOfAdults() {
    return numberOfAdults;
  }

  public int getNumChildren() {
    return numChildren;
  }

  public int getNumberOfInfants() {
    return numberOfInfants;
  }

  public int getTotalTickets() {
    return totalTickets;
  }
}
