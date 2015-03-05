#shortener

Shortener is just another URL shortener, written in Scala using Play Framework and Reactive Mongo. 

###How to use it?
To shorten a URL, just perform a `POST` request to `/url` with the following JSON payload:

    { "url": "http://url.to.shorten" }

Upon successful creation, the server will respond with a `201` status code and the `Location` header will contain the shortened URL. If the payload is invalid or an invalid URL is provided, a response with a `400` status code and an empty body is returned.  

When hitting the shortened URL, a redirect with a `301` status code is returned, with the `Location` header that will point the browser to the original URL.

There's also a `/ping` endpoint available, that will simply reply with a `pong` message to ensure the application is up and running.

###Why another URL shortener?
This is just an opportunity for the author to experiment with different tools and frameworks, starting with something really simple. New features may be added from time to time!

###I want to use it. Is it live?
The application is currently deployed at http://shortener.manub.net - have a try.

###You surely don't want a url shortener without a web interface...
You're right. There will be one :)

###This is awesome, I want to add a new feature!
Great!!! Please make a pull request!

###It doesn't work / I don't like your coding style
I'm glad you find a problem with it. Raise an issue or fix it and submit a pull request!

![build status](https://codeship.com/projects/feadae30-a40e-0132-88e8-2e02871ce1a0/status?branch=master)