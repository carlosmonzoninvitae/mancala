# Mancala assessment

##Description
This rest application simulates the Mancala (or Kalaha) game. In the next section I will what I understood about 
the assessment requirements and the decisions that I took because of them.

### Requirements
#### I decided to use the Minimum Viable Product or MVP because the most important is the code design and not the game in particular 
As it was a Java Web application assignment but the frontend was not important by the position,
  I made it with **Spring Boot** as a **Restful API**.
The requirements say that there have to be **2 players** playing the same game, so as it doesnt say anything about playing in different computers I prepared the game to work in the **same computer playing by turns**.


###Considerations:

####Readability and comprehensibility of the code 
The code is full of comments and logs to explain the flow and decisions just if the name of the methods are not enough.  Also
I divided the functions into smaller ones to facilitate the understanding of processes.

####Testing your solution
There are 2 kind of tests, one part that verifies the business logic using the **MatchService** to interact with all the operations that you can call using the API.
And the others test the Web Layer (MatchController) with all the type of responses that it could respond due to the possible business rules.
The **coverge** calculates that the 28 Unit Tests cover 94% of the classes and 86% of the lines.

####Conscious design/technical decisions
The design was done using the MVC design pattern taking into account a couple of some considerations:
* DTOs and Models are completely separated by the Service, that means the DTO is used to transport data between the Controller to the Service, and the Model between the Service and the DAO.
* Since the requirements didnt say anything about persistence, I decided to use a Hashtable to save all the matches using the sessionId of the request as Key. That strategy let me work with the minimal but necessary security without using Users and tables.
* As the assessment asked just playing a game and not managing it, all the possible calls to the server are related to start, retrieve, delete and make a move in the match. And all of them have to be with the same sessionId that you used to create it (you can not play in other people games, that's rude!)
* I installed Swagger to let you take a look into the Endpoints and DTOs using this [URL](http://localhost:8080/swagger-ui.html). Don't forget should start the server first, here is some [Documentation](https://swagger.io/specification/v2/) about it.
* I created some custom Exceptions that are later catch by the Controller using a very useless strategy of Spring.


by Carlos Monzón
