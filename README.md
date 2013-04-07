Base Spring MVC Web Application
===============================

This project is a very simple base Spring MVC web application that can be used as a starting point to build any Spring MVC application.  It was created to help the initial phase of starting a new project and wiring together the basic elements.  In particular it contains the best practices in a number of areas as follows:

- Servlet Best Practices
  - No web.xml all config using annotation (Servlet 3.0) - _(not yet implemented)_
- Spring Configuration Best Practices
  - JavaConfig configuration (where possible - i.e. everything except Spring Security)
  - Spring Security (with custom login page)
- View / Client Best Practices
  - Hierachical freemarker (to split up / modularise views)
  - CSS at top
  - JS at bottum (loaded asynchronously)
  - Specifying favicon, apple-touch-icon
  - Minified & Bundled JS / CSS - that can be controlled from query string (bundled / unminified / disbaled)
  - Minified HTML
  - Long expiry time for resources (using fingerprint) - _(not fully working yet)_
- Testing Best Practices
  - BDD style approach
  - In-process automated acceptance tests (using new features in Spring 3.2 combined with JSoup)
  - Page Object (encapsulates page interaction)
- Logging Best Practices
  - Logback
  - Symantic logging - _(not yet implemented)_

The intention is that this application is: 
- a simple example 
- a quick to start any Spring MVC web application
- an easy way to follow best practice

To achieve these aims this project will be kept up to date with the latest features on a regular basis.

More information about this application can be found at:
- JavaScript and CSS Minification With WRO4J - http://blog.jamesdbloom.com/JSAndCSSMinificationWithWRO4J.html
- Testing Web Pages In Process - http://blog.jamesdbloom.com/TestingWebPagesInProcess.html
- Using PropertySource & Environment - http://blog.jamesdbloom.com/UsingPropertySourceAndEnvironment.html
