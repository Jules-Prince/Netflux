# Netflux

Service vod en utilisant java RMI

## Topic

The goal is to program in RMI a client-server application that simulates an online platform that delivers Videos On Demand (video=film).

The client should to be able to signin  (create an account) and login (connect to an account) on the server.

Once the server is launched, you can't restart it for your tests, otherwise clients' credentials would be lost ! BONUS points will be given if you save credentials in a file (server-side) and read that file whenever the server is launched, so you could stop and restart your server without losing data.

The client connects to the VOD platform by submitting its credentials. If they are not recognized at the server side, an Exception is returned.
Managing errors is not the purpose of this lab, but BONUS points will be offered if in the client code, you catch and manage connection exceptions (write a message in the client console and asks the user to re-enter the credentials, both for signin and login).
If recognized by the server, a stub to the private part of the VOD platform is returned.

With this stub, the client can request a list of available videos. We assume videos are all uniquely identified by a String (corresponding for instance
to the ISBN number). The description of the video is an object of a class that contains a string representing the ISBN number (standardized : 13 digits separated by 5 '-' 
characters), and a second string giving the synopsys of the film. The toString() method of this class prints these two strings on the client console. 

On client slide, the user can select a video by its ISBN, and then trigger the streaming of that video by invoking a method on server side for which client provides 
a stub of its "internet box" (for the server to stream back the video by blocks of bytes later on), and of course the ISBN identifying the video. As a return value, the client gets back a bill object including the price (in euros) that must be payed (you do not have to handle the payment, it is out of scope of this lab work)

Then, the server will loop to send (using an RMI method call done on the client "internet box") the x consecutive blocks of bytes that the client video player will have to play. As we simply simulate a real VOD platform, of course, you do not have to send blocks of bytes that are pieces of a real video, nor do you have to really play these blocks by a video
player on client side. This means that you also do not have to wonder in which order the client receives the blocks of bytes (even though in real life
the client video player should make sure that the blocks it plays are played in sequence !) To "see" something, you will send bytes corresponding
to ascii code, eg "abcdef" (first block) "ghijkl" (second block), etc, and on the client internet box (an RMI object), you will simply implement a 
method named void stream([] bytes block) whose role is to invoke System.out.println(block).

BONUS: there are clients for which the server (randomly) decides to freely provide the teaser (bande-annonce) additionally to the film description.
This means that, in this case, the server sends a list of film descriptions that are not only containing the two strings (ISBN, and synopsis), 
but that also contains one block of bytes which is the teaser.  On client-side you must have activated the dynamic class loading mechanism,
in order to be able to transparently download a film description class that is a subclass of the basic film description class, but that has one
additional field which is the teaser. The public method toString() is redefined in this sub class: when invoked by the client, it will not only 
print the synopsis on the console, but it also will trigger the video player to read the teaser (in our case, simply prints also this 3rd field on
the client-side standard output).

You will find in a separate file the RMI interfaces and method signatures you must implement.

## Some informations

Server side
	Connection : (c'est une classe Unicast remote object qui implémente des méthodes définies dans une RMI interface)
		Array clientList;
		xxx Movies;
		boolean signUp (String mail, String pwd) throws SignInFailed; (eg, if a client with this email already exists)
		//to make it a RMI method, you also have to announce RemoteException could be thrown, this means signature is:
			// boolean signUp (String mail, String pwd) throws RemoteException, SignInFailed;
		IVODService login(String mail, String pwd) throws InvalidCredentialsException; (if mail/pwd don't match)
		
	VODService : (c'est une classe Unicast remote object qui implémente ces méthodes définies dans une RMI interface)
		List<MovieDesc> viewCatalog();
		Bill playmovie(String isbn, IClientBox box);
	Bill :
		String movieName;
		BigInteger outrageousPrice; (<-- it may not be that big)
	MovieDesc :
		String movieName;
		String isbn;
		String synopsis;
	
	MovieDescExtended extends MovieDesc :
		[]bytes teaser;
Client side
	ClientBox (c'est un Unicast remote object qui implémente une interface RMI exposant ) :
		void stream([]bytes chunck);
The Flow
	1. Le client s'inscrit (si besoin).
	2. Le client se connecte pour récupérer le service de VOD.
	3. Le client appelle viewCatalog() pour récupérer les descriptions des films. (On suppose que le serveur se lance avec un catalogue
             de films prédéfini, et pour chaque film , une description de la classe MovieDesc). 
	     Pour ceux intéressés par faire le bonus: 
		on suppose que dans le catalogue, (certains) des films disposent également d'une description selon MovieDescExtended. 
	4. Il affiche dans la console la liste des films : pour cela il affiche
           le synopsis, et, si c'est un MovieDescExtended, il joue (automatiquement) la bande-annonce, 
		et propose à l'utilisateur de choisir un film
	5. Quand un film est sélectionné dans la console coté client, le client appelle playMovie(...):
		5a. Le serveur envoie le premier bloc du film, en appellant la méthode stream(...) du client, lui fournissant le début du film (qui
			sera affiché sur la sortie standard coté client).
        	5b. Le serveur lance un thread localement pour continuer à streamer les autres blocs les uns à la suite des autres, appelant stream(...)
		5c. Le serveur renvoie la facture en guise de return de la méthode playMovie.
