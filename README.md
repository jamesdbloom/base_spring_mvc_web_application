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

**Upgraded for Spring 4.0.2**

Previous version based on Spring 3.2.1 is still available under branch _spring_3_2_1_version_.

**New Features Added**

- Spring Security (via Java Config)
    - login / logout
    - user registration
    - update password
- JPA Persistence via EBeans
- Email sending (including integration testing via Wiser)
- In-process Multi-Page Top-to-Bottom Integration Testing
- All libraries upgrade (except WRO4J)

**Remaining TODO Items**

- Thymeleaf Templates (for rendering email HTML)
- Replace web.xml with WebApplicationInitializer & WebMvcConfigurerAdapter (therefore removal of last xml files)
- Upgrade WRO4J
- Long expiry time for resources (using fingerprints)
- Symantic logging using MDC (Mapped Diagnostic Context)
