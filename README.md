
<img src="https://user-images.githubusercontent.com/65178775/83269009-6a481f80-a201-11ea-8475-0a779375a005.png" width="100%" />

# MKweb
Minwhoan - Kihyeon's web repository

# Our Structure, Plan, Idea
<img src="https://user-images.githubusercontent.com/65178775/81583650-9b94b300-93ec-11ea-8683-c4ffc67215f9.png" width="66%" />

# What is MKWeb?

MkWeb is well created 'Web-Server' framework(even if not created well now, but we are heading to be).

We considered that developers should think lots of things when he/she started to create some services; App, WebSite, science experimental, etc... Simply Front-End, and Back-End.

But actually, the simply service idea is about Front-End like: How about create a food delivery service? People can select the foods what they want to eat, and the page will show information about the food. Furthermore they will order it, and seller will receive about the information what consumer ordered, where he wants to receive, and everything about the information which is about consumer's order.

So the idea started with 'Developers just need to focus on the Front-End, easily View-side.'

However, there are lots of libraries for creating Web-Server, but when developer use it, he/she needs to construct, code, and test about it. It costs time too much, so we thought that 'What about let we solve the web-server part?'

So MKWeb borned.

We are designing our MKWeb with MVC pattern( however we are students and not pertty good at desining it, but we are working hard on how can we follow the pattern. ), and what MKWeb should offer to let developers focusing into there 'Front-End'.

* Model

A server-side resources such as; DbAccessor, FileTransfer, Logger, ...

* Controller

A bridge of Model <---> Views

* Service


# XML Configs
- Definition of Services or Controllers.
The biggest profit when you use MKWeb.

You can access to Model with service or controller configs.

We designed the configs with XML, so developer can easily access to Server resources.

Before using MKWeb, you need to design DBA, DBO, and whatever you need to access to DB, but now you just need to set Config files to access DB.

For example, you just need to set SQL config, and Page config ( We will introduce about it below part or MKWeb Wiki. )

The response is depending on what view requested, however you need to define the request on Configs.

If the request is not defined, then response will call error-page.

# What We Are Preparing

1. RESTful API

2. Secure
