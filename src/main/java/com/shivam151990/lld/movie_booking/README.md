## Movie Ticket Booking Application

* Implement a Movie Ticket Booking Application for a Theatre. Refer to the following details:
* A Theatre has Screens that run Shows for different Movies. Each Show has a particular Movie, start time, duration, and is played in a particular Screen in the theatre. Each Screen has an arrangement of Seats that can be booked by Users.
* Assume all Users are registered, authenticated, and logged in to the Application.
* Once a User selects a particular show to book tickets for, a UserBookingSession starts. Within this UserBookingSession, a User will be able to get the Available Seats for the show and select the Seats he wishes to book. It is a ‘good to have’ for the Application to have limits on the number of seats a User can book in a Ticket.
* Once the user has selected a group of seats, these seats should become TEMPORARILY_UNAVAILABLE to all other Users.
* The User then proceeds to make payment which can either be SUCCESS or FAILURE.
* If Payment FAILED, user can retry Payment for a maximum number of times. Beyond maximum retries, the seats are made AVAILABLE.
* If Payment SUCCEEDS, Ticket or Booking Confirmation is generated and made available to the User. The UserBookingSession is closed and the Seats are made PERMANENTLY_UNAVAILABLE.
* A User can also explicitly close the UserBookingSession after selecting seats and before making payment. In this case, the seats selected are made AVAILABLE once again.