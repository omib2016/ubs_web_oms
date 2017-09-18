# Project build requirements
1. JDK 1.8.0_40 or higher.
2. Maven 3.2.1 or higher.

# Runtime assumptions
1. The client is very basic & to speed up client side development & end-to-end testing , I have hardcoded a test user with the username "test" in various URI's & HTML/JS files. Therefore when you register a new user , please register a test user by the username "test" for any testing.
2. The Client allows for user registration & logon. Once logged on , the UI allows the user to place an order on a list of available items for a given qty, price & side(Buy or sell). An order once matched becomes a trade. Any open orders on the book & trades can be viewed on the client as well. An order once filled is removed from the book.Any order partially filled goes back on the book with the remaining quantity.
2. You will need 2 users to match 2 orders. The assumption here is that any orders leading to wash trades should not be allowed.Although the implementation
does NOT do a "wash trade check" , it is supported in the design.
3. The implementation supports matches for orders at same price or better price & supports matching for partial quantities as well.
Any remaining quantity then goes back to the book.


# Implementation Notes
1. The source code consists of a matching algorithm which follows the contracts defined by the OrderBookMatcher interface.
2. The matching algorithm is implemented in OrderBookMatcherImpl.
3. All actions on the OrderBook are undertaken by the OrderBookManager which ensures that operations on OrderBook are thread safe.
4. The OrderBookManager interface also defines numerous other contracts like adding bid's/offers, retrieving all bids for a user etc.
5. The OrderBookManagerImpl implements all contracts defined by the OrderBookManager interface.
6. All unit tests demonstrate and assert the functions as defined in the system specification. The tests invoke OrderBookManager API to simulate & assert the system behaviour.
7. The OrderBookManagerImpl leverages Lambdas & Streams API from Java 8 to implement the contracts defined in OrderBookManager interface.
8. Please import the project as a Maven project in IntelliJ when you review the project.

